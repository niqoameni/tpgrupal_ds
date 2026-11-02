================================================================================

## 📊 Sistema de Gestión de Facturación de Ventas

Proyecto integral de facturación comercial desarrollado para la cátedra de **Desarrollo de Software** (**UTN - Facultad Regional Mendoza**).

El sistema permite gestionar de punta a punta el circuito de emisión, consulta, filtrado y anulación de comprobantes de venta, integrando listas de precios diferenciadas, catálogos fiscales, emisión de reportes PDF profesionales y una interfaz web moderna y responsiva.

================================================================================

## ✨ Características Principales

1. **Gestión Completa de Facturas (CRUD)**:
   - Alta de facturas con numeración consecutiva automática.
   - Selección dinámica de listas de precios (Minorista / Mayorista) con recálculo automático de precios unitarios.
   - Desglose impositivo en tiempo real: cálculo automático de Subtotal Neto Gravado, IVA (21%) e Importe Total.
   - Modificación de comprobantes existentes.
   - Baja lógica (*soft-delete*) manteniendo trazabilidad histórica y auditoría.

2. **Búsqueda Avanzada y Paginación**:
   - Filtrado multicriterio: número de comprobante, nombre/razón social del cliente, rango de fechas (desde/hasta), condición de IVA y artículo facturado.
   - Paginación dinámica en servidor y en cliente configurable (5, 10, 20 o más registros por página).

3. **Exploradores Modales y Componentes Inteligentes**:
   - **Comboboxes buscables (`SearchableCombobox`)**: desplegables interactivos con autocompletado y búsqueda flexible sin acentos.
   - **Explorador Modal de Clientes**: búsqueda por nombre o CUIT con creación instantánea sin perder el formulario de factura.
   - **Explorador Modal de Artículos**: catálogo visual con código automático (`ART-00X`) y fijación simultánea de precio Minorista y Mayorista.

4. **Generación de Reportes PDF con iText 9**:
   - **Factura Individual**: emisión del comprobante en PDF con membrete institucional, datos del cliente, desglose de ítems, totales e importes en letra.
   - **Reporte Consolidado**: exportación del listado filtrado de ventas con totales acumulados.

================================================================================

## 🏗 Arquitectura del Sistema

### Tecnologías Empleadas

| Componente | Tecnología | Detalle |
| :--- | :--- | :--- |
| **Backend Core** | Java 21 / Spring Boot | Framework robusto con Spring MVC y Dependency Injection |
| **Persistencia** | Spring Data JPA / Hibernate | Mapeo objeto-relacional y DDL automático (`update`) |
| **Base de Datos** | MySQL 8.x | Base relacional con soporte transaccional y llaves foráneas |
| **Reportes PDF** | iText 9 (`kernel`, `layout`, `io`) | Generación programática de PDFs en memoria |
| **Frontend** | TypeScript | Tipado estático estricto y modularización ESM |
| **Estilos** | Vanilla CSS3 | Diseño responsivo con temas claros, modales y animaciones suaves |
| **Servidor Frontend** | Node.js (`server.mjs`) | Servidor HTTP estático nativo sin dependencias pesadas |

================================================================================

## 🏛 Modelo de Negocio y Dominio

El dominio del sistema modela el circuito de ventas argentino según lineamientos tributarios:

- **`FacturaVenta`**: Entidad raíz que contiene fecha de emisión, número consecutivo, totales (neto, IVA, total), relaciones hacia `Cliente`, `PuntoVenta`, `CondicionIva`, `ListaPrecio` y `TipoMoneda`.
- **`FacturaVentaDetalle`**: Renglones del comprobante vinculados a una instancia de `ListaPrecioArticulo`, cantidad, precio unitario y subtotal.
- **`Articulo`**: Catálogo base de productos con código, denominación, `Marca` y `Rubro`.
- **`ListaPrecio` & `ListaPrecioArticulo`**: Permite que un mismo artículo posea precios diferenciados según la modalidad de venta (ej. Minorista vs Mayorista).
- **`Cliente`**: Datos de facturación con `cuitCuil`, `denominacion`, datos de contacto (`email`, `telefono`, `celular`) y `domicilio`.
- **`CondicionIva`**: Tipificaciones impositivas (Consumidor Final, Responsable Inscripto, Monotributo, Exento) con sus respectivos códigos AFIP.
- **`AuditoriaApp`**: Superclase que registra `fechaAlta`, `usuarioAlta`, `fechaBaja` y `usuarioBaja` para el control de integridad y soft-delete.

================================================================================

## 📂 Estructura del Proyecto

```text
tpgrupal_ds/
├── backend/                               # Proyecto Spring Boot (Maven)
│   ├── pom.xml                            # Dependencias (Spring Boot, Hibernate, MySQL, iText 9)
│   └── src/
│       ├── main/
│       │   ├── java/com/utn/frm/desarrollosoftware/
│       │   │   ├── config/                # Configuraciones (CORS, etc.)
│       │   │   ├── controller/            # FacturaRestController.java
│       │   │   ├── dto/                   # DTOs de entrada y reporte
│       │   │   ├── entities/              # Entidades JPA del dominio
│       │   │   ├── repository/            # Repositorios Spring Data
│       │   │   ├── service/               # Lógica de negocio transaccional
│       │   │   └── util/                  # Exportador de PDFs (PdfItextExporter.java)
│       │   └── resources/
│       │       └── application.properties # Parámetros JDBC, JPA y Hibernate
│       └── test/                          # Pruebas unitarias y de integración
├── frontend/                              # Interfaz de Usuario
│   ├── index.html                         # SPA principal con formularios y modales
│   ├── styles.css                         # Hoja de estilos responsiva
│   ├── tsconfig.json                      # Configuración del compilador TypeScript
│   ├── server.mjs                         # Servidor local Node.js (puerto 5500)
│   ├── dist/                              # Código JavaScript compilado (app.js)
│   └── src/                               # Código fuente TypeScript
│       ├── app.ts                         # Lógica de vistas, eventos y modales
│       ├── models/                        # Interfaces y DTOs cliente
│       └── services/                      # Cliente HTTP (FacturaService.ts) y fallbacks
├── index.html                             # Redirección raíz al frontend
├── package.json                           # Scripts globales npm
└── README.md                              # Documentación del sistema
```
================================================================================

## 🔌 Catálogo de Endpoints REST

La API se publica bajo el prefijo `/api/facturas`:

| Método | Endpoint | Descripción | Parámetros / Payload |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/facturas` | Listado paginado con filtros | `numero`, `cliente`, `fechaDesde`, `fechaHasta`, `condicionIvaId`, `page`, `size` |
| `GET` | `/api/facturas/{id}` | Obtener detalle de una factura | `id` (path variable) |
| `POST` | `/api/facturas` | Crear una nueva factura | JSON con `FacturaRequestDTO` |
| `PUT` | `/api/facturas/{id}` | Actualizar una factura existente | `id` (path) y JSON con `FacturaRequestDTO` |
| `DELETE` | `/api/facturas/{id}` | Baja lógica (*soft-delete*) | `id` (path), opcional `usuario` |
| `GET` | `/api/facturas/catalogos` | Todos los catálogos para combos | Ninguno |
| `GET` | `/api/facturas/clientes` | Buscar clientes por nombre/CUIT | `nombre`, `cuit` |
| `POST` | `/api/facturas/clientes` | Crear un nuevo cliente | JSON con `ClienteRequestDTO` |
| `GET` | `/api/facturas/articulos/siguiente-codigo` | Próximo código de artículo | Retorna `{ "siguienteCodigo": "ART-00X" }` |
| `POST` | `/api/facturas/articulos` | Crear artículo y sus precios | JSON con `ArticuloRequestDTO` |
| `GET` | `/api/facturas/lista-precio-articulos` | Artículos por lista de precio | `listaPrecioId`, `articulo` |
| `GET` | `/api/facturas/siguiente-numero` | Próximo n° de comprobante | Retorna `{ "siguienteNumero": 100X }` |
| `GET` | `/api/facturas/{id}/pdf` | Descargar PDF individual | Retorna archivo `Factura_<Nro>.pdf` |
| `GET` | `/api/facturas/pdf` | Descargar reporte consolidado | Mismos filtros que el listado |

