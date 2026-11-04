============================================================================

## 📊 Sistema de Gestión de Facturación de Ventas (con Integración ARCA / AFIP)

Proyecto integral de facturación comercial desarrollado para la cátedra de **Desarrollo de Software** (**UTN - Facultad Regional Mendoza**).

El sistema permite gestionar de punta a punta el circuito de emisión, autorización electrónica de **CAE en ARCA / AFIP (Homologación)**, consulta, filtrado y anulación de comprobantes de venta, integrando listas de precios diferenciadas, catálogos fiscales, emisión de reportes PDF profesionales y una interfaz web con estética *90s modernizada*.

============================================================================

## ✨ Características Principales

1. **Facturación Electrónica ARCA / AFIP (Homologación - WSAA + WSFEv1)**:
   - **Autenticación WSAA (`AfipWsaaClient`)**: generación del Ticket de Requerimiento de Acceso (`loginTicketRequest`), firma criptográfica **PKCS#7 / CMS (`SHA256withRSA`)** mediante **BouncyCastle 1.78** (soporta par `.key` + `.crt` o archivo `.pfx`) y caché automático del Ticket de Acceso (`token` y `sign`) durante su vigencia.
   - **Autorización de Comprobantes WSFEv1 (`AfipWsfeClient` y `AfipFacturacionService`)**: consulta del último comprobante autorizado (`FECompUltimoAutorizado`) y solicitud automática de **CAE** (`FECAESolicitar`) al emitir una factura, persistiendo `cae`, `caeFechaVencimiento`, `resultadoAfip` y `motivoRechazo` en la base de datos.
   - **Visualización del CAE**: el CAE otorgado por ARCA/AFIP se muestra como insignia en la grilla web, en la alerta de confirmación de emisión y en los reportes PDF (individual y consolidado).
   - **Clase `Main` de prueba directa**: permite ejecutar por consola el flujo completo de 3 pasos (WSAA -> `FECompUltimoAutorizado` -> `FECAESolicitar`).

2. **Gestión Completa de Facturas (CRUD)**:
   - Alta de facturas con numeración consecutiva automática y obtención de CAE.
   - Selección dinámica de listas de precios (Minorista / Mayorista) con recálculo automático de precios unitarios.
   - Desglose impositivo en tiempo real: cálculo automático de Subtotal Neto Gravado, IVA (21%) e Importe Total.
   - Modificación de comprobantes existentes y baja lógica (*soft-delete*) manteniendo trazabilidad histórica y auditoría.
   - Manejo global de excepciones (`GlobalExceptionHandler`) con respuestas HTTP semánticas (`400 Bad Request`, `404 Not Found`, `409 Conflict`).

3. **Búsqueda Avanzada y Paginación**:
   - Filtrado multicriterio: número de comprobante, nombre/razón social o CUIT del cliente, rango de fechas inclusivo (`fechaDesde` / `fechaHasta`), condición de IVA y artículo facturado.
   - Paginación dinámica en servidor y en cliente configurable (3, 5, 10 o 20 registros por página).

4. **Exploradores Modales y Componentes Inteligentes**:
   - **Comboboxes buscables (`SearchableCombobox`)**: desplegables interactivos con autocompletado y búsqueda flexible sin acentos.
   - **Explorador Modal de Clientes**: búsqueda por nombre o CUIT con creación instantánea sin perder el formulario de factura.
   - **Explorador Modal de Artículos**: catálogo visual con código automático (`ART-00X`) y fijación simultánea de precio Minorista y Mayorista.

5. **Generación de Reportes PDF con iText 9**:
   - **Factura Individual**: emisión del comprobante en PDF con datos del cliente, condición de IVA, **N° de CAE y Fecha de Vencimiento CAE**, desglose de ítems y totales.
   - **Reporte Consolidado**: exportación del listado filtrado de ventas incluyendo columna de CAE.

6. **Interfaz Web Estilo *90s Modernizado***:
   - Diseño tipo ventanas clásicas con barras de título en degradé, bordes definidos, sombras duras, controles biselados y panel de totales en tipografía monoespaciada, sin scroll horizontal en los modales.

============================================================================

## 🏗 Arquitectura del Sistema

### Tecnologías Empleadas

| Componente | Tecnología | Detalle |
| :--- | :--- | :--- |
| **Backend Core** | Java 21 / Spring Boot 4 | API REST con Spring MVC, inyección de dependencias y manejo global de errores |
| **Facturación Electrónica** | BouncyCastle 1.78 (`bcpkix` + `bcprov`) + SOAP | Firma CMS/PKCS#7 para WSAA (`LoginCms`) y cliente SOAP para WSFEv1 (`FECAESolicitar`) |
| **Persistencia** | Spring Data JPA / Hibernate | Mapeo objeto-relacional y DDL automático (`update`) |
| **Base de Datos** | MySQL 8.x (Docker o local) | Base relacional con soporte transaccional y llaves foráneas |
| **Reportes PDF** | iText 9 (`kernel`, `layout`, `io`) | Generación programática de PDFs en memoria con datos de CAE |
| **Frontend** | TypeScript | Tipado estático estricto y modularización ESM |
| **Estilos** | Vanilla CSS3 | Estética retro-moderna (*90s modernizado*), responsiva y sin desbordes horizontales |
| **Servidor Frontend** | Node.js (`server.mjs`) | Servidor HTTP estático nativo en el puerto `5500` |

============================================================================

## 📂 Estructura del Proyecto

```text
tpgrupal_ds/
├── backend/                               # Proyecto Spring Boot (Maven)
│   ├── pom.xml                            # Dependencias (Spring Boot, Hibernate, MySQL, iText 9, BouncyCastle 1.78)
│   └── src/
│       ├── main/
│       │   ├── java/com/utn/frm/desarrollosoftware/
│       │   │   ├── Main.java              # Prueba directa por consola del flujo WSAA + WSFEv1 (ARCA/AFIP)
│       │   │   ├── afip/                  # Integración ARCA/AFIP: AfipWsaaClient, AfipWsfeClient, AfipFacturacionService, AfipCaeResponse
│       │   │   ├── config/                # Inicializador de datos semilla (DataInitializer.java)
│       │   │   ├── controller/            # FacturaRestController.java y GlobalExceptionHandler.java
│       │   │   ├── dto/                   # DTOs de entrada y reporte (incluye campos CAE)
│       │   │   ├── entities/              # Entidades JPA del dominio
│       │   │   ├── repository/            # Repositorios Spring Data y consultas JPQL dinámicas
│       │   │   ├── service/               # Lógica de negocio transaccional (FacturaService.java)
│       │   │   └── util/                  # Exportador de PDFs con CAE (PdfItextExporter.java)
│       │   └── resources/
│       │       └── application.properties # Parámetros JDBC, Hibernate y credenciales ARCA/AFIP
│       └── test/                          # Pruebas de contexto e integración
├── frontend/                              # Interfaz de Usuario
│   ├── index.html                         # SPA principal con formularios y modales
│   ├── styles.css                         # Hoja de estilos "90s modernizado"
│   ├── tsconfig.json                      # Configuración del compilador TypeScript
│   ├── server.mjs                         # Servidor local Node.js (puerto 5500)
│   ├── dist/                              # Código JavaScript compilado (app.js)
│   └── src/                               # Código fuente TypeScript
│       ├── app.ts                         # Lógica de vistas, eventos, modales y badge de CAE
│       ├── models/                        # Interfaces y DTOs cliente
│       └── services/                      # Cliente HTTP (FacturaService.ts) y fallbacks
├── index.html                             # Redirección raíz al frontend
├── package.json                           # Scripts globales npm
└── README.md                              # Documentación del sistema
```

============================================================================

## 🔐 Configuración de ARCA / AFIP (Homologación)

En `backend/src/main/resources/application.properties` se configuran los parámetros de conexión con ARCA/AFIP:

```properties
afip.enabled=true
afip.cuit=20446629698
afip.key-path=${user.home}/privada_test.key
afip.cert-path=${user.home}/certificado_test.crt
afip.punto-venta=1
afip.cbte-tipo=11
```

- Colocar los archivos `privada_test.key` y `certificado_test.crt` en la carpeta de usuario (`C:\Users\<TuUsuario>\`) o ajustar `afip.key-path` y `afip.cert-path` con la ruta deseada.
- Si no se cuenta con los certificados localmente, el sistema igualmente permite registrar facturas en la base de datos local sin interrumpir el flujo.

============================================================================

## 🔌 Catálogo de Endpoints REST

La API se publica bajo el prefijo `/api/facturas`:

| Método | Endpoint | Descripción | Parámetros / Payload |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/facturas` | Listado paginado con filtros | `numero`, `cliente`, `fechaDesde`, `fechaHasta`, `condicionIvaId`, `listaPrecioArticuloId`, `page`, `size` |
| `GET` | `/api/facturas/{id}` | Obtener detalle de una factura | `id` (path variable) |
| `POST` | `/api/facturas` | Crear factura y solicitar CAE en ARCA/AFIP | JSON con `FacturaRequestDTO` |
| `POST` | `/api/facturas/{id}/solicitar-cae` | Solicitar CAE en ARCA/AFIP para una factura existente | `id` (path variable) |
| `PUT` | `/api/facturas/{id}` | Actualizar una factura existente | `id` (path) y JSON con `FacturaRequestDTO` |
| `DELETE` | `/api/facturas/{id}` | Baja lógica (*soft-delete*) | `id` (path), opcional `usuario` |
| `GET` | `/api/facturas/catalogos` | Todos los catálogos para combos | Ninguno |
| `GET` | `/api/facturas/clientes` | Buscar clientes por nombre/CUIT | `nombre`, `cuit` |
| `POST` | `/api/facturas/clientes` | Crear un nuevo cliente | JSON con `ClienteRequestDTO` |
| `GET` | `/api/facturas/articulos/siguiente-codigo` | Próximo código de artículo | Retorna `{ "siguienteCodigo": "ART-00X" }` |
| `POST` | `/api/facturas/articulos` | Crear artículo y sus precios | JSON con `ArticuloRequestDTO` |
| `GET` | `/api/facturas/lista-precio-articulos` | Artículos por lista de precio | `listaPrecioId`, `articulo` |
| `GET` | `/api/facturas/siguiente-numero` | Próximo n° de comprobante | Retorna `{ "siguienteNumero": 100X }` |
| `GET` | `/api/facturas/{id}/pdf` | Descargar PDF individual (con CAE) | Retorna archivo `Factura_<Nro>.pdf` |
| `GET` | `/api/facturas/pdf` | Descargar reporte consolidado | Mismos filtros que el listado |
