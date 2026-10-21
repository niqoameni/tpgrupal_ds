import { 
    FacturaService, 
    CATALOGO_CONDICIONES_IVA, 
    CATALOGO_PUNTOS_VENTA, 
    CATALOGO_TIPOS_MONEDA, 
    CATALOGO_CLIENTES,
    PRECIOS_REFERENCIA_ARTICULOS,
    CatalogoItem
} from './services/FacturaService.js';
import { 
    FacturaRequestDTO, 
    FacturaDetalleRequestDTO, 
    FacturaReporteDTO, 
    FacturaFiltrosDTO 
} from './models/factura.dto.js';

const facturaService = new FacturaService();
let detallesEnMemoria: FacturaDetalleRequestDTO[] = [];
let detallesEnEdicion: FacturaDetalleRequestDTO[] = [];

// DOM Filtros
const formFiltros = document.getElementById('form-filtros') as HTMLFormElement;
const btnLimpiarFiltros = document.getElementById('btn-limpiar-filtros') as HTMLButtonElement;
const filtroNumero = document.getElementById('filtro-numero') as HTMLInputElement;
const filtroCliente = document.getElementById('filtro-cliente') as HTMLInputElement;
const filtroFechaDesde = document.getElementById('filtro-fecha-desde') as HTMLInputElement;
const filtroFechaHasta = document.getElementById('filtro-fecha-hasta') as HTMLInputElement;
const filtroCondicionIva = document.getElementById('filtro-condicion-iva') as HTMLInputElement;

// DOM Alta
const formAlta = document.getElementById('form-alta-factura') as HTMLFormElement;
const inputNumeroComprobante = document.getElementById('alta-numero-comprobante') as HTMLInputElement | null;
const inputClienteId = document.getElementById('alta-cliente-id') as HTMLInputElement;
const inputCondicionIvaId = document.getElementById('alta-condicion-iva-id') as HTMLInputElement;
const inputPuntoVentaId = document.getElementById('alta-punto-venta-id') as HTMLInputElement;
const inputTipoMonedaId = document.getElementById('alta-tipo-moneda-id') as HTMLInputElement;
const inputObservaciones = document.getElementById('alta-observaciones') as HTMLInputElement;

// DOM Renglones Alta
const inputItemArticuloId = document.getElementById('item-articulo-id') as HTMLInputElement;
const inputItemDesc = document.getElementById('item-descripcion') as HTMLInputElement;
const inputItemCant = document.getElementById('item-cantidad') as HTMLInputElement;
const inputItemBonif = document.getElementById('item-bonificacion') as HTMLInputElement;
const btnAgregarDetalle = document.getElementById('btn-agregar-detalle') as HTMLButtonElement;

// Tablas y PDF
const tablaDetallesBody = document.getElementById('tabla-detalles-temp-body') as HTMLTableSectionElement;
const tablaFacturasBody = document.getElementById('tabla-facturas-body') as HTMLTableSectionElement;
const btnExportarPdf = document.getElementById('btn-exportar-pdf') as HTMLButtonElement | null;

// Modal Edición
const modalEditar = document.getElementById('modal-editar-factura') as HTMLDivElement;
const modalEditarTitulo = document.getElementById('modal-editar-titulo') as HTMLHeadingElement;
const btnCerrarModal = document.getElementById('btn-cerrar-modal') as HTMLButtonElement;
const btnCancelarModal = document.getElementById('btn-cancelar-modal') as HTMLButtonElement;
const formEditar = document.getElementById('form-editar-factura') as HTMLFormElement;
const editFacturaId = document.getElementById('edit-factura-id') as HTMLInputElement;
const editCliente = document.getElementById('edit-cliente') as HTMLInputElement;
const editCondicionIva = document.getElementById('edit-condicion-iva') as HTMLInputElement;
const editPuntoVenta = document.getElementById('edit-punto-venta') as HTMLInputElement;
const editTotalItemsBadge = document.getElementById('edit-total-items-badge') as HTMLElement;
const editTotalImporteBadge = document.getElementById('edit-total-importe-badge') as HTMLElement;
const editImporteTotal = document.getElementById('edit-importe-total') as HTMLInputElement;

// Elementos de Detalle en Edición
const editItemArticuloId = document.getElementById('edit-item-articulo-id') as HTMLInputElement;
const editItemDescripcion = document.getElementById('edit-item-descripcion') as HTMLInputElement;
const editItemCantidad = document.getElementById('edit-item-cantidad') as HTMLInputElement;
const editItemDescuento = document.getElementById('edit-item-descuento') as HTMLInputElement;
const btnEditAgregarDetalle = document.getElementById('btn-edit-agregar-detalle') as HTMLButtonElement;
const tablaEditDetallesBody = document.getElementById('tabla-edit-detalles-body') as HTMLTableSectionElement;

document.addEventListener('DOMContentLoaded', () => {
    cargarListadoFacturas();
    actualizarSiguienteNumero();

    btnAgregarDetalle?.addEventListener('click', agregarRenglonEnMemoria);
    formAlta?.addEventListener('submit', manejarAltaFactura);
    formAlta?.addEventListener('reset', () => {
        setTimeout(actualizarSiguienteNumero, 50);
    });
    formFiltros?.addEventListener('submit', manejarBusquedaFiltros);
    btnLimpiarFiltros?.addEventListener('click', limpiarFiltros);
    btnExportarPdf?.addEventListener('click', descargarReportePdf);

    // Eventos del modal
    btnCerrarModal?.addEventListener('click', cerrarModalEditar);
    btnCancelarModal?.addEventListener('click', cerrarModalEditar);
    formEditar?.addEventListener('submit', guardarEdicionFactura);
    btnEditAgregarDetalle?.addEventListener('click', agregarRenglonEnEdicion);

    // Autocompletar descripción si selecciona artículo del datalist en Alta
    inputItemArticuloId?.addEventListener('input', () => {
        autocompletarDescripcionArticulo(inputItemArticuloId, inputItemDesc);
    });

    // Autocompletar descripción si selecciona artículo del datalist en Edición
    editItemArticuloId?.addEventListener('input', () => {
        autocompletarDescripcionArticulo(editItemArticuloId, editItemDescripcion);
    });
});

/**
 * Consulta el próximo número de comprobante/ID en la BD y lo refleja en la Sección 2
 */
async function actualizarSiguienteNumero(): Promise<void> {
    if (!inputNumeroComprobante) return;
    try {
        const siguiente = await facturaService.obtenerSiguienteNumero();
        inputNumeroComprobante.value = siguiente.toString();
    } catch (e) {
        console.warn('No se pudo obtener el siguiente número de factura:', e);
    }
}


/**
 * Autocompleta la descripción del artículo a partir del catálogo referencial
 */
function autocompletarDescripcionArticulo(inputArticulo: HTMLInputElement, inputDesc: HTMLInputElement): void {
    const val = inputArticulo.value;
    const matchId = val.match(/^(\d+)/);
    if (matchId) {
        const artId = parseInt(matchId[1], 10);
        const ref = PRECIOS_REFERENCIA_ARTICULOS[artId];
        if (ref && !inputDesc.value.trim()) {
            inputDesc.value = ref.descripcion;
        }
    }
}

/**
 * Resuelve el ID a partir de un valor escrito o seleccionado en el datalist.
 */
function resolverIdCatalogo(valor: string, catalogo: CatalogoItem[], idDefault = 1): number {
    const texto = valor.trim().toLowerCase();
    if (!texto) return idDefault;

    const numDirecto = parseInt(texto, 10);
    if (!isNaN(numDirecto) && catalogo.some(c => c.id === numDirecto)) {
        return numDirecto;
    }

    const coincidencia = catalogo.find(c => c.nombre.toLowerCase() === texto)
                      || catalogo.find(c => c.nombre.toLowerCase().includes(texto));
    if (coincidencia) {
        return coincidencia.id;
    }

    return !isNaN(numDirecto) && numDirecto > 0 ? numDirecto : idDefault;
}

// ==========================================
// 1. GESTIÓN DE RENGLONES EN ALTA (SECCIÓN 2)
// ==========================================

function agregarRenglonEnMemoria(): void {
    const valArticulo = inputItemArticuloId.value.trim();
    const matchId = valArticulo.match(/^(\d+)/);
    const articuloId = matchId ? parseInt(matchId[1], 10) : parseInt(valArticulo, 10);
    const cantidad = parseFloat(inputItemCant.value);
    const descuento = parseFloat(inputItemBonif.value) || 0;
    
    const ref = PRECIOS_REFERENCIA_ARTICULOS[articuloId];
    const descripcion = inputItemDesc.value.trim() 
                     || (ref ? ref.descripcion : `Artículo #${articuloId || 1}`);

    if (isNaN(articuloId) || articuloId <= 0) {
        alert('Debe indicar un artículo válido (puede elegirlo de la lista desplegable).');
        inputItemArticuloId.focus();
        return;
    }
    if (isNaN(cantidad) || cantidad <= 0) {
        alert('La cantidad debe ser mayor a 0.');
        inputItemCant.focus();
        return;
    }

    detallesEnMemoria.push({
        listaPrecioArticuloId: articuloId,
        articuloId: articuloId,
        descripcion: descripcion,
        cantidad: cantidad,
        precioUnitario: ref ? ref.precio : 0,
        porcentajeBonificacion: descuento,
        porcentajeDescuento: descuento
    });

    renderizarTablaDetalles();

    inputItemArticuloId.value = '';
    inputItemDesc.value = '';
    inputItemCant.value = '1';
    inputItemBonif.value = '0';
    inputItemArticuloId.focus();
}

function renderizarTablaDetalles(): void {
    tablaDetallesBody.innerHTML = '';

    if (detallesEnMemoria.length === 0) {
        tablaDetallesBody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-muted">No se agregaron renglones todavía.</td>
            </tr>
        `;
        return;
    }

    detallesEnMemoria.forEach((item, index) => {
        const fila = document.createElement('tr');
        const desc = item.porcentajeDescuento ?? item.porcentajeBonificacion ?? 0;
        fila.innerHTML = `
            <td>#${item.listaPrecioArticuloId}</td>
            <td>${item.descripcion}</td>
            <td>${item.cantidad}</td>
            <td>${desc}%</td>
            <td class="text-center">
                <button type="button" class="btn btn-danger btn-sm" data-idx="${index}">Quitar</button>
            </td>
        `;

        fila.querySelector('button')?.addEventListener('click', () => {
            detallesEnMemoria.splice(index, 1);
            renderizarTablaDetalles();
        });

        tablaDetallesBody.appendChild(fila);
    });
}

async function manejarAltaFactura(e: Event): Promise<void> {
    e.preventDefault();

    if (detallesEnMemoria.length === 0) {
        alert('Debe cargar al menos un renglón antes de registrar la factura.');
        return;
    }

    const clienteId = resolverIdCatalogo(inputClienteId.value, CATALOGO_CLIENTES, 1);
    const condicionIvaId = resolverIdCatalogo(inputCondicionIvaId.value, CATALOGO_CONDICIONES_IVA, 1);
    const puntoVentaId = resolverIdCatalogo(inputPuntoVentaId.value, CATALOGO_PUNTOS_VENTA, 1);
    const tipoMonedaId = resolverIdCatalogo(inputTipoMonedaId.value, CATALOGO_TIPOS_MONEDA, 1);

    const numero = inputNumeroComprobante && inputNumeroComprobante.value
        ? parseInt(inputNumeroComprobante.value, 10)
        : undefined;

    const payload: FacturaRequestDTO = {
        numero: numero,
        clienteId: clienteId,
        condicionIvaId: condicionIvaId,
        puntoVentaId: puntoVentaId,
        tipoMonedaId: tipoMonedaId,
        observaciones: inputObservaciones.value.trim() || undefined,
        detalles: detallesEnMemoria
    };

    try {
        const res = await facturaService.crearFactura(payload);
        const numeroGenerado = res.numeroFactura ?? res.id ?? 'OK';
        alert(`¡Factura N° ${numeroGenerado} registrada con éxito!`);

        formAlta.reset();
        detallesEnMemoria = [];
        renderizarTablaDetalles();
        await cargarListadoFacturas();
        await actualizarSiguienteNumero();
    } catch (error: any) {
        console.error(error);
        alert(`Error al emitir factura: ${error.message}`);
    }
}

// ==========================================
// 2. LISTADO DE FACTURAS (SECCIÓN 3)
// ==========================================

async function cargarListadoFacturas(filtros?: FacturaFiltrosDTO): Promise<void> {
    tablaFacturasBody.innerHTML = `<tr><td colspan="8" class="text-center">Cargando comprobantes...</td></tr>`;

    try {
        const facturas = await facturaService.obtenerFacturas(filtros);
        renderizarTablaFacturas(facturas);
    } catch (error: any) {
        console.error(error);
        tablaFacturasBody.innerHTML = `<tr><td colspan="8" class="text-center" style="color: red;">Error al consultar facturas: ${error.message}</td></tr>`;
    }
}

function renderizarTablaFacturas(facturas: FacturaReporteDTO[]): void {
    tablaFacturasBody.innerHTML = '';

    if (!facturas || facturas.length === 0) {
        tablaFacturasBody.innerHTML = `<tr><td colspan="8" class="text-center text-muted">No se encontraron facturas registradas.</td></tr>`;
        return;
    }

    facturas.forEach(f => {
        const fechaFormateada = f.fechaEmision ? new Date(f.fechaEmision).toLocaleDateString('es-AR') : '-';
        const total = typeof f.importeFinal === 'number' ? f.importeFinal : 0;
        const importeFormateado = `$ ${total.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td><strong>#${f.numeroFactura}</strong></td>
            <td>${fechaFormateada}</td>
            <td>${f.clienteDenominacion || '-'}</td>
            <td>${f.condicionIva || '-'}</td>
            <td>${f.puntoVentaDescripcion || '-'}</td>
            <td>${f.cantidadItems || 0}</td>
            <td><strong>${importeFormateado}</strong></td>
            <td>
                <div class="actions-cell">
                    <button class="btn btn-editar btn-sm">✏️ Editar</button>
                    <button class="btn btn-danger btn-sm btn-eliminar">🗑️ Eliminar</button>
                </div>
            </td>
        `;

        fila.querySelector('.btn-editar')?.addEventListener('click', () => abrirModalEditar(f));
        fila.querySelector('.btn-eliminar')?.addEventListener('click', () => ejecutarEliminarFactura(f.id, f.numeroFactura));

        tablaFacturasBody.appendChild(fila);
    });
}

// ==========================================
// 3. MODAL DE EDICIÓN CON DETALLE DE ARTÍCULOS
// ==========================================

async function abrirModalEditar(facturaResumen: FacturaReporteDTO): Promise<void> {
    try {
        const factura = await facturaService.obtenerFacturaPorId(facturaResumen.id);

        editFacturaId.value = factura.id.toString();
        modalEditarTitulo.innerText = `Editar Factura N° ${factura.numeroFactura}`;
        editCliente.value = factura.clienteDenominacion || '';
        editCondicionIva.value = factura.condicionIva || '';
        editPuntoVenta.value = factura.puntoVentaDescripcion || '';

        if (factura.detalles && factura.detalles.length > 0) {
            detallesEnEdicion = factura.detalles.map(d => {
                const artId = d.articuloId || d.listaPrecioArticuloId || 1;
                const ref = PRECIOS_REFERENCIA_ARTICULOS[artId];
                const unitario = (d.precioUnitario && d.precioUnitario > 0)
                    ? d.precioUnitario
                    : (ref ? ref.precio : 0);
                const desc = d.porcentajeDescuento ?? d.porcentajeBonificacion ?? 0;

                return {
                    listaPrecioArticuloId: d.listaPrecioArticuloId || artId,
                    articuloId: artId,
                    descripcion: d.descripcion || (ref ? ref.descripcion : `Artículo #${artId}`),
                    cantidad: d.cantidad || 1,
                    precioUnitario: unitario,
                    porcentajeBonificacion: desc,
                    porcentajeDescuento: desc
                };
            });
        } else {
            detallesEnEdicion = [];
        }

        renderizarTablaEditDetalles();
        modalEditar.style.display = 'flex';
    } catch (error: any) {
        console.error(error);
        alert(`Error al cargar los datos de la factura: ${error.message}`);
    }
}


function cerrarModalEditar(): void {
    modalEditar.style.display = 'none';
    formEditar.reset();
    detallesEnEdicion = [];
}

function agregarRenglonEnEdicion(): void {
    const valArticulo = editItemArticuloId.value.trim();
    const matchId = valArticulo.match(/^(\d+)/);
    const articuloId = matchId ? parseInt(matchId[1], 10) : parseInt(valArticulo, 10);
    const cantidad = parseFloat(editItemCantidad.value);
    const descuento = parseFloat(editItemDescuento.value) || 0;

    const ref = PRECIOS_REFERENCIA_ARTICULOS[articuloId];
    const descripcion = editItemDescripcion.value.trim() 
                     || (ref ? ref.descripcion : `Artículo #${articuloId || 1}`);

    if (isNaN(articuloId) || articuloId <= 0) {
        alert('Debe indicar un artículo válido para añadir.');
        editItemArticuloId.focus();
        return;
    }
    if (isNaN(cantidad) || cantidad <= 0) {
        alert('La cantidad debe ser mayor a 0.');
        editItemCantidad.focus();
        return;
    }

    detallesEnEdicion.push({
        listaPrecioArticuloId: articuloId,
        articuloId: articuloId,
        descripcion: descripcion,
        cantidad: cantidad,
        precioUnitario: ref ? ref.precio : 0,
        porcentajeBonificacion: descuento,
        porcentajeDescuento: descuento
    });

    renderizarTablaEditDetalles();

    editItemArticuloId.value = '';
    editItemDescripcion.value = '';
    editItemCantidad.value = '1';
    editItemDescuento.value = '0';
    editItemArticuloId.focus();
}

function renderizarTablaEditDetalles(): void {
    tablaEditDetallesBody.innerHTML = '';

    if (detallesEnEdicion.length === 0) {
        tablaEditDetallesBody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted">Esta factura no tiene renglones. Añada al menos un artículo.</td>
            </tr>
        `;
        recalcularTotalesEdicion();
        return;
    }

    detallesEnEdicion.forEach((item, index) => {
        const precioUnit = item.precioUnitario ?? 0;
        const desc = item.porcentajeDescuento ?? item.porcentajeBonificacion ?? 0;
        const subtotal = item.cantidad * precioUnit * (1 - desc / 100);

        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>#${item.listaPrecioArticuloId}</td>
            <td>${item.descripcion}</td>
            <td>${item.cantidad}</td>
            <td>$ ${precioUnit.toLocaleString('es-AR', { minimumFractionDigits: 2 })}</td>
            <td>${desc}%</td>
            <td><strong>$ ${subtotal.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</strong></td>
            <td class="text-center">
                <button type="button" class="btn btn-danger btn-sm btn-quitar-edit" data-idx="${index}">Quitar</button>
            </td>
        `;

        fila.querySelector('.btn-quitar-edit')?.addEventListener('click', () => {
            detallesEnEdicion.splice(index, 1);
            renderizarTablaEditDetalles();
        });

        tablaEditDetallesBody.appendChild(fila);
    });

    recalcularTotalesEdicion();
}

function recalcularTotalesEdicion(): void {
    let cantTotal = 0;
    let impTotal = 0;

    detallesEnEdicion.forEach(item => {
        const precioUnit = item.precioUnitario ?? 0;
        const desc = item.porcentajeDescuento ?? item.porcentajeBonificacion ?? 0;
        const subtotal = item.cantidad * precioUnit * (1 - desc / 100);
        cantTotal += item.cantidad;
        impTotal += subtotal;
    });

    editTotalItemsBadge.innerText = cantTotal.toString();
    editTotalImporteBadge.innerText = `$ ${impTotal.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    editImporteTotal.value = impTotal.toFixed(2);
}

async function guardarEdicionFactura(e: Event): Promise<void> {
    e.preventDefault();
    const id = parseInt(editFacturaId.value, 10);

    const clienteId = resolverIdCatalogo(editCliente.value, CATALOGO_CLIENTES, 1);
    const condicionIvaId = resolverIdCatalogo(editCondicionIva.value, CATALOGO_CONDICIONES_IVA, 1);
    const puntoVentaId = resolverIdCatalogo(editPuntoVenta.value, CATALOGO_PUNTOS_VENTA, 1);

    const payload: FacturaRequestDTO = {
        clienteId: clienteId,
        condicionIvaId: condicionIvaId,
        puntoVentaId: puntoVentaId,
        tipoMonedaId: 1,
        detalles: detallesEnEdicion
    };

    try {
        await facturaService.actualizarFactura(id, payload);
        alert(`Factura actualizada con éxito en el servidor.`);
        cerrarModalEditar();
        await cargarListadoFacturas(obtenerFiltrosActuales());
    } catch (error: any) {
        console.error(error);
        alert(`Error al actualizar la factura: ${error.message}`);
    }
}

async function ejecutarEliminarFactura(id: number, numero: number): Promise<void> {
    if (!confirm(`¿Desea dar de baja la Factura N° ${numero}?`)) return;

    try {
        await facturaService.eliminarFactura(id);
        alert(`Factura N° ${numero} eliminada exitosamente.`);
        await cargarListadoFacturas(obtenerFiltrosActuales());
        await actualizarSiguienteNumero();
    } catch (error: any) {
        console.error(error);
        alert(`Error al eliminar: ${error.message}`);
    }
}

async function manejarBusquedaFiltros(e: Event): Promise<void> {
    e.preventDefault();
    await cargarListadoFacturas(obtenerFiltrosActuales());
}

function limpiarFiltros(): void {
    formFiltros.reset();
    cargarListadoFacturas();
}

function obtenerFiltrosActuales(): FacturaFiltrosDTO {
    const filtros: FacturaFiltrosDTO = {};
    if (filtroNumero.value) filtros.numero = parseInt(filtroNumero.value, 10);
    if (filtroCliente.value.trim()) filtros.cliente = filtroCliente.value.trim();
    if (filtroFechaDesde.value) filtros.fechaDesde = filtroFechaDesde.value;
    if (filtroFechaHasta.value) filtros.fechaHasta = filtroFechaHasta.value;

    if (filtroCondicionIva.value.trim()) {
        const idCond = resolverIdCatalogo(filtroCondicionIva.value, CATALOGO_CONDICIONES_IVA, 0);
        if (idCond > 0) {
            filtros.condicionIvaId = idCond;
        }
    }
    return filtros;
}

async function descargarReportePdf(): Promise<void> {
    try {
        const filtros = obtenerFiltrosActuales();
        await facturaService.descargarPdf(filtros);
    } catch (error: any) {
        console.error(error);
        alert(`Error al generar o descargar el PDF: ${error.message}`);
    }
}