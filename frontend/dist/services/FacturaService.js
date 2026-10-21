export const CATALOGO_CONDICIONES_IVA = [
    { id: 1, nombre: 'Consumidor Final' },
    { id: 2, nombre: 'Responsable Inscripto' },
    { id: 3, nombre: 'Responsable Monotributo' },
    { id: 4, nombre: 'IVA Sujeto Exento' },
];
export const CATALOGO_PUNTOS_VENTA = [
    { id: 1, nombre: 'Sucursal Centro', detalle: 'N° 1 - Av. San Martín 123' },
    { id: 2, nombre: 'Sucursal Mendoza Shopping', detalle: 'N° 2 - Acceso Este 3280' },
    { id: 3, nombre: 'Ventas Online - Web', detalle: 'N° 3 - Av. España 500' },
];
export const CATALOGO_TIPOS_MONEDA = [
    { id: 1, nombre: 'Pesos Argentinos ($)' },
    { id: 2, nombre: 'Dólar Estadounidense (U$S)' },
    { id: 3, nombre: 'Euros (€)' },
];
export const CATALOGO_CLIENTES = [
    { id: 1, nombre: 'Nicolas Ameni', detalle: 'ID: 1 - CUIT 23447568519' },
    { id: 2, nombre: 'Nicolas Contrera', detalle: 'ID: 2 - CUIT 23445675323' },
    { id: 3, nombre: 'Luciano Cruz', detalle: 'ID: 3 - CUIT 23445445343' },
];
export const PRECIOS_REFERENCIA_ARTICULOS = {
    1: { descripcion: 'Tablet S6 Lite', precio: 785000 },
    2: { descripcion: 'Motorola Edge 70', precio: 647000 },
    3: { descripcion: 'Samsung Galaxy A16', precio: 248000 },
    4: { descripcion: 'Redragon Luce H888', precio: 82000 },
    5: { descripcion: 'Mouse inalambrico Logitech M170', precio: 15000 },
    6: { descripcion: 'Mouse Logitech G203', precio: 39000 },
};
export class FacturaService {
    baseUrl = 'http://localhost:8080/api/facturas';
    async obtenerFacturas(filtros) {
        const url = new URL(this.baseUrl);
        if (filtros) {
            if (filtros.numero !== undefined && filtros.numero !== null) {
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
            if (filtros.condicionIvaId !== undefined && filtros.condicionIvaId !== null) {
                url.searchParams.append('condicionIvaId', filtros.condicionIvaId.toString());
            }
        }
        const respuesta = await fetch(url.toString(), {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        if (!respuesta.ok) {
            throw new Error(`Error ${respuesta.status}: ${respuesta.statusText}`);
        }
        return await respuesta.json();
    }
    async obtenerFacturaPorId(id) {
        const respuesta = await fetch(`${this.baseUrl}/${id}`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        if (!respuesta.ok) {
            throw new Error(`Error ${respuesta.status}: ${respuesta.statusText}`);
        }
        return await respuesta.json();
    }
    async obtenerSiguienteNumero() {
        const respuesta = await fetch(`${this.baseUrl}/siguiente-numero`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        if (!respuesta.ok) {
            throw new Error(`Error al obtener siguiente número: ${respuesta.statusText}`);
        }
        const data = await respuesta.json();
        return data.siguienteNumero;
    }
    async crearFactura(factura) {
        const respuesta = await fetch(this.baseUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(factura)
        });
        if (!respuesta.ok) {
            const errorMsg = await respuesta.text();
            throw new Error(`Error ${respuesta.status}: ${errorMsg || respuesta.statusText}`);
        }
        return await respuesta.json();
    }
    async actualizarFactura(id, factura) {
        const respuesta = await fetch(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(factura)
        });
        if (!respuesta.ok) {
            const errorMsg = await respuesta.text();
            throw new Error(`Error ${respuesta.status}: ${errorMsg || respuesta.statusText}`);
        }
        return await respuesta.json();
    }
    async eliminarFactura(id) {
        const respuesta = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!respuesta.ok && respuesta.status !== 204) {
            const errorMsg = await respuesta.text();
            throw new Error(`Error ${respuesta.status}: ${errorMsg || respuesta.statusText}`);
        }
    }
    async descargarPdf(filtros) {
        const url = new URL(`${this.baseUrl}/pdf`);
        if (filtros) {
            if (filtros.numero !== undefined && filtros.numero !== null) {
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
            if (filtros.condicionIvaId !== undefined && filtros.condicionIvaId !== null) {
                url.searchParams.append('condicionIvaId', filtros.condicionIvaId.toString());
            }
        }
        const respuesta = await fetch(url.toString(), {
            method: 'GET'
        });
        if (!respuesta.ok) {
            throw new Error('No se pudo generar el reporte PDF en el servidor.');
        }
        const blob = await respuesta.blob();
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = `Reporte_Ventas_${new Date().toISOString().slice(0, 10)}.pdf`;
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(link.href);
    }
}
