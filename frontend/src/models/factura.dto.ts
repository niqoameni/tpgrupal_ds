// Entrada / Reporte de Factura
export interface FacturaReporteDTO {
    id: number;
    numeroFactura: number;
    fechaEmision: string;
    clienteDenominacion: string;
    clienteCuit?: string;
    condicionIva: string;
    puntoVentaDescripcion: string;
    importeFinal: number;
    cantidadItems: number;
    detalles?: FacturaDetalleRequestDTO[];
}

// Renglón de detalle de factura
export interface FacturaDetalleRequestDTO {
    listaPrecioArticuloId: number;
    articuloId?: number;
    descripcion?: string;
    cantidad: number;
    precioUnitario?: number;
    porcentajeBonificacion?: number; // compatibilidad backend
    porcentajeDescuento?: number;   // nombre preferido en interfaz
    alicuotaIva?: number;
}

// Salida / Alta de Factura
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