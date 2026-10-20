import { FacturaService, CATALOGO_CONDICIONES_IVA, CATALOGO_PUNTOS_VENTA, CATALOGO_TIPOS_MONEDA, CATALOGO_CLIENTES, PRECIOS_REFERENCIA_ARTICULOS } from './services/FacturaService.js';
const facturaService = new FacturaService();
let detallesEnMemoria = [];
let detallesEnEdicion = [];
// DOM Filtros
const formFiltros = document.getElementById('form-filtros');
const btnLimpiarFiltros = document.getElementById('btn-limpiar-filtros');
const filtroNumero = document.getElementById('filtro-numero');
const filtroCliente = document.getElementById('filtro-cliente');
const filtroFechaDesde = document.getElementById('filtro-fecha-desde');
const filtroFechaHasta = document.getElementById('filtro-fecha-hasta');
const filtroCondicionIva = document.getElementById('filtro-condicion-iva');
// DOM Alta
const formAlta = document.getElementById('form-alta-factura');
const inputClienteId = document.getElementById('alta-cliente-id');
const inputCondicionIvaId = document.getElementById('alta-condicion-iva-id');
const inputPuntoVentaId = document.getElementById('alta-punto-venta-id');
const inputTipoMonedaId = document.getElementById('alta-tipo-moneda-id');
const inputObservaciones = document.getElementById('alta-observaciones');
// DOM Renglones Alta
const inputItemArticuloId = document.getElementById('item-articulo-id');
const inputItemDesc = document.getElementById('item-descripcion');
const inputItemCant = document.getElementById('item-cantidad');
const inputItemBonif = document.getElementById('item-bonificacion');
const btnAgregarDetalle = document.getElementById('btn-agregar-detalle');
// Tablas y PDF
const tablaDetallesBody = document.getElementById('tabla-detalles-temp-body');
const tablaFacturasBody = document.getElementById('tabla-facturas-body');
const btnExportarPdf = document.getElementById('btn-exportar-pdf');
// Modal Edición
const modalEditar = document.getElementById('modal-editar-factura');
const modalEditarTitulo = document.getElementById('modal-editar-titulo');
const btnCerrarModal = document.getElementById('btn-cerrar-modal');
const btnCancelarModal = document.getElementById('btn-cancelar-modal');
const formEditar = document.getElementById('form-editar-factura');
const editFacturaId = document.getElementById('edit-factura-id');
const editCliente = document.getElementById('edit-cliente');
const editCondicionIva = document.getElementById('edit-condicion-iva');
const editPuntoVenta = document.getElementById('edit-punto-venta');
const editTotalItemsBadge = document.getElementById('edit-total-items-badge');
const editTotalImporteBadge = document.getElementById('edit-total-importe-badge');
const editImporteTotal = document.getElementById('edit-importe-total');
// Elementos de Detalle en Edición
const editItemArticuloId = document.getElementById('edit-item-articulo-id');
const editItemDescripcion = document.getElementById('edit-item-descripcion');
const editItemCantidad = document.getElementById('edit-item-cantidad');
const editItemDescuento = document.getElementById('edit-item-descuento');
const btnEditAgregarDetalle = document.getElementById('btn-edit-agregar-detalle');
const tablaEditDetallesBody = document.getElementById('tabla-edit-detalles-body');
document.addEventListener('DOMContentLoaded', () => {
    cargarListadoFacturas();
    btnAgregarDetalle?.addEventListener('click', agregarRenglonEnMemoria);
    formAlta?.addEventListener('submit', manejarAltaFactura);
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
 * Autocompleta la descripción del artículo a partir del catálogo referencial
 */
function autocompletarDescripcionArticulo(inputArticulo, inputDesc) {
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
function resolverIdCatalogo(valor, catalogo, idDefault = 1) {
    const texto = valor.trim().toLowerCase();
    if (!texto)
        return idDefault;
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
function agregarRenglonEnMemoria() {
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
function renderizarTablaDetalles() {
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
async function manejarAltaFactura(e) {
    e.preventDefault();
    if (detallesEnMemoria.length === 0) {
        alert('Debe cargar al menos un renglón antes de registrar la factura.');
        return;
    }
    const clienteId = resolverIdCatalogo(inputClienteId.value, CATALOGO_CLIENTES, 1);
    const condicionIvaId = resolverIdCatalogo(inputCondicionIvaId.value, CATALOGO_CONDICIONES_IVA, 1);
    const puntoVentaId = resolverIdCatalogo(inputPuntoVentaId.value, CATALOGO_PUNTOS_VENTA, 1);
    const tipoMonedaId = resolverIdCatalogo(inputTipoMonedaId.value, CATALOGO_TIPOS_MONEDA, 1);
    const payload = {
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
    }
    catch (error) {
        console.error(error);
        alert(`Error al emitir factura: ${error.message}`);
    }
}
// ==========================================
// 2. LISTADO DE FACTURAS (SECCIÓN 3)
// ==========================================
async function cargarListadoFacturas(filtros) {
    tablaFacturasBody.innerHTML = `<tr><td colspan="8" class="text-center">Cargando comprobantes...</td></tr>`;
    try {
        const facturas = await facturaService.obtenerFacturas(filtros);
        renderizarTablaFacturas(facturas);
    }
    catch (error) {
        console.error(error);
        tablaFacturasBody.innerHTML = `<tr><td colspan="8" class="text-center" style="color: red;">Error al consultar facturas: ${error.message}</td></tr>`;
    }
}
function renderizarTablaFacturas(facturas) {
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
function abrirModalEditar(factura) {
    editFacturaId.value = factura.id.toString();
    modalEditarTitulo.innerText = `Editar Factura N° ${factura.numeroFactura}`;
    editCliente.value = factura.clienteDenominacion || '';
    editCondicionIva.value = factura.condicionIva || '';
    editPuntoVenta.value = factura.puntoVentaDescripcion || '';
    if (factura.detalles && factura.detalles.length > 0) {
        detallesEnEdicion = JSON.parse(JSON.stringify(factura.detalles));
    }
    else {
        detallesEnEdicion = [];
    }
    renderizarTablaEditDetalles();
    modalEditar.style.display = 'flex';
}
function cerrarModalEditar() {
    modalEditar.style.display = 'none';
    formEditar.reset();
    detallesEnEdicion = [];
}
function agregarRenglonEnEdicion() {
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
function renderizarTablaEditDetalles() {
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
function recalcularTotalesEdicion() {
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
async function guardarEdicionFactura(e) {
    e.preventDefault();
    const id = parseInt(editFacturaId.value, 10);
    const clienteId = resolverIdCatalogo(editCliente.value, CATALOGO_CLIENTES, 1);
    const condicionIvaId = resolverIdCatalogo(editCondicionIva.value, CATALOGO_CONDICIONES_IVA, 1);
    const puntoVentaId = resolverIdCatalogo(editPuntoVenta.value, CATALOGO_PUNTOS_VENTA, 1);
    const payload = {
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
    }
    catch (error) {
        console.error(error);
        alert(`Error al actualizar la factura: ${error.message}`);
    }
}
async function ejecutarEliminarFactura(id, numero) {
    if (!confirm(`¿Desea dar de baja la Factura N° ${numero}?`))
        return;
    try {
        await facturaService.eliminarFactura(id);
        alert(`Factura N° ${numero} eliminada exitosamente.`);
        await cargarListadoFacturas(obtenerFiltrosActuales());
    }
    catch (error) {
        console.error(error);
        alert(`Error al eliminar: ${error.message}`);
    }
}
async function manejarBusquedaFiltros(e) {
    e.preventDefault();
    await cargarListadoFacturas(obtenerFiltrosActuales());
}
function limpiarFiltros() {
    formFiltros.reset();
    cargarListadoFacturas();
}
function obtenerFiltrosActuales() {
    const filtros = {};
    if (filtroNumero.value)
        filtros.numero = parseInt(filtroNumero.value, 10);
    if (filtroCliente.value.trim())
        filtros.cliente = filtroCliente.value.trim();
    if (filtroFechaDesde.value)
        filtros.fechaDesde = filtroFechaDesde.value;
    if (filtroFechaHasta.value)
        filtros.fechaHasta = filtroFechaHasta.value;
    if (filtroCondicionIva.value.trim()) {
        const idCond = resolverIdCatalogo(filtroCondicionIva.value, CATALOGO_CONDICIONES_IVA, 0);
        if (idCond > 0) {
            filtros.condicionIvaId = idCond;
        }
    }
    return filtros;
}
async function descargarReportePdf() {
    try {
        const filtros = obtenerFiltrosActuales();
        await facturaService.descargarPdf(filtros);
    }
    catch (error) {
        console.error(error);
        alert(`Error al generar o descargar el PDF: ${error.message}`);
    }
}
