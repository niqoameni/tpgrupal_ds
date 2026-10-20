import { FacturaService } from './services/FacturaService';
const facturaService = new FacturaService();
// Estado en memoria
let detallesTemporales = [];
let articulosCatalogo = [];
let facturaActualId = null;
document.addEventListener('DOMContentLoaded', async () => {
    // 1. Cargar catálogos y selects iniciales
    await inicializarCatalogos();
    // 2. Cargar facturas registradas en la BD
    await cargarTablaFacturas();
    // 3. Establecer fecha de hoy por defecto en el formulario de alta
    const inputFecha = document.getElementById('fecha-emision');
    if (inputFecha) {
        inputFecha.value = new Date().toISOString().split('T')[0];
    }
    // 4. Toggle para abrir/cerrar el formulario de nueva factura
    const btnToggleNueva = document.getElementById('btn-toggle-nueva-factura');
    const seccionNueva = document.getElementById('seccion-nueva-factura');
    const btnCerrarNueva = document.getElementById('btn-cerrar-nueva-factura');
    btnToggleNueva?.addEventListener('click', () => {
        if (seccionNueva) {
            seccionNueva.style.display = seccionNueva.style.display === 'none' ? 'block' : 'none';
            if (seccionNueva.style.display === 'block') {
                seccionNueva.scrollIntoView({ behavior: 'smooth' });
            }
        }
    });
    btnCerrarNueva?.addEventListener('click', () => {
        if (seccionNueva)
            seccionNueva.style.display = 'none';
    });
    // 5. Autocompletar precio al seleccionar artículo en el alta
    const selectArticulo = document.getElementById('det-articulo');
    selectArticulo?.addEventListener('change', () => {
        const lpaId = Number(selectArticulo.value);
        const art = articulosCatalogo.find(a => a.listaPrecioArticuloId === lpaId);
        const inputPrecio = document.getElementById('det-precio');
        if (art && inputPrecio) {
            inputPrecio.value = art.precioVenta.toFixed(2);
        }
    });
    // 6. Autocompletar precio al seleccionar artículo en la pantalla de edición
    const selectEditArticulo = document.getElementById('edit-det-articulo');
    selectEditArticulo?.addEventListener('change', () => {
        const lpaId = Number(selectEditArticulo.value);
        const art = articulosCatalogo.find(a => a.listaPrecioArticuloId === lpaId);
        const inputPrecio = document.getElementById('edit-det-precio');
        if (art && inputPrecio) {
            inputPrecio.value = art.precioVenta.toFixed(2);
        }
    });
    // 7. Botón "Añadir a la lista" (Nueva Factura temporal)
    const btnAgregarDetalle = document.getElementById('btn-agregar-detalle');
    btnAgregarDetalle?.addEventListener('click', () => {
        const selArt = document.getElementById('det-articulo');
        const lpaId = Number(selArt.value);
        const cantidad = Number(document.getElementById('det-cantidad').value);
        const precioUnitario = Number(document.getElementById('det-precio').value);
        const bonif = Number(document.getElementById('det-bonif').value) || 0;
        if (!lpaId || isNaN(lpaId)) {
            alert('Por favor seleccione un artículo de la lista.');
            return;
        }
        if (!cantidad || cantidad <= 0) {
            alert('Ingrese una cantidad válida mayor a cero.');
            return;
        }
        const art = articulosCatalogo.find(a => a.listaPrecioArticuloId === lpaId);
        const descripcion = art ? `${art.codigo} - ${art.denominacion}` : 'Artículo';
        detallesTemporales.push({
            listaPrecioArticuloId: lpaId,
            articuloId: art?.articuloId,
            descripcion: descripcion,
            cantidad: cantidad,
            precioUnitario: precioUnitario,
            porcentajeBonificacion: bonif,
            porcentajeIva: 21
        });
        renderizarTablaDetallesTemp();
        limpiarInputsDetalle();
    });
    // 8. Enviar formulario de alta de factura
    const facturaForm = document.getElementById('factura-form');
    facturaForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (detallesTemporales.length === 0) {
            alert('La factura debe tener al menos un artículo cargado.');
            return;
        }
        const inputNumero = document.getElementById('num-factura').value;
        const nuevaFactura = {
            numero: inputNumero ? Number(inputNumero) : undefined,
            fechaEmision: document.getElementById('fecha-emision').value || undefined,
            clienteId: Number(document.getElementById('cliente-id').value),
            condicionIvaId: Number(document.getElementById('cond-iva-id').value),
            puntoVentaId: Number(document.getElementById('pv-id').value),
            tipoMonedaId: Number(document.getElementById('moneda-id').value),
            observaciones: document.getElementById('observaciones').value || undefined,
            usuario: 'admin',
            detalles: detallesTemporales
        };
        try {
            const creada = await facturaService.crearFactura(nuevaFactura);
            alert(`¡Factura N° ${creada.numero} creada con éxito!`);
            detallesTemporales = [];
            renderizarTablaDetallesTemp();
            facturaForm.reset();
            if (seccionNueva)
                seccionNueva.style.display = 'none';
            await cargarTablaFacturas();
        }
        catch (error) {
            console.error(error);
            alert('Error al registrar la factura. Verifique los datos ingresados.');
        }
    });
    // 9. Aplicar filtros de búsqueda
    const filterForm = document.getElementById('filter-form');
    filterForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const inputNum = document.getElementById('filtro-numero').value;
        const inputCli = document.getElementById('filtro-cliente').value;
        const inputDesde = document.getElementById('filtro-desde').value;
        const inputHasta = document.getElementById('filtro-hasta').value;
        const inputCondIva = document.getElementById('filtro-cond-iva').value;
        const filtros = {
            numero: inputNum ? Number(inputNum) : undefined,
            cliente: inputCli ? inputCli : undefined,
            fechaDesde: inputDesde ? inputDesde : undefined,
            fechaHasta: inputHasta ? inputHasta : undefined,
            condicionIvaId: inputCondIva ? Number(inputCondIva) : undefined
        };
        await cargarTablaFacturas(filtros);
    });
    // 10. Limpiar filtros
    const btnLimpiar = document.getElementById('btn-limpiar-filtros');
    btnLimpiar?.addEventListener('click', async () => {
        filterForm?.reset();
        await cargarTablaFacturas();
    });
    // 11. Botón "Volver al listado" en la pantalla de edición
    const btnVolver = document.getElementById('btn-volver-principal');
    btnVolver?.addEventListener('click', async () => {
        facturaActualId = null;
        mostrarPantalla('principal');
        await cargarTablaFacturas();
    });
    // 12. Botón "Agregar Artículo" en la pantalla de edición
    const btnEditAgregar = document.getElementById('btn-edit-agregar-articulo');
    btnEditAgregar?.addEventListener('click', async () => {
        if (!facturaActualId)
            return;
        const selArt = document.getElementById('edit-det-articulo');
        const lpaId = Number(selArt.value);
        const cantidad = Number(document.getElementById('edit-det-cantidad').value);
        const precioUnitario = Number(document.getElementById('edit-det-precio').value);
        const bonif = Number(document.getElementById('edit-det-bonif').value) || 0;
        if (!lpaId || isNaN(lpaId)) {
            alert('Seleccione un artículo.');
            return;
        }
        if (!cantidad || cantidad <= 0) {
            alert('Ingrese una cantidad válida mayor a 0.');
            return;
        }
        const art = articulosCatalogo.find(a => a.listaPrecioArticuloId === lpaId);
        const descripcion = art ? `${art.codigo} - ${art.denominacion}` : 'Artículo';
        try {
            const facturaActualizada = await facturaService.agregarDetalle(facturaActualId, {
                listaPrecioArticuloId: lpaId,
                articuloId: art?.articuloId,
                descripcion: descripcion,
                cantidad: cantidad,
                precioUnitario: precioUnitario,
                porcentajeBonificacion: bonif
            });
            renderizarPantallaEdicion(facturaActualizada);
            // Limpiar inputs
            selArt.value = '';
            document.getElementById('edit-det-cantidad').value = '1';
            document.getElementById('edit-det-precio').value = '';
            document.getElementById('edit-det-bonif').value = '0';
        }
        catch (error) {
            console.error(error);
            alert('Error al agregar el artículo a la factura.');
        }
    });
});
// Cargar opciones en los select desde los catálogos del Backend
async function inicializarCatalogos() {
    try {
        const [clientes, condiciones, puntosVenta, monedas, articulos] = await Promise.all([
            facturaService.obtenerClientes(),
            facturaService.obtenerCondicionesIva(),
            facturaService.obtenerPuntosVenta(),
            facturaService.obtenerTiposMoneda(),
            facturaService.obtenerArticulos()
        ]);
        articulosCatalogo = articulos;
        // 1. Clientes
        const selCliente = document.getElementById('cliente-id');
        if (selCliente) {
            clientes.forEach(c => {
                const opt = document.createElement('option');
                opt.value = c.id.toString();
                opt.textContent = `${c.denominacion} (CUIT: ${c.cuitCuil})`;
                selCliente.appendChild(opt);
            });
        }
        // 2. Condiciones de IVA (formulario y filtro)
        const selCondIvaForm = document.getElementById('cond-iva-id');
        const selCondIvaFiltro = document.getElementById('filtro-cond-iva');
        condiciones.forEach(ci => {
            if (selCondIvaForm) {
                const opt = document.createElement('option');
                opt.value = ci.id.toString();
                opt.textContent = `${ci.denominacion} (AFIP: ${ci.codigoAfip})`;
                selCondIvaForm.appendChild(opt);
            }
            if (selCondIvaFiltro) {
                const optFiltro = document.createElement('option');
                optFiltro.value = ci.id.toString();
                optFiltro.textContent = ci.denominacion;
                selCondIvaFiltro.appendChild(optFiltro);
            }
        });
        // 3. Puntos de Venta
        const selPv = document.getElementById('pv-id');
        if (selPv) {
            puntosVenta.forEach(pv => {
                const opt = document.createElement('option');
                opt.value = pv.id.toString();
                opt.textContent = `Punto ${pv.numero} - ${pv.descripcion}`;
                selPv.appendChild(opt);
            });
        }
        // 4. Tipos de Moneda
        const selMoneda = document.getElementById('moneda-id');
        if (selMoneda) {
            monedas.forEach(m => {
                const opt = document.createElement('option');
                opt.value = m.id.toString();
                opt.textContent = `${m.denominacion} (${m.simbolo})`;
                selMoneda.appendChild(opt);
            });
        }
        // 5. Artículos para formulario de alta y de edición
        const selArticulo = document.getElementById('det-articulo');
        const selEditArticulo = document.getElementById('edit-det-articulo');
        articulos.forEach(a => {
            const label = `[${a.codigo}] ${a.denominacion} - $${a.precioVenta.toFixed(2)}`;
            if (selArticulo) {
                const opt = document.createElement('option');
                opt.value = a.listaPrecioArticuloId.toString();
                opt.textContent = label;
                selArticulo.appendChild(opt);
            }
            if (selEditArticulo) {
                const opt = document.createElement('option');
                opt.value = a.listaPrecioArticuloId.toString();
                opt.textContent = label;
                selEditArticulo.appendChild(opt);
            }
        });
    }
    catch (e) {
        console.warn('No se pudieron cargar los catálogos auxiliares (posiblemente backend apagado):', e);
    }
}
// Cargar y renderizar la tabla principal de facturas
async function cargarTablaFacturas(filtros) {
    const tbody = document.querySelector('#tabla-facturas tbody');
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Cargando facturas...</td></tr>';
    const contador = document.getElementById('contador-facturas');
    try {
        const facturas = await facturaService.obtenerFacturas(filtros);
        tbody.innerHTML = '';
        if (contador)
            contador.textContent = `${facturas.length} factura(s)`;
        if (facturas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No se encontraron facturas con los filtros aplicados.</td></tr>';
            return;
        }
        facturas.forEach(f => {
            const tr = document.createElement('tr');
            const fechaFormateada = formatearFecha(f.fechaEmision);
            tr.innerHTML = `
                <td><strong>#${f.numeroFactura}</strong></td>
                <td>${fechaFormateada}</td>
                <td>${f.clienteDenominacion || 'Consumidor Final'}</td>
                <td><span class="tag">${f.condicionIva || '-'}</span></td>
                <td>${f.puntoVentaDescripcion || '-'}</td>
                <td style="text-align: center;">${f.cantidadItems || 0}</td>
                <td><strong>$${(f.importeFinal || 0).toLocaleString('es-AR', { minimumFractionDigits: 2 })}</strong></td>
                <td>
                    <button class="btn btn-sm btn-edit btn-accion-editar" data-id="${f.id}" title="Editar artículos de esta factura">
                        Editar Artículos
                    </button>
                    <button class="btn btn-sm btn-delete btn-accion-eliminar" data-id="${f.id}" data-num="${f.numeroFactura}" title="Eliminar factura (Baja lógica)">
                        Eliminar
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
        vincularAccionesTabla();
    }
    catch (error) {
        console.error(error);
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center; color:red;">No se pudo conectar con el servidor backend (http://localhost:8080).</td></tr>';
    }
}
// Vincular botones Editar y Eliminar en la tabla
function vincularAccionesTabla() {
    // Editar artículos
    document.querySelectorAll('.btn-accion-editar').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const id = Number(e.currentTarget.getAttribute('data-id'));
            if (!id)
                return;
            await abrirPantallaEdicion(id);
        });
    });
    // Eliminar (Baja Lógica)
    document.querySelectorAll('.btn-accion-eliminar').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const id = Number(e.currentTarget.getAttribute('data-id'));
            const num = e.currentTarget.getAttribute('data-num');
            if (confirm(`¿Confirma que desea dar de baja lógica la Factura N° ${num}?`)) {
                try {
                    await facturaService.darDeBajaLogica(id);
                    alert(`Factura N° ${num} dada de baja lógicamente.`);
                    await cargarTablaFacturas();
                }
                catch (err) {
                    alert('Error al realizar la baja lógica de la factura.');
                }
            }
        });
    });
}
// Pantalla 2: Abrir y renderizar vista de edición de artículos
async function abrirPantallaEdicion(facturaId) {
    try {
        facturaActualId = facturaId;
        const factura = await facturaService.obtenerFacturaPorId(facturaId);
        renderizarPantallaEdicion(factura);
        mostrarPantalla('edicion');
    }
    catch (e) {
        console.error(e);
        alert('No se pudieron obtener los datos de la factura.');
    }
}
function renderizarPantallaEdicion(f) {
    document.getElementById('edit-num-factura').textContent = f.numero.toString();
    document.getElementById('edit-cliente').textContent = f.clienteDenominacion || 'Consumidor Final';
    document.getElementById('edit-cuit').textContent = f.clienteCuit || '-';
    document.getElementById('edit-cond-iva').textContent = f.condicionIvaDenominacion || '-';
    document.getElementById('edit-pv').textContent = f.puntoVentaDescripcion || '-';
    document.getElementById('edit-fecha').textContent = formatearFecha(f.fechaEmision);
    const badgeEstado = document.getElementById('edit-estado');
    if (badgeEstado) {
        badgeEstado.textContent = f.estado || 'EMITIDA';
        badgeEstado.className = f.estado === 'ANULADA' ? 'badge badge-danger' : 'badge badge-success';
    }
    // Renderizar detalles
    const tbody = document.querySelector('#tabla-edit-detalles tbody');
    tbody.innerHTML = '';
    if (!f.detalles || f.detalles.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">Esta factura no contiene artículos cargados.</td></tr>';
    }
    else {
        f.detalles.forEach(d => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><code>${d.codigoArticulo || '-'}</code></td>
                <td>${d.denominacionArticulo || d.descripcion || 'Artículo'}</td>
                <td style="text-align: center;">${d.cantidad}</td>
                <td>$${d.precioUnitario.toFixed(2)}</td>
                <td style="text-align: center;">${d.porcentajeBonificacion || 0}%</td>
                <td>$${d.importeNeto.toFixed(2)}</td>
                <td>$${d.importeIva.toFixed(2)}</td>
                <td><strong>$${d.importeSubtotal.toFixed(2)}</strong></td>
                <td>
                    <button class="btn btn-sm btn-delete btn-quitar-articulo-factura" data-detalle-id="${d.id}" title="Quitar este artículo">
                        Quitar
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
    // Total
    const totalSpan = document.getElementById('edit-total-factura');
    if (totalSpan) {
        totalSpan.textContent = (f.importeTotal || 0).toLocaleString('es-AR', { minimumFractionDigits: 2 });
    }
    // Vincular botones para quitar renglón
    document.querySelectorAll('.btn-quitar-articulo-factura').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const detalleId = Number(e.currentTarget.getAttribute('data-detalle-id'));
            if (!facturaActualId || !detalleId)
                return;
            if (confirm('¿Desea quitar este artículo de la factura?')) {
                try {
                    const actualizada = await facturaService.eliminarDetalle(facturaActualId, detalleId);
                    renderizarPantallaEdicion(actualizada);
                }
                catch (err) {
                    alert('Error al quitar el artículo.');
                }
            }
        });
    });
}
// Renderizar tabla temporal de ítems en el formulario de alta
function renderizarTablaDetallesTemp() {
    const tbody = document.querySelector('#tabla-detalles-temp tbody');
    tbody.innerHTML = '';
    let total = 0;
    detallesTemporales.forEach((det, index) => {
        const bruto = det.cantidad * (det.precioUnitario || 0);
        const bonif = det.porcentajeBonificacion || 0;
        const neto = bruto - (bruto * (bonif / 100));
        const iva = neto * 0.21;
        const subtotal = neto + iva;
        total += subtotal;
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${det.descripcion || 'Artículo'}</td>
            <td style="text-align:center;">${det.cantidad}</td>
            <td>$${(det.precioUnitario || 0).toFixed(2)}</td>
            <td style="text-align:center;">${bonif}%</td>
            <td><strong>$${subtotal.toFixed(2)}</strong></td>
            <td><button type="button" class="btn btn-sm btn-delete btn-quitar-detalle" data-index="${index}">Quitar</button></td>
        `;
        tbody.appendChild(tr);
    });
    const spanTotal = document.getElementById('nuevo-total-temp');
    if (spanTotal) {
        spanTotal.textContent = `$${total.toLocaleString('es-AR', { minimumFractionDigits: 2 })}`;
    }
    document.querySelectorAll('.btn-quitar-detalle').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const idx = Number(e.currentTarget.getAttribute('data-index'));
            detallesTemporales.splice(idx, 1);
            renderizarTablaDetallesTemp();
        });
    });
}
function limpiarInputsDetalle() {
    const sel = document.getElementById('det-articulo');
    if (sel)
        sel.value = '';
    const cant = document.getElementById('det-cantidad');
    if (cant)
        cant.value = '1';
    const precio = document.getElementById('det-precio');
    if (precio)
        precio.value = '';
    const bonif = document.getElementById('det-bonif');
    if (bonif)
        bonif.value = '0';
}
function mostrarPantalla(pantalla) {
    const principal = document.getElementById('pantalla-principal');
    const edicion = document.getElementById('pantalla-edicion');
    if (pantalla === 'principal') {
        if (principal)
            principal.style.display = 'block';
        if (edicion)
            edicion.style.display = 'none';
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
    else {
        if (principal)
            principal.style.display = 'none';
        if (edicion)
            edicion.style.display = 'block';
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
}
function formatearFecha(fechaStr) {
    if (!fechaStr)
        return '-';
    try {
        const d = new Date(fechaStr);
        if (isNaN(d.getTime()))
            return fechaStr;
        return d.toLocaleDateString('es-AR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    }
    catch {
        return fechaStr;
    }
}
