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
    async actualizarEstado(id, estado, observaciones) {
        const respuesta = await fetch(`${this.baseUrl}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ estado, observaciones })
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
