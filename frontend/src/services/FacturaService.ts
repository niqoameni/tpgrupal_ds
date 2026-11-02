import { 
    FacturaReporteDTO, 
    FacturaRequestDTO, 
    FacturaFiltrosDTO,
    PageResponse,
    CatalogoItem,
    ListaPrecioArticuloDTO,
    CatalogosResponseDTO,
    ClienteDetalleDTO,
    ClienteRequestDTO,
    ArticuloRequestDTO,
    ArticuloCreadoResponseDTO
} from '../models/factura.dto.js';

export type {
    CatalogoItem,
    ListaPrecioArticuloDTO,
    CatalogosResponseDTO,
    ClienteDetalleDTO,
    ClienteRequestDTO,
    ArticuloRequestDTO,
    ArticuloCreadoResponseDTO
};

export const CATALOGO_CONDICIONES_IVA: CatalogoItem[] = [
    { id: 1, nombre: 'Consumidor Final', detalle: 'Código AFIP: 5' },
    { id: 2, nombre: 'Responsable Inscripto', detalle: 'Código AFIP: 1' },
    { id: 3, nombre: 'Responsable Monotributo', detalle: 'Código AFIP: 6' },
    { id: 4, nombre: 'IVA Sujeto Exento', detalle: 'Código AFIP: 4' },
];

export const CATALOGO_PUNTOS_VENTA: CatalogoItem[] = [
    { id: 1, nombre: 'Sucursal Centro', detalle: 'Punto 1 - Av. San Martin 123' },
    { id: 2, nombre: 'Sucursal de Mendoza Shopping', detalle: 'Punto 2 - Acceso Este 3280' },
    { id: 3, nombre: 'Ventas Online - Web', detalle: 'Punto 3 - Av. España 500' },
];

export const CATALOGO_TIPOS_MONEDA: CatalogoItem[] = [
    { id: 1, nombre: 'Pesos Argentinos ($)', detalle: 'Cód: PES' },
    { id: 2, nombre: 'Dolar Estadounidense (U$S)', detalle: 'Cód: DOL' },
    { id: 3, nombre: 'Euros (€)', detalle: 'Cód: EUR' },
];

export const CATALOGO_CLIENTES: ClienteDetalleDTO[] = [
    { id: 1, nombre: 'Nicolas Ameni', denominacion: 'Nicolas Ameni', cuit: '23447568519', cuitCuil: '23447568519', detalle: 'CUIT: 23447568519', email: 'ameninicolas@gmail.com', telefono: '4450073', celular: '2612414004', nombreCalle: 'Correa Saa', numeroCalle: '1540', domicilio: 'Correa Saa 1540' },
    { id: 2, nombre: 'Nicolas Contrera', denominacion: 'Nicolas Contrera', cuit: '23445675323', cuitCuil: '23445675323', detalle: 'CUIT: 23445675323', celular: '2614443334', domicilio: '' },
    { id: 3, nombre: 'Luciano Cruz', denominacion: 'Luciano Cruz', cuit: '23445445343', cuitCuil: '23445445343', detalle: 'CUIT: 23445445343', email: 'lucianocruz@gmail.com', nombreCalle: 'Allayme', numeroCalle: '2021', domicilio: 'Allayme 2021' },
];

export const CATALOGO_LISTAS_PRECIO: CatalogoItem[] = [
    { id: 1, nombre: 'Minorista - Lista General Minorista', detalle: 'Lista General Minorista' },
    { id: 2, nombre: 'Mayorista - Lista General Mayorista', detalle: 'Lista General Mayorista' },
];

export const CATALOGO_MARCAS: CatalogoItem[] = [
    { id: 1, nombre: 'Samsung', detalle: 'Cód: 1' },
    { id: 2, nombre: 'Motorola', detalle: 'Cód: 2' },
    { id: 3, nombre: 'Redragon', detalle: 'Cód: 3' },
    { id: 4, nombre: 'Logitech', detalle: 'Cód: 4' },
];

export const CATALOGO_RUBROS: CatalogoItem[] = [
    { id: 1, nombre: 'Informatica', detalle: 'Cód: 1' },
    { id: 2, nombre: 'Telefonia Celular', detalle: 'Cód: 2' },
    { id: 3, nombre: 'Accesorios y Perifericos', detalle: 'Cód: 3' },
];

export const CATALOGO_LISTA_PRECIO_ARTICULOS: ListaPrecioArticuloDTO[] = [
    { id: 1, listaPrecioId: 1, listaPrecioDenominacion: 'Lista General Minorista', articuloId: 1, articuloCodigo: 'ART-001', articuloDenominacion: 'Tablet S6 Lite', precioVenta: 785000 },
    { id: 2, listaPrecioId: 2, listaPrecioDenominacion: 'Lista General Mayorista', articuloId: 1, articuloCodigo: 'ART-001', articuloDenominacion: 'Tablet S6 Lite', precioVenta: 685000 },
    { id: 3, listaPrecioId: 1, listaPrecioDenominacion: 'Lista General Minorista', articuloId: 2, articuloCodigo: 'ART-002', articuloDenominacion: 'Motorola Edge 70', precioVenta: 647000 },
    { id: 4, listaPrecioId: 2, listaPrecioDenominacion: 'Lista General Mayorista', articuloId: 2, articuloCodigo: 'ART-002', articuloDenominacion: 'Motorola Edge 70', precioVenta: 590000 },
    { id: 5, listaPrecioId: 1, listaPrecioDenominacion: 'Lista General Minorista', articuloId: 3, articuloCodigo: 'ART-003', articuloDenominacion: 'Samsung Galaxy A16', precioVenta: 248000 },
    { id: 6, listaPrecioId: 2, listaPrecioDenominacion: 'Lista General Mayorista', articuloId: 3, articuloCodigo: 'ART-003', articuloDenominacion: 'Samsung Galaxy A16', precioVenta: 200000 },
    { id: 7, listaPrecioId: 1, listaPrecioDenominacion: 'Lista General Minorista', articuloId: 4, articuloCodigo: 'ART-004', articuloDenominacion: 'Redragon Luce H888', precioVenta: 82000 },
    { id: 8, listaPrecioId: 2, listaPrecioDenominacion: 'Lista General Mayorista', articuloId: 4, articuloCodigo: 'ART-004', articuloDenominacion: 'Redragon Luce H888', precioVenta: 60000 },
    { id: 9, listaPrecioId: 1, listaPrecioDenominacion: 'Lista General Minorista', articuloId: 5, articuloCodigo: 'ART-005', articuloDenominacion: 'Mouse inalambrico Logitech M170', precioVenta: 15000 },
    { id: 10, listaPrecioId: 2, listaPrecioDenominacion: 'Lista General Mayorista', articuloId: 5, articuloCodigo: 'ART-005', articuloDenominacion: 'Mouse inalambrico Logitech M170', precioVenta: 9000 },
    { id: 11, listaPrecioId: 1, listaPrecioDenominacion: 'Lista General Minorista', articuloId: 6, articuloCodigo: 'ART-006', articuloDenominacion: 'Mouse Logitech G203', precioVenta: 39000 },
    { id: 12, listaPrecioId: 2, listaPrecioDenominacion: 'Lista General Mayorista', articuloId: 6, articuloCodigo: 'ART-006', articuloDenominacion: 'Mouse Logitech G203', precioVenta: 30000 },
];

export class FacturaService {
    private readonly baseUrl: string = 'http://localhost:8080/api/facturas';

    async obtenerCatalogos(): Promise<CatalogosResponseDTO> {
        const respuesta = await fetch(`${this.baseUrl}/catalogos`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            throw new Error(`Error al obtener catálogos: ${respuesta.statusText}`);
        }

        return await respuesta.json();
    }

    async buscarClientes(nombre?: string, cuit?: string): Promise<ClienteDetalleDTO[]> {
        const url = new URL(`${this.baseUrl}/clientes`);
        if (nombre && nombre.trim()) url.searchParams.append('nombre', nombre.trim());
        if (cuit && cuit.trim()) url.searchParams.append('cuit', cuit.trim());

        const respuesta = await fetch(url.toString(), {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            throw new Error(`Error al buscar clientes: ${respuesta.statusText}`);
        }

        return await respuesta.json();
    }

    async crearCliente(cliente: ClienteRequestDTO): Promise<ClienteDetalleDTO> {
        const respuesta = await fetch(`${this.baseUrl}/clientes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(cliente)
        });

        if (!respuesta.ok) {
            const errorMsg = await respuesta.text();
            throw new Error(`Error ${respuesta.status}: ${errorMsg || respuesta.statusText}`);
        }

        return await respuesta.json();
    }

    async obtenerSiguienteCodigoArticulo(): Promise<string> {
        const respuesta = await fetch(`${this.baseUrl}/articulos/siguiente-codigo`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            throw new Error(`Error al obtener siguiente código de artículo: ${respuesta.statusText}`);
        }

        const data = await respuesta.json();
        return data.siguienteCodigo;
    }

    async crearArticulo(articulo: ArticuloRequestDTO): Promise<ArticuloCreadoResponseDTO> {
        const respuesta = await fetch(`${this.baseUrl}/articulos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(articulo)
        });

        if (!respuesta.ok) {
            const errorMsg = await respuesta.text();
            throw new Error(`Error ${respuesta.status}: ${errorMsg || respuesta.statusText}`);
        }

        return await respuesta.json();
    }

    async obtenerArticulosPorListaPrecio(listaPrecioId?: number, articulo?: string): Promise<ListaPrecioArticuloDTO[]> {
        const url = new URL(`${this.baseUrl}/lista-precio-articulos`);
        if (listaPrecioId !== undefined && listaPrecioId !== null && listaPrecioId > 0) {
            url.searchParams.append('listaPrecioId', listaPrecioId.toString());
        }
        if (articulo && articulo.trim() !== '') {
            url.searchParams.append('articulo', articulo.trim());
        }

        const respuesta = await fetch(url.toString(), {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            throw new Error(`Error al obtener artículos de la lista de precios: ${respuesta.statusText}`);
        }

        return await respuesta.json();
    }

    async obtenerFacturas(filtros?: FacturaFiltrosDTO): Promise<PageResponse<FacturaReporteDTO>> {
        const url = new URL(this.baseUrl);

        const page = filtros?.page ?? 0;
        const size = filtros?.size ?? 5;
        url.searchParams.append('page', page.toString());
        url.searchParams.append('size', size.toString());

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
            if (filtros.listaPrecioArticuloId !== undefined && filtros.listaPrecioArticuloId !== null) {
                url.searchParams.append('listaPrecioArticuloId', filtros.listaPrecioArticuloId.toString());
            }
        }

        const respuesta = await fetch(url.toString(), {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            throw new Error(`Error ${respuesta.status}: ${respuesta.statusText}`);
        }

        const data = await respuesta.json();

        if (Array.isArray(data)) {
            return {
                content: data,
                totalElements: data.length,
                totalPages: data.length > 0 ? 1 : 0,
                size: size,
                number: 0,
                first: true,
                last: true,
                empty: data.length === 0
            };
        }

        const content: FacturaReporteDTO[] = data.content ?? [];
        const totalElements: number = data.totalElements ?? data.page?.totalElements ?? content.length;
        const totalPages: number = data.totalPages ?? data.page?.totalPages ?? (totalElements > 0 ? Math.ceil(totalElements / size) : 0);
        const currentNumber: number = data.number ?? data.page?.number ?? page;
        const currentSize: number = data.size ?? data.page?.size ?? size;

        return {
            content,
            totalElements,
            totalPages,
            size: currentSize,
            number: currentNumber,
            first: data.first ?? (currentNumber <= 0),
            last: data.last ?? (totalPages === 0 || currentNumber >= totalPages - 1),
            empty: data.empty ?? (content.length === 0)
        };
    }

    async obtenerFacturaPorId(id: number): Promise<FacturaReporteDTO> {
        const respuesta = await fetch(`${this.baseUrl}/${id}`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            throw new Error(`Error ${respuesta.status}: ${respuesta.statusText}`);
        }

        return await respuesta.json();
    }

    async obtenerSiguienteNumero(): Promise<number> {
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

    async crearFactura(factura: FacturaRequestDTO): Promise<FacturaReporteDTO> {
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

    async actualizarFactura(id: number, factura: FacturaRequestDTO): Promise<FacturaReporteDTO> {
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

    async eliminarFactura(id: number): Promise<void> {
        const respuesta = await fetch(`${this.baseUrl}/${id}`, {
            method: 'DELETE'
        });

        if (!respuesta.ok && respuesta.status !== 204) {
            const errorMsg = await respuesta.text();
            throw new Error(`Error ${respuesta.status}: ${errorMsg || respuesta.statusText}`);
        }
    }

    async descargarPdf(filtros?: FacturaFiltrosDTO): Promise<void> {
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
            if (filtros.listaPrecioArticuloId !== undefined && filtros.listaPrecioArticuloId !== null) {
                url.searchParams.append('listaPrecioArticuloId', filtros.listaPrecioArticuloId.toString());
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

    async descargarFacturaIndividualPdf(id: number, numeroFactura?: number): Promise<void> {
        const respuesta = await fetch(`${this.baseUrl}/${id}/pdf`, {
            method: 'GET'
        });

        if (!respuesta.ok) {
            throw new Error('No se pudo generar el PDF de la factura en el servidor.');
        }

        const blob = await respuesta.blob();
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = `Factura_${numeroFactura ?? id}.pdf`;
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(link.href);
    }
}
