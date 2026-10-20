export class FacturaService {
    constructor() {
        this.baseUrl = 'http://localhost:8080/api';
    }
    // Facturas
    async obtenerFacturas(filtros) {
        const url = new URL(`${this.baseUrl}/facturas`);
        if (filtros) {
            if (filtros.numero !== undefined && !isNaN(filtros.numero)) {
                url.searchParams.append('numero', filtros.numero.toString());
            }
            if (filtros.cliente && filtros.cliente.trim() !== '') {
                url.searchParams.append('cliente', filtros.cliente.trim());
            }
            if (filtros.fechaDesde) {
                url.searchParams.append('fechaDesde', filtros.fechaDesde);
            }
            if (filtros.fechaHasta) {
                url.searchParams.append('fechaHasta', filtros.fechaHasta);
            }
            if (filtros.condicionIvaId !== undefined && !isNaN(filtros.condicionIvaId)) {
                url.searchParams.append('condicionIvaId', filtros.condicionIvaId.toString());
            }
        }
        const response = await fetch(url.toString());
        if (!response.ok)
            throw new Error('Error al obtener el listado de facturas');
        return await response.json();
    }
    async obtenerFacturaPorId(id) {
        const response = await fetch(`${this.baseUrl}/facturas/${id}`);
        if (!response.ok)
            throw new Error(`Error al obtener la factura ID: ${id}`);
        return await response.json();
    }
    async crearFactura(factura) {
        const response = await fetch(`${this.baseUrl}/facturas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(factura)
        });
        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(`Error al crear la factura: ${errorMsg}`);
        }
        return await response.json();
    }
    async agregarDetalle(facturaId, detalle) {
        const response = await fetch(`${this.baseUrl}/facturas/${facturaId}/detalles`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(detalle)
        });
        if (!response.ok)
            throw new Error('Error al agregar el artículo a la factura');
        return await response.json();
    }
    async eliminarDetalle(facturaId, detalleId) {
        const response = await fetch(`${this.baseUrl}/facturas/${facturaId}/detalles/${detalleId}`, {
            method: 'DELETE'
        });
        if (!response.ok)
            throw new Error('Error al quitar el artículo de la factura');
        return await response.json();
    }
    async darDeBajaLogica(id, usuario = 'admin') {
        const response = await fetch(`${this.baseUrl}/facturas/${id}?usuario=${encodeURIComponent(usuario)}`, {
            method: 'DELETE'
        });
        if (!response.ok)
            throw new Error('Error al realizar la baja lógica de la factura');
    }
    // Catálogos
    async obtenerClientes() {
        const response = await fetch(`${this.baseUrl}/clientes`);
        if (!response.ok)
            return [];
        return await response.json();
    }
    async obtenerCondicionesIva() {
        const response = await fetch(`${this.baseUrl}/condiciones-iva`);
        if (!response.ok)
            return [];
        return await response.json();
    }
    async obtenerPuntosVenta() {
        const response = await fetch(`${this.baseUrl}/puntos-venta`);
        if (!response.ok)
            return [];
        return await response.json();
    }
    async obtenerTiposMoneda() {
        const response = await fetch(`${this.baseUrl}/tipos-moneda`);
        if (!response.ok)
            return [];
        return await response.json();
    }
    async obtenerArticulos() {
        const response = await fetch(`${this.baseUrl}/articulos`);
        if (!response.ok)
            return [];
        return await response.json();
    }
}
