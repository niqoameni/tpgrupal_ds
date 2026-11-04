import { FacturaService, CATALOGO_CONDICIONES_IVA, CATALOGO_PUNTOS_VENTA, CATALOGO_TIPOS_MONEDA, CATALOGO_CLIENTES, CATALOGO_LISTAS_PRECIO, CATALOGO_MARCAS, CATALOGO_RUBROS, CATALOGO_LISTA_PRECIO_ARTICULOS } from './services/FacturaService.js';
const facturaService = new FacturaService();
let detallesEnMemoria = [];
// Catálogos dinámicos (se actualizan desde el backend al iniciar)
let catalogoClientes = [...CATALOGO_CLIENTES];
let catalogoCondicionesIva = [...CATALOGO_CONDICIONES_IVA];
let catalogoPuntosVenta = [...CATALOGO_PUNTOS_VENTA];
let catalogoTiposMoneda = [...CATALOGO_TIPOS_MONEDA];
let catalogoListasPrecio = [...CATALOGO_LISTAS_PRECIO];
let catalogoMarcas = [...CATALOGO_MARCAS];
let catalogoRubros = [...CATALOGO_RUBROS];
let catalogoListaPrecioArticulos = [...CATALOGO_LISTA_PRECIO_ARTICULOS];
let siguienteCodigoArticuloCache = 'ART-007';
// Lista de precio activa en el formulario (1 = Minorista, 2 = Mayorista)
let listaPrecioSeleccionadaId = 1;
// Instancia de ListaPrecioArticulo seleccionada actualmente en el selector de Artículo del alta
let lpaSeleccionadoActual = null;
// ID de la instancia ListaPrecioArticulo seleccionada en el filtro de búsqueda de facturas
let filtroListaPrecioArticuloId = undefined;
// Estado de Paginación
let paginaActual = 0;
let tamanioPagina = 5;
let totalPaginas = 0;
let totalElementos = 0;
/**
 * Normaliza texto quitando acentos y pasando a minúsculas para búsquedas flexibles
 */
function normalizarTexto(texto) {
    return (texto || '')
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase()
        .trim();
}
class SearchableCombobox {
    container;
    input;
    arrowBtn;
    dropdown;
    options = [];
    filteredOptions = [];
    selectedOption = null;
    highlightedIndex = -1;
    onSelectCallback;
    constructor(containerId, onSelect) {
        const el = document.getElementById(containerId);
        if (!el) {
            throw new Error(`No se encontró el contenedor de combobox #${containerId}`);
        }
        this.container = el;
        this.input = el.querySelector('.combobox-input');
        this.arrowBtn = el.querySelector('.combobox-arrow');
        this.dropdown = el.querySelector('.combobox-dropdown');
        this.onSelectCallback = onSelect;
        this.inicializarEventos();
    }
    inicializarEventos() {
        this.input.addEventListener('focus', () => {
            this.abrirDesplegable(false);
        });
        this.input.addEventListener('click', () => {
            if (!this.dropdown.classList.contains('open')) {
                this.abrirDesplegable(false);
            }
        });
        this.input.addEventListener('input', () => {
            this.selectedOption = null;
            this.abrirDesplegable(true);
        });
        this.arrowBtn?.addEventListener('mousedown', (e) => {
            e.preventDefault();
            if (this.dropdown.classList.contains('open')) {
                this.cerrarDesplegable();
            }
            else {
                this.input.focus();
                this.abrirDesplegable(false);
            }
        });
        this.input.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowDown') {
                e.preventDefault();
                if (!this.dropdown.classList.contains('open')) {
                    this.abrirDesplegable(false);
                }
                else if (this.filteredOptions.length > 0) {
                    this.highlightedIndex = (this.highlightedIndex + 1) % this.filteredOptions.length;
                    this.actualizarHighlight();
                }
            }
            else if (e.key === 'ArrowUp') {
                e.preventDefault();
                if (this.dropdown.classList.contains('open') && this.filteredOptions.length > 0) {
                    this.highlightedIndex = (this.highlightedIndex - 1 + this.filteredOptions.length) % this.filteredOptions.length;
                    this.actualizarHighlight();
                }
            }
            else if (e.key === 'Enter') {
                if (this.dropdown.classList.contains('open')) {
                    e.preventDefault();
                    if (this.highlightedIndex >= 0 && this.highlightedIndex < this.filteredOptions.length) {
                        this.seleccionarOpcion(this.filteredOptions[this.highlightedIndex]);
                    }
                    else if (this.filteredOptions.length === 1) {
                        this.seleccionarOpcion(this.filteredOptions[0]);
                    }
                }
            }
            else if (e.key === 'Escape') {
                if (this.dropdown.classList.contains('open')) {
                    e.stopPropagation();
                    this.cerrarDesplegable();
                }
            }
        });
        this.input.addEventListener('blur', () => {
            setTimeout(() => {
                this.cerrarDesplegable();
                this.SincronizarTextoConOpcion();
            }, 150);
        });
    }
    setOptions(options) {
        this.options = options;
        this.filteredOptions = [...options];
        if (this.selectedOption) {
            const actualizada = this.options.find(o => o.id === this.selectedOption.id);
            this.selectedOption = actualizada || null;
        }
    }
    setSelectedById(id, triggerCallback = false) {
        if (id === null || id === undefined) {
            this.clear();
            return;
        }
        const encontrada = this.options.find(o => o.id === id);
        if (encontrada) {
            this.selectedOption = encontrada;
            this.input.value = encontrada.label;
            if (triggerCallback && this.onSelectCallback) {
                this.onSelectCallback(encontrada);
            }
        }
    }
    getSelectedOption() {
        this.SincronizarTextoConOpcion();
        return this.selectedOption;
    }
    getSelectedId(defaultId) {
        const opt = this.getSelectedOption();
        if (opt)
            return opt.id;
        return defaultId;
    }
    clear() {
        this.selectedOption = null;
        this.input.value = '';
        this.highlightedIndex = -1;
        this.cerrarDesplegable();
    }
    SincronizarTextoConOpcion() {
        const texto = normalizarTexto(this.input.value);
        if (!texto) {
            this.selectedOption = null;
            return;
        }
        if (this.selectedOption && normalizarTexto(this.selectedOption.label) === texto) {
            return;
        }
        const exacta = this.options.find(o => normalizarTexto(o.label) === texto);
        if (exacta) {
            this.selectedOption = exacta;
            this.input.value = exacta.label;
            return;
        }
        const parcial = this.options.find(o => normalizarTexto(o.label).includes(texto) || normalizarTexto(o.subtitle || '').includes(texto));
        if (parcial) {
            this.selectedOption = parcial;
            this.input.value = parcial.label;
        }
    }
    abrirDesplegable(filtrarPorInput) {
        const termino = filtrarPorInput ? normalizarTexto(this.input.value) : '';
        if (!termino) {
            this.filteredOptions = [...this.options];
        }
        else {
            this.filteredOptions = this.options.filter(o => normalizarTexto(o.label).includes(termino)
                || normalizarTexto(o.subtitle || '').includes(termino));
        }
        this.renderizarOpciones();
        this.dropdown.classList.add('open');
    }
    cerrarDesplegable() {
        this.dropdown.classList.remove('open');
        this.highlightedIndex = -1;
    }
    renderizarOpciones() {
        this.dropdown.innerHTML = '';
        if (this.filteredOptions.length === 0) {
            const emptyLi = document.createElement('li');
            emptyLi.className = 'combobox-empty';
            emptyLi.innerText = 'No se encontraron coincidencias';
            this.dropdown.appendChild(emptyLi);
            return;
        }
        this.filteredOptions.forEach((opt, idx) => {
            const li = document.createElement('li');
            li.className = 'combobox-option';
            if (this.selectedOption && this.selectedOption.id === opt.id) {
                li.classList.add('selected');
                this.highlightedIndex = idx;
            }
            const titleDiv = document.createElement('div');
            titleDiv.className = 'combobox-option-title';
            titleDiv.innerText = opt.label;
            li.appendChild(titleDiv);
            if (opt.subtitle) {
                const subDiv = document.createElement('div');
                subDiv.className = 'combobox-option-subtitle';
                subDiv.innerText = opt.subtitle;
                li.appendChild(subDiv);
            }
            li.addEventListener('mousedown', (e) => {
                e.preventDefault();
                this.seleccionarOpcion(opt);
            });
            this.dropdown.appendChild(li);
        });
    }
    actualizarHighlight() {
        const items = this.dropdown.querySelectorAll('.combobox-option');
        items.forEach((el, i) => {
            if (i === this.highlightedIndex) {
                el.classList.add('highlighted');
                el.scrollIntoView({ block: 'nearest' });
            }
            else {
                el.classList.remove('highlighted');
            }
        });
    }
    seleccionarOpcion(opt) {
        this.selectedOption = opt;
        this.input.value = opt.label;
        this.cerrarDesplegable();
        if (this.onSelectCallback) {
            this.onSelectCallback(opt);
        }
    }
}
// Instancias de SearchableCombobox
let comboFiltroCondicionIva;
let comboAltaCliente;
let comboAltaCondicionIva;
let comboAltaPuntoVenta;
let comboAltaListaPrecio;
let comboAltaTipoMoneda;
let comboItemArticulo;
let comboArticuloMarca;
let comboArticuloRubro;
// DOM Filtros
const formFiltros = document.getElementById('form-filtros');
const btnLimpiarFiltros = document.getElementById('btn-limpiar-filtros');
const filtroNumero = document.getElementById('filtro-numero');
const filtroCliente = document.getElementById('filtro-cliente');
const btnLimpiarFiltroCliente = document.getElementById('btn-limpiar-filtro-cliente');
const filtroArticulo = document.getElementById('filtro-articulo');
const btnLimpiarFiltroArticulo = document.getElementById('btn-limpiar-filtro-articulo');
const filtroFechaDesde = document.getElementById('filtro-fecha-desde');
const filtroFechaHasta = document.getElementById('filtro-fecha-hasta');
const filtroCondicionIva = document.getElementById('filtro-condicion-iva');
// DOM Grilla y Paginación
const tablaFacturasBody = document.getElementById('tabla-facturas-body');
const btnExportarPdf = document.getElementById('btn-exportar-pdf');
const btnNuevaFactura = document.getElementById('btn-nueva-factura');
const btnNuevaFacturaHeader = document.getElementById('btn-nueva-factura-header');
const btnNuevoClienteHeader = document.getElementById('btn-nuevo-cliente-header');
const btnNuevoArticuloHeader = document.getElementById('btn-nuevo-articulo-header');
const paginacionTexto = document.getElementById('paginacion-texto');
const selectPageSize = document.getElementById('select-page-size');
const btnPageFirst = document.getElementById('btn-page-first');
const btnPagePrev = document.getElementById('btn-page-prev');
const btnPageNext = document.getElementById('btn-page-next');
const btnPageLast = document.getElementById('btn-page-last');
const paginacionNumeros = document.getElementById('paginacion-numeros');
// DOM Modal Unificado (Alta y Modificación de Factura)
const modalFactura = document.getElementById('modal-factura');
const modalFacturaTitulo = document.getElementById('modal-factura-titulo');
const btnCerrarModal = document.getElementById('btn-cerrar-modal');
const btnCancelarModal = document.getElementById('btn-cancelar-modal');
const formFactura = document.getElementById('form-alta-factura');
const inputFacturaId = document.getElementById('factura-id');
const btnSubmitFactura = document.getElementById('btn-submit-factura');
const btnNuevoClienteFactura = document.getElementById('btn-nuevo-cliente-factura');
const btnNuevoArticuloFactura = document.getElementById('btn-nuevo-articulo-factura');
const inputNumeroComprobante = document.getElementById('alta-numero-comprobante');
const inputObservaciones = document.getElementById('alta-observaciones');
// DOM Renglones dentro del formulario unificado
const inputItemArticuloId = document.getElementById('item-articulo-id');
const inputItemDesc = document.getElementById('item-descripcion');
const inputItemCant = document.getElementById('item-cantidad');
const inputItemBonif = document.getElementById('item-bonificacion');
const btnAgregarDetalle = document.getElementById('btn-agregar-detalle');
const tablaDetallesBody = document.getElementById('tabla-detalles-temp-body');
const totalItemsBadge = document.getElementById('total-items-badge');
const totalImporteBadge = document.getElementById('total-importe-badge');
// DOM Modal Explorer de Clientes
const modalExplorerClientes = document.getElementById('modal-explorer-clientes');
const btnCerrarExplorerClientes = document.getElementById('btn-cerrar-explorer-clientes');
const btnCancelarExplorerClientes = document.getElementById('btn-cancelar-explorer-clientes');
const formExplorerClientes = document.getElementById('form-explorer-clientes');
const inputExplorerNombre = document.getElementById('explorer-cliente-nombre');
const inputExplorerCuit = document.getElementById('explorer-cliente-cuit');
const btnLimpiarExplorerClientes = document.getElementById('btn-limpiar-explorer-clientes');
const btnNuevoClienteExplorer = document.getElementById('btn-nuevo-cliente-explorer');
const tablaExplorerClientesBody = document.getElementById('tabla-explorer-clientes-body');
// DOM Modal Explorer de Artículos
const modalExplorerArticulos = document.getElementById('modal-explorer-articulos');
const btnCerrarExplorerArticulos = document.getElementById('btn-cerrar-explorer-articulos');
const btnCancelarExplorerArticulos = document.getElementById('btn-cancelar-explorer-articulos');
const formExplorerArticulos = document.getElementById('form-explorer-articulos');
const selectExplorerArticuloLista = document.getElementById('explorer-articulo-lista');
const inputExplorerArticuloTexto = document.getElementById('explorer-articulo-texto');
const btnLimpiarExplorerArticulos = document.getElementById('btn-limpiar-explorer-articulos');
const btnNuevoArticuloExplorer = document.getElementById('btn-nuevo-articulo-explorer');
const tablaExplorerArticulosBody = document.getElementById('tabla-explorer-articulos-body');
// DOM Modal Nuevo Cliente
const modalNuevoCliente = document.getElementById('modal-nuevo-cliente');
const btnCerrarModalCliente = document.getElementById('btn-cerrar-modal-cliente');
const btnCancelarModalCliente = document.getElementById('btn-cancelar-modal-cliente');
const formNuevoCliente = document.getElementById('form-nuevo-cliente');
const inputClienteDenominacion = document.getElementById('cliente-denominacion');
const inputClienteCuit = document.getElementById('cliente-cuit');
const inputClienteEmail = document.getElementById('cliente-email');
const inputClienteCelular = document.getElementById('cliente-celular');
const inputClienteTelefono = document.getElementById('cliente-telefono');
const inputClienteNombreCalle = document.getElementById('cliente-nombre-calle');
const inputClienteNumeroCalle = document.getElementById('cliente-numero-calle');
// DOM Modal Nuevo Artículo
const modalNuevoArticulo = document.getElementById('modal-nuevo-articulo');
const btnCerrarModalArticulo = document.getElementById('btn-cerrar-modal-articulo');
const btnCancelarModalArticulo = document.getElementById('btn-cancelar-modal-articulo');
const formNuevoArticulo = document.getElementById('form-nuevo-articulo');
const inputArticuloCodigo = document.getElementById('articulo-codigo');
const inputArticuloDenominacion = document.getElementById('articulo-denominacion');
const inputArticuloPrecioMinorista = document.getElementById('articulo-precio-minorista');
const inputArticuloPrecioMayorista = document.getElementById('articulo-precio-mayorista');
document.addEventListener('DOMContentLoaded', async () => {
    inicializarComboboxes();
    await sincronizarCatalogosDesdeServidor();
    await cargarListadoFacturas(0);
    // Eventos de Búsqueda y Filtros
    formFiltros?.addEventListener('submit', manejarBusquedaFiltros);
    btnLimpiarFiltros?.addEventListener('click', limpiarFiltros);
    btnExportarPdf?.addEventListener('click', descargarReportePdf);
    // Eventos del Filtro de Cliente (Explorer)
    filtroCliente?.addEventListener('click', abrirExplorerClientes);
    btnLimpiarFiltroCliente?.addEventListener('click', (e) => {
        e.stopPropagation();
        limpiarSeleccionFiltroCliente();
    });
    // Eventos del Filtro de Artículo (Explorer)
    filtroArticulo?.addEventListener('click', abrirExplorerArticulos);
    btnLimpiarFiltroArticulo?.addEventListener('click', (e) => {
        e.stopPropagation();
        limpiarSeleccionFiltroArticulo();
    });
    // Eventos dentro del Modal Explorer de Clientes
    btnCerrarExplorerClientes?.addEventListener('click', cerrarExplorerClientes);
    btnCancelarExplorerClientes?.addEventListener('click', cerrarExplorerClientes);
    formExplorerClientes?.addEventListener('submit', async (e) => {
        e.preventDefault();
        await ejecutarBusquedaExplorerClientes();
    });
    inputExplorerNombre?.addEventListener('input', () => ejecutarBusquedaExplorerClientes());
    inputExplorerCuit?.addEventListener('input', () => ejecutarBusquedaExplorerClientes());
    btnLimpiarExplorerClientes?.addEventListener('click', async () => {
        formExplorerClientes.reset();
        await ejecutarBusquedaExplorerClientes();
    });
    btnNuevoClienteExplorer?.addEventListener('click', abrirModalNuevoCliente);
    // Eventos dentro del Modal Explorer de Artículos
    btnCerrarExplorerArticulos?.addEventListener('click', cerrarExplorerArticulos);
    btnCancelarExplorerArticulos?.addEventListener('click', cerrarExplorerArticulos);
    formExplorerArticulos?.addEventListener('submit', async (e) => {
        e.preventDefault();
        await ejecutarBusquedaExplorerArticulos();
    });
    selectExplorerArticuloLista?.addEventListener('change', () => ejecutarBusquedaExplorerArticulos());
    inputExplorerArticuloTexto?.addEventListener('input', () => ejecutarBusquedaExplorerArticulos());
    btnLimpiarExplorerArticulos?.addEventListener('click', async () => {
        formExplorerArticulos.reset();
        await ejecutarBusquedaExplorerArticulos();
    });
    btnNuevoArticuloExplorer?.addEventListener('click', abrirModalNuevoArticulo);
    // Eventos para abrir el formulario de Factura en modo Alta
    btnNuevaFactura?.addEventListener('click', abrirFormularioAlta);
    btnNuevaFacturaHeader?.addEventListener('click', abrirFormularioAlta);
    // Eventos para abrir el formulario de Nuevo Cliente
    btnNuevoClienteHeader?.addEventListener('click', abrirModalNuevoCliente);
    btnNuevoClienteFactura?.addEventListener('click', abrirModalNuevoCliente);
    btnCerrarModalCliente?.addEventListener('click', cerrarModalNuevoCliente);
    btnCancelarModalCliente?.addEventListener('click', cerrarModalNuevoCliente);
    formNuevoCliente?.addEventListener('submit', manejarGuardarNuevoCliente);
    // Eventos para abrir el formulario de Nuevo Artículo
    btnNuevoArticuloHeader?.addEventListener('click', abrirModalNuevoArticulo);
    btnNuevoArticuloFactura?.addEventListener('click', abrirModalNuevoArticulo);
    btnCerrarModalArticulo?.addEventListener('click', cerrarModalNuevoArticulo);
    btnCancelarModalArticulo?.addEventListener('click', cerrarModalNuevoArticulo);
    formNuevoArticulo?.addEventListener('submit', manejarGuardarNuevoArticulo);
    // Eventos del Formulario Unificado (Alta / Edición de Factura)
    btnCerrarModal?.addEventListener('click', cerrarModalFactura);
    btnCancelarModal?.addEventListener('click', cerrarModalFactura);
    btnAgregarDetalle?.addEventListener('click', agregarRenglonEnMemoria);
    formFactura?.addEventListener('submit', manejarGuardarFactura);
    // Cerrar modales con tecla Escape (respetando jerarquía de capas)
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            if (modalNuevoCliente && modalNuevoCliente.style.display !== 'none') {
                cerrarModalNuevoCliente();
            }
            else if (modalNuevoArticulo && modalNuevoArticulo.style.display !== 'none') {
                cerrarModalNuevoArticulo();
            }
            else if (modalExplorerClientes && modalExplorerClientes.style.display !== 'none') {
                cerrarExplorerClientes();
            }
            else if (modalExplorerArticulos && modalExplorerArticulos.style.display !== 'none') {
                cerrarExplorerArticulos();
            }
            else if (modalFactura && modalFactura.style.display !== 'none') {
                cerrarModalFactura();
            }
        }
    });
    // Eventos de Paginación
    selectPageSize?.addEventListener('change', () => {
        tamanioPagina = parseInt(selectPageSize.value, 10) || 5;
        cargarListadoFacturas(0);
    });
    btnPageFirst?.addEventListener('click', () => irAPagina(0));
    btnPagePrev?.addEventListener('click', () => irAPagina(paginaActual - 1));
    btnPageNext?.addEventListener('click', () => irAPagina(paginaActual + 1));
    btnPageLast?.addEventListener('click', () => irAPagina(totalPaginas - 1));
});
/**
 * Inicializa todos los desplegables buscables (SearchableCombobox)
 */
function inicializarComboboxes() {
    comboFiltroCondicionIva = new SearchableCombobox('combo-filtro-condicion-iva');
    comboAltaCliente = new SearchableCombobox('combo-alta-cliente');
    comboAltaCondicionIva = new SearchableCombobox('combo-alta-condicion-iva');
    comboAltaPuntoVenta = new SearchableCombobox('combo-alta-punto-venta');
    // Al cambiar entre Minorista (FK=1) y Mayorista (FK=2), se recargan las instancias de ListaPrecioArticulo
    comboAltaListaPrecio = new SearchableCombobox('combo-alta-lista-precio', async (opcion) => {
        await cambiarListaPrecioActiva(opcion.id);
    });
    comboAltaTipoMoneda = new SearchableCombobox('combo-alta-tipo-moneda');
    // Desplegable de Artículos: muestra el nombre del artículo pero guarda el objeto ListaPrecioArticulo
    comboItemArticulo = new SearchableCombobox('combo-item-articulo', (opcion) => {
        lpaSeleccionadoActual = opcion.raw;
        if (!inputItemDesc.value.trim()) {
            inputItemDesc.value = opcion.raw.articuloDenominacion;
        }
    });
    comboArticuloMarca = new SearchableCombobox('combo-articulo-marca');
    comboArticuloRubro = new SearchableCombobox('combo-articulo-rubro');
    poblarOpcionesComboboxes();
}
/**
 * Carga las opciones en los comboboxes a partir de los catálogos en memoria
 */
function poblarOpcionesComboboxes() {
    comboFiltroCondicionIva.setOptions(catalogoCondicionesIva.map(c => ({ id: c.id, label: c.nombre, subtitle: c.detalle, raw: c })));
    comboAltaCliente.setOptions(catalogoClientes.map(c => ({
        id: c.id,
        label: c.denominacion || c.nombre,
        subtitle: c.detalle || `CUIT: ${c.cuitCuil || c.cuit || '-'}`,
        raw: c
    })));
    comboAltaCondicionIva.setOptions(catalogoCondicionesIva.map(c => ({ id: c.id, label: c.nombre, subtitle: c.detalle, raw: c })));
    comboAltaPuntoVenta.setOptions(catalogoPuntosVenta.map(pv => ({ id: pv.id, label: pv.nombre, subtitle: pv.detalle, raw: pv })));
    comboAltaListaPrecio.setOptions(catalogoListasPrecio.map(lp => ({ id: lp.id, label: lp.nombre, subtitle: lp.detalle, raw: lp })));
    comboAltaTipoMoneda.setOptions(catalogoTiposMoneda.map(tm => ({ id: tm.id, label: tm.nombre, subtitle: tm.detalle, raw: tm })));
    comboArticuloMarca.setOptions(catalogoMarcas.map(m => ({ id: m.id, label: m.nombre, subtitle: m.detalle, raw: m })));
    comboArticuloRubro.setOptions(catalogoRubros.map(r => ({ id: r.id, label: r.nombre, subtitle: r.detalle, raw: r })));
    actualizarOpcionesArticulosSegunLista(listaPrecioSeleccionadaId);
}
/**
 * Sincroniza los catálogos reales desde la base de datos
 */
async function sincronizarCatalogosDesdeServidor() {
    try {
        const data = await facturaService.obtenerCatalogos();
        if (data.clientes?.length)
            catalogoClientes = data.clientes;
        if (data.condicionesIva?.length)
            catalogoCondicionesIva = data.condicionesIva;
        if (data.puntosVenta?.length)
            catalogoPuntosVenta = data.puntosVenta;
        if (data.tiposMoneda?.length)
            catalogoTiposMoneda = data.tiposMoneda;
        if (data.listasPrecio?.length)
            catalogoListasPrecio = data.listasPrecio;
        if (data.marcas?.length)
            catalogoMarcas = data.marcas;
        if (data.rubros?.length)
            catalogoRubros = data.rubros;
        if (data.siguienteCodigoArticulo)
            siguienteCodigoArticuloCache = data.siguienteCodigoArticulo;
        if (data.listaPrecioArticulos?.length)
            catalogoListaPrecioArticulos = data.listaPrecioArticulos;
        poblarOpcionesComboboxes();
    }
    catch (e) {
        console.warn('Usando catálogos locales de respaldo:', e);
    }
}
// =========================================================
// EXPLORER DE CLIENTES (BÚSQUEDA POR NOMBRE O CUIT/CUIL)
// =========================================================
async function abrirExplorerClientes() {
    formExplorerClientes?.reset();
    modalExplorerClientes.style.display = 'flex';
    await ejecutarBusquedaExplorerClientes();
    inputExplorerNombre?.focus();
}
function cerrarExplorerClientes() {
    modalExplorerClientes.style.display = 'none';
}
async function ejecutarBusquedaExplorerClientes() {
    const nombre = inputExplorerNombre?.value.trim() || '';
    const cuit = inputExplorerCuit?.value.trim().replace(/[^0-9]/g, '') || '';
    let resultados = [];
    try {
        resultados = await facturaService.buscarClientes(nombre, cuit);
    }
    catch (e) {
        const normNombre = normalizarTexto(nombre);
        resultados = catalogoClientes.filter(c => {
            const nombreCliente = normalizarTexto(c.denominacion || c.nombre);
            const cuitCliente = (c.cuitCuil || c.cuit || '').replace(/[^0-9]/g, '');
            const coincideNombre = !normNombre || nombreCliente.includes(normNombre);
            const coincideCuit = !cuit || cuitCliente.includes(cuit);
            return coincideNombre && coincideCuit;
        });
    }
    renderizarTablaExplorerClientes(resultados);
}
function renderizarTablaExplorerClientes(clientes) {
    tablaExplorerClientesBody.innerHTML = '';
    if (!clientes || clientes.length === 0) {
        tablaExplorerClientesBody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted">
                    No se encontraron clientes con esos criterios.
                </td>
            </tr>
        `;
        return;
    }
    clientes.forEach(c => {
        const nombreMostrar = c.denominacion || c.nombre || '-';
        const cuitMostrar = c.cuitCuil || c.cuit || '-';
        const partesContacto = [];
        if (c.email)
            partesContacto.push(c.email);
        if (c.celular)
            partesContacto.push(`Cel: ${c.celular}`);
        else if (c.telefono)
            partesContacto.push(`Tel: ${c.telefono}`);
        const dir = c.domicilio || `${c.nombreCalle || ''} ${c.numeroCalle || ''}`.trim();
        if (dir)
            partesContacto.push(dir);
        const contactoDomicilio = partesContacto.length > 0 ? partesContacto.join(' | ') : '-';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${nombreMostrar}</strong></td>
            <td>${cuitMostrar}</td>
            <td>${contactoDomicilio}</td>
            <td class="text-center">
                <button type="button" class="btn btn-success btn-sm btn-elegir-cliente">✓ Elegir</button>
            </td>
        `;
        tr.querySelector('.btn-elegir-cliente')?.addEventListener('click', () => {
            seleccionarClienteDesdeExplorer(c);
        });
        tablaExplorerClientesBody.appendChild(tr);
    });
}
function seleccionarClienteDesdeExplorer(cliente) {
    const nombre = cliente.denominacion || cliente.nombre;
    filtroCliente.value = nombre;
    if (btnLimpiarFiltroCliente) {
        btnLimpiarFiltroCliente.style.display = 'inline-block';
    }
    cerrarExplorerClientes();
    cargarListadoFacturas(0);
}
function limpiarSeleccionFiltroCliente() {
    filtroCliente.value = '';
    if (btnLimpiarFiltroCliente) {
        btnLimpiarFiltroCliente.style.display = 'none';
    }
    cargarListadoFacturas(0);
}
// =========================================================
// EXPLORER DE ARTÍCULOS (FILTRAR POR TIPO DE LISTA, ARTÍCULO O AMBOS)
// =========================================================
async function abrirExplorerArticulos() {
    formExplorerArticulos?.reset();
    modalExplorerArticulos.style.display = 'flex';
    await ejecutarBusquedaExplorerArticulos();
    inputExplorerArticuloTexto?.focus();
}
function cerrarExplorerArticulos() {
    modalExplorerArticulos.style.display = 'none';
}
async function ejecutarBusquedaExplorerArticulos() {
    const listaVal = selectExplorerArticuloLista?.value || '';
    const listaPrecioId = listaVal ? parseInt(listaVal, 10) : undefined;
    const textoArticulo = inputExplorerArticuloTexto?.value.trim() || '';
    let resultados = [];
    try {
        resultados = await facturaService.obtenerArticulosPorListaPrecio(listaPrecioId, textoArticulo);
    }
    catch (e) {
        const normTexto = normalizarTexto(textoArticulo);
        resultados = catalogoListaPrecioArticulos.filter(lpa => {
            const coincideLista = !listaPrecioId || lpa.listaPrecioId === listaPrecioId;
            const coincideTexto = !normTexto
                || normalizarTexto(lpa.articuloDenominacion).includes(normTexto)
                || normalizarTexto(lpa.articuloCodigo || '').includes(normTexto);
            return coincideLista && coincideTexto;
        });
    }
    renderizarTablaExplorerArticulos(resultados);
}
function renderizarTablaExplorerArticulos(instancias) {
    tablaExplorerArticulosBody.innerHTML = '';
    if (!instancias || instancias.length === 0) {
        tablaExplorerArticulosBody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center text-muted">
                    No se encontraron instancias de artículos con esos criterios.
                </td>
            </tr>
        `;
        return;
    }
    instancias.forEach(lpa => {
        const codigo = lpa.articuloCodigo || '-';
        const nombre = lpa.articuloDenominacion || '-';
        const tipoLista = lpa.listaPrecioId === 2 ? 'Mayorista' : 'Minorista';
        const precioFormateado = `$ ${lpa.precioVenta.toLocaleString('es-AR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        })}`;
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${codigo}</td>
            <td><strong>${nombre}</strong></td>
            <td><span class="badge-lista">${tipoLista}</span></td>
            <td>${precioFormateado}</td>
            <td class="text-center">
                <button type="button" class="btn btn-success btn-sm btn-elegir-articulo">✓ Elegir</button>
            </td>
        `;
        tr.querySelector('.btn-elegir-articulo')?.addEventListener('click', () => {
            seleccionarArticuloDesdeExplorer(lpa);
        });
        tablaExplorerArticulosBody.appendChild(tr);
    });
}
function seleccionarArticuloDesdeExplorer(lpa) {
    const tipoLista = lpa.listaPrecioId === 2 ? 'Mayorista' : 'Minorista';
    filtroListaPrecioArticuloId = lpa.id;
    filtroArticulo.value = `${lpa.articuloDenominacion} (${tipoLista})`;
    if (btnLimpiarFiltroArticulo) {
        btnLimpiarFiltroArticulo.style.display = 'inline-block';
    }
    cerrarExplorerArticulos();
    cargarListadoFacturas(0);
}
function limpiarSeleccionFiltroArticulo() {
    filtroListaPrecioArticuloId = undefined;
    filtroArticulo.value = '';
    if (btnLimpiarFiltroArticulo) {
        btnLimpiarFiltroArticulo.style.display = 'none';
    }
    cargarListadoFacturas(0);
}
// =========================================================
// MODAL CREAR NUEVO CLIENTE
// =========================================================
function abrirModalNuevoCliente() {
    formNuevoCliente?.reset();
    modalNuevoCliente.style.display = 'flex';
    inputClienteDenominacion?.focus();
}
function cerrarModalNuevoCliente() {
    modalNuevoCliente.style.display = 'none';
    formNuevoCliente?.reset();
}
async function manejarGuardarNuevoCliente(e) {
    e.preventDefault();
    const denominacion = inputClienteDenominacion.value.trim();
    const cuitCuil = inputClienteCuit.value.trim().replace(/[^0-9]/g, '');
    if (!denominacion) {
        alert('Debe ingresar el Nombre / Denominación del cliente.');
        inputClienteDenominacion.focus();
        return;
    }
    if (!cuitCuil) {
        alert('Debe ingresar el CUIT/CUIL del cliente.');
        inputClienteCuit.focus();
        return;
    }
    const payload = {
        denominacion,
        cuitCuil,
        email: inputClienteEmail.value.trim() || undefined,
        celular: inputClienteCelular.value.trim() || undefined,
        telefono: inputClienteTelefono.value.trim() || undefined,
        nombreCalle: inputClienteNombreCalle.value.trim() || undefined,
        numeroCalle: inputClienteNumeroCalle.value.trim() || undefined
    };
    try {
        const clienteCreado = await facturaService.crearCliente(payload);
        // Actualizar catálogo en memoria y comboboxes
        const idx = catalogoClientes.findIndex(c => c.id === clienteCreado.id);
        if (idx >= 0) {
            catalogoClientes[idx] = clienteCreado;
        }
        else {
            catalogoClientes.push(clienteCreado);
        }
        poblarOpcionesComboboxes();
        // Si el formulario de factura está abierto, seleccionar automáticamente el nuevo cliente
        if (modalFactura && modalFactura.style.display !== 'none') {
            comboAltaCliente.setSelectedById(clienteCreado.id, true);
        }
        // Si el Explorer de clientes está abierto, refrescar su tabla
        if (modalExplorerClientes && modalExplorerClientes.style.display !== 'none') {
            await ejecutarBusquedaExplorerClientes();
        }
        alert(`¡Cliente "${clienteCreado.denominacion || clienteCreado.nombre}" creado con éxito!`);
        cerrarModalNuevoCliente();
    }
    catch (error) {
        console.error(error);
        alert(`Error al crear el cliente: ${error.message}`);
    }
}
// =========================================================
// MODAL CREAR NUEVO ARTÍCULO (Y SUS 2 LISTAPRECIOARTICULO)
// =========================================================
function calcularSiguienteCodigoLocal() {
    let maxNum = 0;
    catalogoListaPrecioArticulos.forEach(lpa => {
        const cod = lpa.articuloCodigo || '';
        const match = cod.match(/(\d+)$/);
        if (match) {
            const num = parseInt(match[1], 10);
            if (!isNaN(num) && num > maxNum) {
                maxNum = num;
            }
        }
    });
    return `ART-${String(maxNum + 1).padStart(3, '0')}`;
}
async function abrirModalNuevoArticulo() {
    formNuevoArticulo?.reset();
    comboArticuloMarca?.clear();
    comboArticuloRubro?.clear();
    inputArticuloCodigo.value = siguienteCodigoArticuloCache || calcularSiguienteCodigoLocal();
    modalNuevoArticulo.style.display = 'flex';
    inputArticuloDenominacion?.focus();
    try {
        const codigoServidor = await facturaService.obtenerSiguienteCodigoArticulo();
        if (codigoServidor) {
            siguienteCodigoArticuloCache = codigoServidor;
            inputArticuloCodigo.value = codigoServidor;
        }
    }
    catch (e) {
        inputArticuloCodigo.value = calcularSiguienteCodigoLocal();
    }
}
function cerrarModalNuevoArticulo() {
    modalNuevoArticulo.style.display = 'none';
    formNuevoArticulo?.reset();
}
async function manejarGuardarNuevoArticulo(e) {
    e.preventDefault();
    const codigo = inputArticuloCodigo.value.trim();
    const denominacion = inputArticuloDenominacion.value.trim();
    const marcaId = comboArticuloMarca.getSelectedId();
    const rubroId = comboArticuloRubro.getSelectedId();
    const precioMinorista = parseFloat(inputArticuloPrecioMinorista.value);
    const precioMayorista = parseFloat(inputArticuloPrecioMayorista.value);
    if (!codigo) {
        alert('El código del artículo es obligatorio.');
        return;
    }
    if (!denominacion) {
        alert('El nombre (denominación) del artículo es obligatorio.');
        inputArticuloDenominacion.focus();
        return;
    }
    if (!marcaId) {
        alert('Seleccione una Marca válida de la lista desplegable.');
        return;
    }
    if (!rubroId) {
        alert('Seleccione un Rubro válido de la lista desplegable.');
        return;
    }
    if (isNaN(precioMinorista) || precioMinorista <= 0) {
        alert('El Precio Minorista debe ser un valor mayor a 0.');
        inputArticuloPrecioMinorista.focus();
        return;
    }
    if (isNaN(precioMayorista) || precioMayorista <= 0) {
        alert('El Precio Mayorista debe ser un valor mayor a 0.');
        inputArticuloPrecioMayorista.focus();
        return;
    }
    const payload = {
        codigo,
        denominacion,
        marcaId,
        rubroId,
        precioMinorista,
        precioMayorista
    };
    try {
        const creado = await facturaService.crearArticulo(payload);
        if (creado.lpaMinorista) {
            catalogoListaPrecioArticulos.push(creado.lpaMinorista);
        }
        if (creado.lpaMayorista) {
            catalogoListaPrecioArticulos.push(creado.lpaMayorista);
        }
        if (creado.siguienteCodigoArticulo) {
            siguienteCodigoArticuloCache = creado.siguienteCodigoArticulo;
        }
        else {
            siguienteCodigoArticuloCache = calcularSiguienteCodigoLocal();
        }
        actualizarOpcionesArticulosSegunLista(listaPrecioSeleccionadaId);
        // Si el formulario de factura está abierto, seleccionar automáticamente el nuevo artículo según la lista activa
        if (modalFactura && modalFactura.style.display !== 'none') {
            const lpaParaListaActiva = listaPrecioSeleccionadaId === 2
                ? creado.lpaMayorista
                : creado.lpaMinorista;
            if (lpaParaListaActiva) {
                comboItemArticulo.setSelectedById(lpaParaListaActiva.id, true);
            }
        }
        // Si el Explorer de artículos está abierto, refrescar su tabla
        if (modalExplorerArticulos && modalExplorerArticulos.style.display !== 'none') {
            await ejecutarBusquedaExplorerArticulos();
        }
        alert(`¡Artículo "${creado.denominacion}" (${creado.codigo}) creado con sus precios Minorista y Mayorista!`);
        cerrarModalNuevoArticulo();
    }
    catch (error) {
        console.error(error);
        alert(`Error al crear el artículo: ${error.message}`);
    }
}
/**
 * Cambia la lista de precios activa (1 = Minorista, 2 = Mayorista), consulta las instancias
 * correspondientes de ListaPrecioArticulo en el backend y actualiza tanto el selector de artículos
 * como los renglones ya cargados en el formulario.
 */
async function cambiarListaPrecioActiva(nuevaListaPrecioId) {
    listaPrecioSeleccionadaId = nuevaListaPrecioId;
    try {
        const instancias = await facturaService.obtenerArticulosPorListaPrecio(nuevaListaPrecioId);
        if (instancias && instancias.length > 0) {
            catalogoListaPrecioArticulos = [
                ...catalogoListaPrecioArticulos.filter(x => x.listaPrecioId !== nuevaListaPrecioId),
                ...instancias
            ];
        }
    }
    catch (e) {
        console.warn('Filtrando instancias de ListaPrecioArticulo en memoria:', e);
    }
    actualizarOpcionesArticulosSegunLista(nuevaListaPrecioId);
    lpaSeleccionadoActual = null;
    comboItemArticulo.clear();
    inputItemDesc.value = '';
    if (detallesEnMemoria.length > 0) {
        const instanciasDeLista = obtenerInstanciasLpaPorLista(nuevaListaPrecioId);
        detallesEnMemoria = detallesEnMemoria.map(det => {
            const lpaEquivalente = instanciasDeLista.find(x => x.articuloId === det.articuloId);
            if (lpaEquivalente) {
                return {
                    ...det,
                    listaPrecioArticuloId: lpaEquivalente.id,
                    listaPrecioId: lpaEquivalente.listaPrecioId,
                    articuloId: lpaEquivalente.articuloId,
                    articuloDenominacion: lpaEquivalente.articuloDenominacion,
                    precioUnitario: lpaEquivalente.precioVenta
                };
            }
            return det;
        });
        renderizarTablaDetalles();
    }
}
function obtenerInstanciasLpaPorLista(listaPrecioId) {
    return catalogoListaPrecioArticulos.filter(lpa => lpa.listaPrecioId === listaPrecioId);
}
/**
 * Actualiza el desplegable de Artículo con las instancias de ListaPrecioArticulo que pertenecen
 * a la lista de precios indicada (1 = Minorista, 2 = Mayorista).
 * En el desplegable se muestra únicamente el nombre del artículo y su precio, sin mostrar IDs internos ni FKs.
 */
function actualizarOpcionesArticulosSegunLista(listaPrecioId) {
    const instancias = obtenerInstanciasLpaPorLista(listaPrecioId);
    const tipoTexto = listaPrecioId === 2 ? 'Mayorista' : 'Minorista';
    comboItemArticulo.setOptions(instancias.map(lpa => {
        const precioFormateado = lpa.precioVenta.toLocaleString('es-AR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
        return {
            id: lpa.id,
            label: lpa.articuloDenominacion,
            subtitle: `Precio ${tipoTexto}: $ ${precioFormateado}`,
            raw: lpa
        };
    }));
}
/**
 * Consulta el próximo número de comprobante/ID en la BD y lo refleja en el formulario
 */
async function actualizarSiguienteNumero() {
    if (!inputNumeroComprobante)
        return;
    try {
        const siguiente = await facturaService.obtenerSiguienteNumero();
        inputNumeroComprobante.value = siguiente.toString();
    }
    catch (e) {
        console.warn('No se pudo obtener el siguiente número de factura:', e);
    }
}
// =========================================================
// 1. FORMULARIO UNIFICADO DE ALTA Y MODIFICACIÓN DE FACTURA
// =========================================================
async function abrirFormularioAlta() {
    formFactura.reset();
    inputFacturaId.value = '';
    detallesEnMemoria = [];
    lpaSeleccionadoActual = null;
    comboAltaCliente.clear();
    comboAltaCondicionIva.clear();
    comboAltaPuntoVenta.clear();
    comboItemArticulo.clear();
    // Por defecto: Precio Lista = Minorista (FK = 1) y Tipo de Moneda = Pesos Argentinos (ID = 1)
    comboAltaListaPrecio.setSelectedById(1, false);
    await cambiarListaPrecioActiva(1);
    comboAltaTipoMoneda.setSelectedById(1, false);
    modalFacturaTitulo.innerText = 'Alta de Factura de Venta';
    btnSubmitFactura.innerText = 'Guardar y Emitir Factura';
    btnSubmitFactura.classList.remove('btn-primary');
    btnSubmitFactura.classList.add('btn-success');
    if (inputNumeroComprobante) {
        inputNumeroComprobante.value = '';
        inputNumeroComprobante.placeholder = 'Cargando N°...';
    }
    renderizarTablaDetalles();
    modalFactura.style.display = 'flex';
    await actualizarSiguienteNumero();
}
async function abrirFormularioEdicion(facturaResumen) {
    try {
        const factura = await facturaService.obtenerFacturaPorId(facturaResumen.id);
        formFactura.reset();
        inputFacturaId.value = factura.id.toString();
        lpaSeleccionadoActual = null;
        modalFacturaTitulo.innerText = `Editar Factura N° ${factura.numeroFactura}`;
        btnSubmitFactura.innerText = 'Guardar Cambios de Factura';
        btnSubmitFactura.classList.remove('btn-success');
        btnSubmitFactura.classList.add('btn-primary');
        if (inputNumeroComprobante) {
            inputNumeroComprobante.value = (factura.numeroFactura ?? factura.id).toString();
        }
        const listaIdFactura = factura.listaPrecioId
            ?? factura.detalles?.[0]?.listaPrecioId
            ?? 1;
        comboAltaListaPrecio.setSelectedById(listaIdFactura, false);
        await cambiarListaPrecioActiva(listaIdFactura);
        comboAltaCliente.setSelectedById(factura.clienteId ?? 1, false);
        comboAltaCondicionIva.setSelectedById(factura.condicionIvaId ?? 1, false);
        comboAltaPuntoVenta.setSelectedById(factura.puntoVentaId ?? 1, false);
        comboAltaTipoMoneda.setSelectedById(factura.tipoMonedaId ?? 1, false);
        inputObservaciones.value = factura.observaciones || '';
        if (factura.detalles && factura.detalles.length > 0) {
            detallesEnMemoria = factura.detalles.map(d => {
                const lpaId = d.listaPrecioArticuloId;
                const lpaEnCatalogo = catalogoListaPrecioArticulos.find(x => x.id === lpaId)
                    || catalogoListaPrecioArticulos.find(x => x.articuloId === d.articuloId && x.listaPrecioId === (d.listaPrecioId ?? listaIdFactura));
                const unitario = (d.precioUnitario && d.precioUnitario > 0)
                    ? d.precioUnitario
                    : (lpaEnCatalogo ? lpaEnCatalogo.precioVenta : 0);
                const desc = d.porcentajeDescuento ?? d.porcentajeBonificacion ?? 0;
                const artNombre = d.articuloDenominacion
                    || lpaEnCatalogo?.articuloDenominacion
                    || d.descripcion
                    || 'Artículo';
                return {
                    listaPrecioArticuloId: lpaEnCatalogo ? lpaEnCatalogo.id : lpaId,
                    listaPrecioId: d.listaPrecioId ?? lpaEnCatalogo?.listaPrecioId ?? listaIdFactura,
                    articuloId: d.articuloId ?? lpaEnCatalogo?.articuloId,
                    articuloDenominacion: artNombre,
                    descripcion: d.descripcion || artNombre,
                    cantidad: d.cantidad || 1,
                    precioUnitario: unitario,
                    porcentajeBonificacion: desc,
                    porcentajeDescuento: desc
                };
            });
        }
        else {
            detallesEnMemoria = [];
        }
        renderizarTablaDetalles();
        modalFactura.style.display = 'flex';
    }
    catch (error) {
        console.error(error);
        alert(`Error al cargar los datos de la factura: ${error.message}`);
    }
}
function cerrarModalFactura() {
    modalFactura.style.display = 'none';
    formFactura.reset();
    inputFacturaId.value = '';
    detallesEnMemoria = [];
    lpaSeleccionadoActual = null;
    renderizarTablaDetalles();
}
function agregarRenglonEnMemoria() {
    const opcionSeleccionada = comboItemArticulo.getSelectedOption();
    const lpa = opcionSeleccionada ? opcionSeleccionada.raw : lpaSeleccionadoActual;
    if (!lpa) {
        alert('Debe seleccionar un artículo válido de la lista desplegable.');
        inputItemArticuloId.focus();
        return;
    }
    const cantidad = parseFloat(inputItemCant.value);
    if (isNaN(cantidad) || cantidad < 1) {
        alert('La cantidad no puede ser menor a 1.');
        inputItemCant.focus();
        return;
    }
    const textoBonif = inputItemBonif.value.trim();
    const descuento = textoBonif === '' ? 0 : parseFloat(textoBonif);
    if (isNaN(descuento) || descuento < 0 || descuento > 99) {
        alert('El porcentaje de descuento no puede ser menor que 0 ni mayor a 99.');
        inputItemBonif.focus();
        return;
    }
    const descripcion = inputItemDesc.value.trim() || lpa.articuloDenominacion;
    detallesEnMemoria.push({
        listaPrecioArticuloId: lpa.id,
        listaPrecioId: lpa.listaPrecioId,
        articuloId: lpa.articuloId,
        articuloDenominacion: lpa.articuloDenominacion,
        descripcion: descripcion,
        cantidad: cantidad,
        precioUnitario: lpa.precioVenta,
        porcentajeBonificacion: descuento,
        porcentajeDescuento: descuento
    });
    renderizarTablaDetalles();
    lpaSeleccionadoActual = null;
    comboItemArticulo.clear();
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
                <td colspan="7" class="text-center text-muted">No se agregaron renglones todavía.</td>
            </tr>
        `;
        recalcularTotalesFormulario();
        return;
    }
    detallesEnMemoria.forEach((item, index) => {
        const precioUnit = item.precioUnitario ?? 0;
        const desc = item.porcentajeDescuento ?? item.porcentajeBonificacion ?? 0;
        const neto = item.cantidad * precioUnit * (1 - desc / 100);
        const subtotalConIva = neto * 1.21;
        const nombreArt = item.articuloDenominacion || item.descripcion || 'Artículo';
        const tipoListaBadge = item.listaPrecioId === 2 ? 'Mayorista' : 'Minorista';
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>
                <strong>${nombreArt}</strong>
                <span class="badge-lista">${tipoListaBadge}</span>
            </td>
            <td>${item.descripcion || '-'}</td>
            <td>${item.cantidad}</td>
            <td>$ ${precioUnit.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</td>
            <td>${desc}%</td>
            <td><strong>$ ${subtotalConIva.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</strong></td>
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
    recalcularTotalesFormulario();
}
function recalcularTotalesFormulario() {
    let cantTotal = 0;
    let impTotal = 0;
    detallesEnMemoria.forEach(item => {
        const precioUnit = item.precioUnitario ?? 0;
        const desc = item.porcentajeDescuento ?? item.porcentajeBonificacion ?? 0;
        const neto = item.cantidad * precioUnit * (1 - desc / 100);
        const subtotalConIva = neto * 1.21;
        cantTotal += item.cantidad;
        impTotal += subtotalConIva;
    });
    if (totalItemsBadge) {
        totalItemsBadge.innerText = cantTotal.toString();
    }
    if (totalImporteBadge) {
        totalImporteBadge.innerText = `$ ${impTotal.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    }
}
async function manejarGuardarFactura(e) {
    e.preventDefault();
    if (detallesEnMemoria.length === 0) {
        alert('Debe cargar al menos un renglón antes de guardar la factura.');
        return;
    }
    const clienteId = comboAltaCliente.getSelectedId();
    const condicionIvaId = comboAltaCondicionIva.getSelectedId();
    const puntoVentaId = comboAltaPuntoVenta.getSelectedId();
    const tipoMonedaId = comboAltaTipoMoneda.getSelectedId(1) ?? 1;
    if (!clienteId) {
        alert('Seleccione un Cliente válido de la lista desplegable.');
        return;
    }
    if (!condicionIvaId) {
        alert('Seleccione una Condición de IVA válida de la lista desplegable.');
        return;
    }
    if (!puntoVentaId) {
        alert('Seleccione un Punto de Venta válido de la lista desplegable.');
        return;
    }
    const idEdicion = inputFacturaId.value ? parseInt(inputFacturaId.value, 10) : null;
    const esEdicion = idEdicion !== null && !isNaN(idEdicion) && idEdicion > 0;
    const numero = inputNumeroComprobante && inputNumeroComprobante.value
        ? parseInt(inputNumeroComprobante.value, 10)
        : undefined;
    const payload = {
        numero: numero,
        clienteId: clienteId,
        condicionIvaId: condicionIvaId,
        puntoVentaId: puntoVentaId,
        tipoMonedaId: tipoMonedaId,
        observaciones: inputObservaciones.value.trim(),
        detalles: detallesEnMemoria
    };
    try {
        if (esEdicion) {
            const res = await facturaService.actualizarFactura(idEdicion, payload);
            const nro = res.numeroFactura ?? numero ?? idEdicion;
            alert(`¡Factura N° ${nro} actualizada con éxito!`);
            cerrarModalFactura();
            await cargarListadoFacturas(paginaActual);
        }
        else {
            const res = await facturaService.crearFactura(payload);
            const numeroGenerado = res.numeroFactura ?? res.id ?? 'OK';
            const infoCae = res.cae ? `\nCAE ARCA/AFIP: ${res.cae}` : '';
            alert(`¡Factura N° ${numeroGenerado} registrada con éxito!${infoCae}`);
            cerrarModalFactura();
            await cargarListadoFacturas(0);
        }
    }
    catch (error) {
        console.error(error);
        alert(`Error al guardar la factura: ${error.message}`);
    }
}
// =========================================================
// 2. LISTADO PAGINADO DE FACTURAS Y BÚSQUEDA
// =========================================================
async function cargarListadoFacturas(pagina = paginaActual) {
    tablaFacturasBody.innerHTML = `<tr><td colspan="8" class="text-center">Cargando comprobantes...</td></tr>`;
    const filtros = obtenerFiltrosActuales();
    filtros.page = Math.max(0, pagina);
    filtros.size = tamanioPagina;
    try {
        const respuestaPagina = await facturaService.obtenerFacturas(filtros);
        if (respuestaPagina.content.length === 0 && respuestaPagina.totalElements > 0 && filtros.page > 0) {
            await cargarListadoFacturas(filtros.page - 1);
            return;
        }
        paginaActual = respuestaPagina.number;
        totalPaginas = respuestaPagina.totalPages;
        totalElementos = respuestaPagina.totalElements;
        renderizarTablaFacturas(respuestaPagina.content);
        renderizarControlesPaginacion(respuestaPagina);
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
        const badgeCae = f.cae
            ? `<div style="margin-top:3px;"><span class="badge-lista" title="CAE autorizado en ARCA/AFIP">CAE: ${f.cae}</span></div>`
            : '';
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td><strong>#${f.numeroFactura}</strong>${badgeCae}</td>
            <td>${fechaFormateada}</td>
            <td>${f.clienteDenominacion || '-'}</td>
            <td>${f.condicionIva || '-'}</td>
            <td>${f.puntoVentaDescripcion || '-'}</td>
            <td>${f.cantidadItems || 0}</td>
            <td><strong>${importeFormateado}</strong></td>
            <td>
                <div class="actions-cell">
                    <button type="button" class="btn btn-outline btn-sm btn-pdf-factura" title="Imprimir factura en PDF">📄</button>
                    <button type="button" class="btn btn-editar btn-sm">✏️ Editar</button>
                    <button type="button" class="btn btn-danger btn-sm btn-eliminar">🗑️ Eliminar</button>
                </div>
            </td>
        `;
        fila.querySelector('.btn-pdf-factura')?.addEventListener('click', () => descargarPdfIndividual(f.id, f.numeroFactura));
        fila.querySelector('.btn-editar')?.addEventListener('click', () => abrirFormularioEdicion(f));
        fila.querySelector('.btn-eliminar')?.addEventListener('click', () => ejecutarEliminarFactura(f.id, f.numeroFactura));
        tablaFacturasBody.appendChild(fila);
    });
}
function renderizarControlesPaginacion(pageData) {
    const { number: currentPage, size, totalElements, totalPages, content } = pageData;
    if (paginacionTexto) {
        if (totalElements === 0 || content.length === 0) {
            paginacionTexto.innerText = 'Mostrando 0 de 0 comprobantes';
        }
        else {
            const desde = currentPage * size + 1;
            const hasta = desde + content.length - 1;
            paginacionTexto.innerText = `Mostrando ${desde} - ${hasta} de ${totalElements} comprobantes (Página ${currentPage + 1} de ${Math.max(1, totalPages)})`;
        }
    }
    const esPrimera = currentPage <= 0;
    const esUltima = totalPages === 0 || currentPage >= totalPages - 1;
    if (btnPageFirst)
        btnPageFirst.disabled = esPrimera;
    if (btnPagePrev)
        btnPagePrev.disabled = esPrimera;
    if (btnPageNext)
        btnPageNext.disabled = esUltima;
    if (btnPageLast)
        btnPageLast.disabled = esUltima;
    if (paginacionNumeros) {
        paginacionNumeros.innerHTML = '';
        const total = Math.max(1, totalPages);
        const maxBotones = 5;
        let inicio = Math.max(0, currentPage - Math.floor(maxBotones / 2));
        let fin = Math.min(total - 1, inicio + maxBotones - 1);
        if (fin - inicio + 1 < maxBotones) {
            inicio = Math.max(0, fin - maxBotones + 1);
        }
        for (let i = inicio; i <= fin; i++) {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = `btn btn-page ${i === currentPage ? 'active' : ''}`;
            btn.innerText = (i + 1).toString();
            btn.addEventListener('click', () => irAPagina(i));
            paginacionNumeros.appendChild(btn);
        }
    }
}
function irAPagina(nuevaPagina) {
    if (nuevaPagina < 0 || (totalPaginas > 0 && nuevaPagina >= totalPaginas) || nuevaPagina === paginaActual) {
        return;
    }
    cargarListadoFacturas(nuevaPagina);
}
async function ejecutarEliminarFactura(id, numero) {
    if (!confirm(`¿Desea dar de baja la Factura N° ${numero}?`))
        return;
    try {
        await facturaService.eliminarFactura(id);
        alert(`Factura N° ${numero} eliminada exitosamente.`);
        await cargarListadoFacturas(paginaActual);
    }
    catch (error) {
        console.error(error);
        alert(`Error al eliminar: ${error.message}`);
    }
}
async function manejarBusquedaFiltros(e) {
    e.preventDefault();
    await cargarListadoFacturas(0);
}
function limpiarFiltros() {
    formFiltros.reset();
    filtroListaPrecioArticuloId = undefined;
    if (btnLimpiarFiltroCliente) {
        btnLimpiarFiltroCliente.style.display = 'none';
    }
    if (btnLimpiarFiltroArticulo) {
        btnLimpiarFiltroArticulo.style.display = 'none';
    }
    comboFiltroCondicionIva?.clear();
    cargarListadoFacturas(0);
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
    if (filtroListaPrecioArticuloId !== undefined) {
        filtros.listaPrecioArticuloId = filtroListaPrecioArticuloId;
    }
    if (filtroCondicionIva.value.trim()) {
        const idCond = comboFiltroCondicionIva?.getSelectedId();
        if (idCond && idCond > 0) {
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
async function descargarPdfIndividual(id, numeroFactura) {
    try {
        await facturaService.descargarFacturaIndividualPdf(id, numeroFactura);
    }
    catch (error) {
        console.error(error);
        alert(`Error al generar o descargar el PDF de la factura: ${error.message}`);
    }
}
