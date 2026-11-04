// Ítem genérico para desplegables / catálogos
export interface CatalogoItem {
    id: number;
    nombre: string;
    detalle?: string;
    cuit?: string;
    cuitCuil?: string;
    email?: string;
    telefono?: string;
    celular?: string;
    nombreCalle?: string;
    numeroCalle?: string;
    domicilio?: string;
}

// Datos completos de un Cliente para el Explorer y Alta
export interface ClienteDetalleDTO {
    id: number;
    nombre: string;
    denominacion: string;
    cuit: string;
    cuitCuil: string;
    detalle?: string;
    email?: string;
    telefono?: string;
    celular?: string;
    nombreCalle?: string;
    numeroCalle?: string;
    domicilio?: string;
}

// Solicitud para crear un nuevo Cliente
export interface ClienteRequestDTO {
    denominacion: string;
    cuitCuil: string;
    email?: string;
    telefono?: string;
    celular?: string;
    nombreCalle?: string;
    numeroCalle?: string;
    usuario?: string;
}

// Solicitud para crear un nuevo Artículo (y sus 2 ListaPrecioArticulo: Minorista y Mayorista)
export interface ArticuloRequestDTO {
    codigo: string;
    denominacion: string;
    marcaId: number;
    rubroId: number;
    precioMinorista: number;
    precioMayorista: number;
    usuario?: string;
}

// Respuesta al crear un nuevo Artículo
export interface ArticuloCreadoResponseDTO {
    id: number;
    codigo: string;
    denominacion: string;
    marcaId: number;
    marcaDenominacion: string;
    rubroId: number;
    rubroDenominacion: string;
    precioMinorista: number;
    precioMayorista: number;
    lpaMinorista: ListaPrecioArticuloDTO;
    lpaMayorista: ListaPrecioArticuloDTO;
    siguienteCodigoArticulo?: string;
}

// Instancia de ListaPrecioArticulo proveniente del backend
export interface ListaPrecioArticuloDTO {
    id: number; // PK de ListaPrecioArticulo
    precioVenta: number;
    listaPrecioId: number; // FK de ListaPrecio (1 = Minorista, 2 = Mayorista)
    listaPrecioDenominacion: string;
    articuloId: number;
    articuloCodigo?: string;
    articuloDenominacion: string;
}

// Respuesta unificada de catálogos del backend
export interface CatalogosResponseDTO {
    clientes: ClienteDetalleDTO[];
    condicionesIva: CatalogoItem[];
    puntosVenta: CatalogoItem[];
    tiposMoneda: CatalogoItem[];
    listasPrecio: CatalogoItem[];
    marcas?: CatalogoItem[];
    rubros?: CatalogoItem[];
    siguienteCodigoArticulo?: string;
    listaPrecioArticulos: ListaPrecioArticuloDTO[];
}

// Entrada / Reporte de Factura
export interface FacturaReporteDTO {
    id: number;
    numeroFactura: number;
    fechaEmision: string;
    clienteId?: number;
    clienteDenominacion: string;
    clienteCuit?: string;
    condicionIvaId?: number;
    condicionIva: string;
    puntoVentaId?: number;
    puntoVentaDescripcion: string;
    tipoMonedaId?: number;
    tipoMonedaDescripcion?: string;
    listaPrecioId?: number;
    listaPrecioDenominacion?: string;
    observaciones?: string;
    estado?: string;
    cae?: string;
    caeFechaVencimiento?: string;
    resultadoAfip?: string;
    motivoRechazo?: string;
    importeFinal: number;
    cantidadItems: number;
    detalles?: FacturaDetalleRequestDTO[];
}

// Renglón de detalle de factura
export interface FacturaDetalleRequestDTO {
    listaPrecioArticuloId: number;
    listaPrecioId?: number;
    articuloId?: number;
    articuloDenominacion?: string;
    descripcion?: string;
    cantidad: number;
    precioUnitario?: number;
    porcentajeBonificacion?: number; // compatibilidad backend
    porcentajeDescuento?: number;   // nombre preferido en interfaz
    alicuotaIva?: number;
}

// Salida / Alta y Modificación de Factura
export interface FacturaRequestDTO {
    numero?: number;
    clienteId: number;
    condicionIvaId: number;
    puntoVentaId: number;
    tipoMonedaId: number;
    observaciones?: string;
    detalles: FacturaDetalleRequestDTO[];
}

// Filtrado y Paginación
export interface FacturaFiltrosDTO {
    numero?: number;
    cliente?: string;
    fechaDesde?: string;
    fechaHasta?: string;
    condicionIvaId?: number;
    listaPrecioArticuloId?: number;
    page?: number;
    size?: number;
}

// Respuesta paginada (Spring Data Page)
export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}
