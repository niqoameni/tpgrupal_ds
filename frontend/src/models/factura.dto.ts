export interface FacturaDetalleRequestDTO {
    articuloId?: number;
    listaPrecioArticuloId?: number;
    descripcion?: string;
    cantidad: number;
    precioUnitario?: number;
    porcentajeBonificacion?: number;
    porcentajeIva?: number;
}

export interface FacturaDetalleResponseDTO {
    id: number;
    listaPrecioArticuloId?: number;
    articuloId?: number;
    codigoArticulo?: string;
    denominacionArticulo?: string;
    descripcion?: string;
    cantidad: number;
    precioUnitario: number;
    porcentajeBonificacion?: number;
    importeNeto: number;
    importeIva: number;
    importeSubtotal: number;
}

export interface FacturaReporteDTO {
    id: number;
    numeroFactura: number;
    fechaEmision: string;
    clienteDenominacion: string;
    condicionIva: string;
    puntoVentaDescripcion: string;
    importeFinal: number;
    cantidadItems: number;
}

export interface FacturaResponseDTO {
    id: number;
    numero: number;
    fechaEmision: string;
    clienteId: number;
    clienteDenominacion: string;
    clienteCuit: string;
    condicionIvaId: number;
    condicionIvaDenominacion: string;
    puntoVentaId: number;
    puntoVentaDescripcion: string;
    tipoMonedaId: number;
    tipoMonedaDenominacion: string;
    tipoMonedaSimbolo: string;
    importeCobrado: number;
    importeSaldo: number;
    importeTotal: number;
    estado: string;
    observaciones?: string;
    detalles: FacturaDetalleResponseDTO[];
}

export interface FacturaRequestDTO {
    numero?: number;
    fechaEmision?: string;
    clienteId: number;
    condicionIvaId: number;
    puntoVentaId: number;
    tipoMonedaId: number;
    estado?: string;
    observaciones?: string;
    usuario?: string;
    detalles: FacturaDetalleRequestDTO[];
}

export interface FacturaFiltrosDTO {
    numero?: number;
    cliente?: string;
    fechaDesde?: string;
    fechaHasta?: string;
    condicionIvaId?: number;
}

// DTOs de Catálogos auxiliares
export interface ClienteDTO {
    id: number;
    denominacion: string;
    cuitCuil: string;
}

export interface CondicionIvaDTO {
    id: number;
    denominacion: string;
    codigoAfip: number;
}

export interface PuntoVentaDTO {
    id: number;
    numero: number;
    descripcion: string;
}

export interface TipoMonedaDTO {
    id: number;
    denominacion: string;
    simbolo: string;
    codigoAfip: string;
}

export interface ArticuloPrecioDTO {
    listaPrecioArticuloId: number;
    articuloId: number;
    codigo: string;
    denominacion: string;
    marca: string;
    rubro: string;
    precioVenta: number;
    listaPrecioNombre: string;
}