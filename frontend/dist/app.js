import { FacturaService } from './services/FacturaService.js';
const facturaService = new FacturaService();
let detallesEnMemoria = [];
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
// DOM Renglones
const inputItemArticuloId = document.getElementById('item-articulo-id');
const inputItemDesc = document.getElementById('item-descripcion');
const inputItemCant = document.getElementById('item-cantidad');
const inputItemBonif = document.getElementById('item-bonificacion');
const btnAgregarDetalle = document.getElementById('btn-agregar-detalle');
// Tablas y PDF
const tablaDetallesBody = document.getElementById('tabla-detalles-temp-body');
const tablaFacturasBody = document.getElementById('tabla-facturas-body');
const btnExportarPdf = document.getElementById('btn-exportar-pdf');
document.addEventListener('DOMContentLoaded', () => {
    cargarListadoFacturas();
    btnAgregarDetalle?.addEventListener('click', agregarRenglonEnMemoria);
    formAlta?.addEventListener('submit', manejarAltaFactura);
    formFiltros?.addEventListener('submit', manejarBusquedaFiltros);
    btnLimpiarFiltros?.addEventListener('click', limpiarFiltros);
    btnExportarPdf?.addEventListener('click', descargarReportePdf);
});
function agregarRenglonEnMemoria() {
    const articuloId = parseInt(inputItemArticuloId.value);
    const cantidad = parseFloat(inputItemCant.value);
    const bonificacion = parseFloat(inputItemBonif.value) || 0;
    const descripcion = inputItemDesc.value.trim() || `Artículo #${articuloId}`;
    if (isNaN(articuloId) || articuloId <= 0) {
        alert('Debe indicar un ID de artículo / lista válido.');
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
        porcentajeBonificacion: bonificacion
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
        fila.innerHTML = `
            <td>#${item.listaPrecioArticuloId}</td>
            <td>${item.descripcion}</td>
            <td>${item.cantidad}</td>
            <td>${item.porcentajeBonificacion || 0}%</td>
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
    const payload = {
        clienteId: parseInt(inputClienteId.value),
        condicionIvaId: parseInt(inputCondicionIvaId.value),
        puntoVentaId: parseInt(inputPuntoVentaId.value),
        tipoMonedaId: parseInt(inputTipoMonedaId.value),
        observaciones: inputObservaciones.value.trim() || undefined,
        detalles: detallesEnMemoria
    };
    try {
        const res = await facturaService.crearFactura(payload);
        const numeroGenerado = res.numeroFactura ?? res.numero ?? 'OK';
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
                    <button class="btn btn-warning btn-sm btn-anular">Anular</button>
                    <button class="btn btn-danger btn-sm btn-eliminar">Eliminar</button>
                </div>
            </td>
        `;
        fila.querySelector('.btn-anular')?.addEventListener('click', () => ejecutarAnularFactura(f.id, f.numeroFactura));
        fila.querySelector('.btn-eliminar')?.addEventListener('click', () => ejecutarEliminarFactura(f.id, f.numeroFactura));
        tablaFacturasBody.appendChild(fila);
    });
}
async function ejecutarAnularFactura(id, numero) {
    if (!confirm(`¿Está seguro de anular la Factura N° ${numero}?`))
        return;
    try {
        await facturaService.actualizarEstado(id, 'ANULADA', 'Anulada desde panel web');
        alert(`Factura N° ${numero} anulada correctamente.`);
        await cargarListadoFacturas();
    }
    catch (error) {
        console.error(error);
        alert(`Error al anular: ${error.message}`);
    }
}
async function ejecutarEliminarFactura(id, numero) {
    if (!confirm(`¿Desea dar de baja la Factura N° ${numero}?`))
        return;
    try {
        await facturaService.eliminarFactura(id);
        alert(`Factura N° ${numero} eliminada exitosamente.`);
        await cargarListadoFacturas();
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
        filtros.numero = parseInt(filtroNumero.value);
    if (filtroCliente.value.trim())
        filtros.cliente = filtroCliente.value.trim();
    if (filtroFechaDesde.value)
        filtros.fechaDesde = filtroFechaDesde.value;
    if (filtroFechaHasta.value)
        filtros.fechaHasta = filtroFechaHasta.value;
    if (filtroCondicionIva.value)
        filtros.condicionIvaId = parseInt(filtroCondicionIva.value);
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
