// Entrada
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

export interface FacturaDetalleRequestDTO {
    listaPrecioArticuloId: number;
    articuloId?: number;
    descripcion?: string;
    cantidad: number;
    precioUnitario?: number;
    porcentajeBonificacion?: number;
    alicuotaIva?: number;
}

// Salida
export interface FacturaRequestDTO {
    clienteId: number;
    condicionIvaId: number;
    puntoVentaId: number;
    tipoMonedaId: number;
    observaciones?: string;
    detalles: FacturaDetalleRequestDTO[];
}

// Filtrado
export interface FacturaFiltrosDTO {
    numero?: number;
    cliente?: string;
    fechaDesde?: string;
    fechaHasta?: string;
    condicionIvaId?: number;
}