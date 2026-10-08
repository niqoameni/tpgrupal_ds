package com.utn.frm.desarrollosoftware;

import com.utn.frm.desarrollosoftware.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transaction;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParseException {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        /*
        ===============================================================================================================
                                        TP GRUPAL 2 - Jakarta Persistence API
        =============================================================================================================== */ /*

        em.getTransaction().begin();

        // Usuarios:
        Usuario niqoameni = new Usuario("niqoameni", "admin", "Nicolas", "Ameni");
        Usuario nicomesa = new Usuario("nicomesa", "123", "Nicolas", "Mesa");
        Usuario agusbanuls = new Usuario("agusbañuls", "123", "Agustin", "Bañuls");
        em.persist(niqoameni);
        em.persist(nicomesa);
        em.persist(agusbanuls);

        // Tipo Moneda:
        TipoMoneda pesos = new TipoMoneda(niqoameni, "PES", "Pesos Argentinos", "$");
        TipoMoneda usd = new TipoMoneda(nicomesa, "DOL", "Dolar Estadounidense", "U$S");
        TipoMoneda euro = new TipoMoneda(nicomesa, "EUR", "Euros", "€");
        em.persist(pesos);
        em.persist(usd);
        em.persist(euro);

        // Condicion IVA:
        CondicionIva consumidorFinal = new CondicionIva(niqoameni, "Consumidor Final", 5);
        CondicionIva responsableIns = new CondicionIva(niqoameni, "Responsable Inscripto", 1);
        CondicionIva responsableMon = new CondicionIva(niqoameni, "Responsable Monotributo", 6);
        CondicionIva exento = new CondicionIva(niqoameni, "IVA Sujeto Exento", 4);
        em.persist(consumidorFinal);
        em.persist(responsableIns);
        em.persist(responsableMon);
        em.persist(exento);

        // Punto Venta:
        PuntoVenta sucursalCentro = new PuntoVenta(niqoameni, 1, "Sucursal Centro", "ELECTRONICA", "Av. San Martin 123");
        PuntoVenta sucursalShopping = new PuntoVenta(agusbanuls, 2, "Sucursal de Mendoza Shopping", "ELECTRONICA", "Acceso Este 3280");
        PuntoVenta casaCentral = new PuntoVenta(agusbanuls, 3, "Ventas Online - Web", "ELECTRONICA", "Av. España 500");
        em.persist(sucursalCentro);
        em.persist(sucursalShopping);
        em.persist(casaCentral);

        // Rubro:
        Rubro informatica = new Rubro(niqoameni, "Informatica", 1);
        Rubro telefonia = new Rubro(nicomesa, "Telefonia Celular", 2);
        Rubro perifericos = new Rubro(nicomesa, "Accesorios y Perifericos", 3);
        em.persist(informatica);
        em.persist(telefonia);
        em.persist(perifericos);

        // Marca:
        Marca samsung = new Marca(niqoameni, "Samsung", 1);
        Marca motorola = new Marca(agusbanuls, "Motorola", 2);
        Marca redragon = new Marca(agusbanuls, "Redragon", 3);
        Marca logitech = new Marca(agusbanuls, "Logitech", 4);
        em.persist(samsung);
        em.persist(motorola);
        em.persist(redragon);
        em.persist(logitech);

        // Contacto:
        Contacto contacto1 = new Contacto("ameninicolas@gmail.com", "4450073", "2612414004");
        Contacto contacto2 = new Contacto();
        contacto2.setCelular("2614443334");
        Contacto contacto3 = new Contacto();
        contacto3.setEmail("lucianocruz@gmail.com");
        em.persist(contacto1);
        em.persist(contacto2);
        em.persist(contacto3);

        // Domicilio:
        Domicilio domicilio1 = new Domicilio("Correa Saa", "1540");
        Domicilio domicilio2 = new Domicilio();
        Domicilio domicilio3 = new Domicilio("Allayme", "2021");
        em.persist(domicilio1);
        em.persist(domicilio2);
        em.persist(domicilio3);

        // Cliente:
        Cliente cliente1 = new Cliente(niqoameni, "23447568519", "Nicolas Ameni", contacto1, domicilio1);
        Cliente cliente2 = new Cliente(niqoameni, "23445675323", "Nicolas Contrera", contacto2, domicilio2);
        Cliente cliente3 = new Cliente(nicomesa, "23445445343", "Luciano Cruz", contacto3, domicilio3);
        em.persist(cliente1);
        em.persist(cliente2);
        em.persist(cliente3);

        // Articulo:
        Articulo tablet1 = new Articulo(niqoameni, informatica, "ART-001", "Tablet S6 Lite", samsung);
        Articulo celular1 = new Articulo(agusbanuls, telefonia, "ART-002", "Motorola Edge 70", motorola);
        Articulo celular2 = new Articulo(agusbanuls, telefonia, "ART-003", "Samsung Galaxy A16", samsung);
        Articulo auriculares1 = new Articulo(niqoameni, perifericos, "ART-004", "Redragon Luce H888", redragon);
        Articulo mouse1 = new Articulo(niqoameni, perifericos, "ART-005", "Mouse inalambrino Logitech M170", logitech);
        Articulo mouse2 = new Articulo(niqoameni, perifericos, "ART-006", "Mouse Logitech G203", logitech);
        em.persist(tablet1);
        em.persist(celular1);
        em.persist(celular2);
        em.persist(auriculares1);
        em.persist(mouse1);
        em.persist(mouse2);

        // Lista Precios:
        ListaPrecio listaMin = new ListaPrecio(niqoameni, "LP-01", "Lista General Minorista");
        ListaPrecio listaMay = new ListaPrecio(nicomesa, "LP-02", "Lista General Mayorista");
        em.persist(listaMin);
        em.persist(listaMay);

        // Lista Precio-Articulo:
        ListaPrecioArticulo lmi_tablet1 = new ListaPrecioArticulo(niqoameni, listaMin, 785000.00, tablet1);
        ListaPrecioArticulo lma_tablet1 = new ListaPrecioArticulo(niqoameni, listaMay, 685000.00, tablet1);
        ListaPrecioArticulo lmi_celular1 = new ListaPrecioArticulo(agusbanuls, listaMin, 647000.00, celular1);
        ListaPrecioArticulo lma_celular1 = new ListaPrecioArticulo(agusbanuls, listaMay, 590000.00, celular1);
        ListaPrecioArticulo lmi_celular2 = new ListaPrecioArticulo(agusbanuls, listaMin, 248000.00, celular2);
        ListaPrecioArticulo lma_celular2 = new ListaPrecioArticulo(agusbanuls, listaMay, 200000.00, celular2);
        ListaPrecioArticulo lmi_auriculares1 = new ListaPrecioArticulo(agusbanuls, listaMin, 82000.00, auriculares1);
        ListaPrecioArticulo lma_auriculares1 = new ListaPrecioArticulo(agusbanuls, listaMay, 60000.00, auriculares1);
        ListaPrecioArticulo lmi_mouse1 = new ListaPrecioArticulo(agusbanuls, listaMin, 15000.00, mouse1);
        ListaPrecioArticulo lma_mouse1 = new ListaPrecioArticulo(agusbanuls, listaMay, 9000.00, mouse1);
        ListaPrecioArticulo lmi_mouse2 = new ListaPrecioArticulo(agusbanuls, listaMin, 39000.00, mouse2);
        ListaPrecioArticulo lma_mouse2 = new ListaPrecioArticulo(agusbanuls, listaMay, 30000.00, mouse2);
        em.persist(lmi_tablet1);
        em.persist(lma_tablet1);
        em.persist(lmi_celular1);
        em.persist(lma_celular1);
        em.persist(lmi_celular2);
        em.persist(lma_celular2);
        em.persist(lmi_auriculares1);
        em.persist(lma_auriculares1);
        em.persist(lmi_mouse1);
        em.persist(lma_mouse1);
        em.persist(lmi_mouse2);
        em.persist(lma_mouse2);

        // Facturas de Venta:
        FacturaVenta factura1 = new FacturaVenta(niqoameni, 1001L, cliente2, consumidorFinal, pesos, sucursalShopping, 0.00, null, null, null, null, null);
        FacturaVenta factura2 = new FacturaVenta(nicomesa, 1002L, cliente1, responsableIns, pesos, sucursalCentro, 0.0, null, null, null, null, null);
        FacturaVenta factura3 = new FacturaVenta(agusbanuls, 1003L, cliente3, consumidorFinal, pesos, sucursalShopping, 0.0, null, null, null, null, null);
        FacturaVenta factura4 = new FacturaVenta(agusbanuls, 1004L, cliente2, consumidorFinal, pesos, sucursalShopping, 0.0, null, null, null, null, null);

        // Detalles de la Factura:
        FacturaVentaDetalle detalle1 = new FacturaVentaDetalle(factura1, lmi_tablet1, "Compra por menor: Informatica", 1, 0.0);
        FacturaVentaDetalle detalle2 = new FacturaVentaDetalle(factura2, lma_auriculares1, "Compra por mayor: Perifericos", 7, 0.0);
        FacturaVentaDetalle detalle3 = new FacturaVentaDetalle(factura2, lma_mouse2, "Compra por mayor: Perifericos", 10, 30.0);
        FacturaVentaDetalle detalle4 = new FacturaVentaDetalle(factura3, lmi_celular1, "Compra por menor: Telefonia", 1, 0.0);
        FacturaVentaDetalle detalle5 = new FacturaVentaDetalle(factura4, lmi_mouse1, "Compra por menor: Perifericos", 1, 0.0);

        factura1.addDetalle(detalle1);
        factura2.addDetalle(detalle2);
        factura2.addDetalle(detalle3);
        factura3.addDetalle(detalle4);
        factura4.addDetalle(detalle5);

        em.persist(factura1);
        em.persist(factura2);
        em.persist(factura3);
        em.persist(factura4);

        em.getTransaction().commit(); */

        /*
        ===============================================================================================================
                                                    TP GRUPAL 3 - JPQL
        =============================================================================================================== */

        System.out.println("------------------------------------- Nivel 1 -------------------------------------");
        System.out.println("Ejercicio 1: ----------------------------------------------------------------------");
        List<FacturaVenta> facturasRegistradas = em.createQuery(
                "SELECT f FROM FacturaVenta f", FacturaVenta.class
        ).getResultList();
        facturasRegistradas.forEach(System.out::println);

        System.out.println("Ejercicio 2: ----------------------------------------------------------------------");
        List<Object[]> atributosEspecificos = em.createQuery(
                "SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f", Object[].class
        ).getResultList();
        atributosEspecificos.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("Ejercicio 3: ----------------------------------------------------------------------");
        List<Articulo> filtradoIgual = em.createQuery(
                "SELECT a FROM Articulo a WHERE a.rubro.denominacion = :denominacionRubro", Articulo.class)
                .setParameter("denominacionRubro", "Accesorios y Perifericos")
                .getResultList();
        filtradoIgual.forEach(System.out::println);

        System.out.println("Ejercicio 4: ----------------------------------------------------------------------");
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fechaInicio = sfd.parse("2026-09-10 00:00:00");
        Date fechaFin = sfd.parse("2026-10-10 00:00:00");

        List<FacturaVenta> filtradoRango = em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin", FacturaVenta.class
        )
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .getResultList();
        filtradoRango.forEach(System.out::println);

        System.out.println("------------------------------------- Nivel 2 -------------------------------------");
        System.out.println("Ejercicio 5: ----------------------------------------------------------------------");

        List<FacturaVenta> condicionalesComplejos = em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.estado = :estado AND f.importeTotal > :importeMinimo AND f.fechaAnulacion IS NULL", FacturaVenta.class
        )
                .setParameter("estado", "EMITIDA")
                .setParameter("importeMinimo", 100000.00)
                .getResultList();
        condicionalesComplejos.forEach(System.out::println);

        System.out.println("Ejercicio 6: ----------------------------------------------------------------------");
        List<Cliente> filtroPatronTexto = em.createQuery(
                "SELECT c FROM Cliente c WHERE LOWER(c.denominacion) LIKE LOWER(:textoParcial) OR c.cuitCuil LIKE :prefijoCuit", Cliente.class
        )
                .setParameter("textoParcial", "%as%")
                .setParameter("prefijoCuit", "20-%")
                .getResultList();
        filtroPatronTexto.forEach(System.out::println);

        System.out.println("Ejercicio 7: ----------------------------------------------------------------------");
        List<Object[]> ordenamientoSinDuplicados = em.createQuery(
                "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC", Object[].class
        )
                .getResultList();
        ordenamientoSinDuplicados.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("Ejercicio 8: ----------------------------------------------------------------------");
        List<Object[]> agregacionSimple = em.createQuery(
                "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) FROM FacturaVenta f WHERE f.estado = :estado", Object[].class
        )
                .setParameter("estado", "EMITIDA")
                .getResultList();
        agregacionSimple.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("Ejercicio 9: ----------------------------------------------------------------------");
        List<Integer> numerosPuntoVenta = Arrays.asList(1, 3);
        List<PuntoVenta> operadorInclusion = em.createQuery(
               "SELECT p FROM PuntoVenta p WHERE p.numero IN (:listaPuntos)", PuntoVenta.class
        )
                .setParameter("listaPuntos", numerosPuntoVenta)
                .getResultList();
        operadorInclusion.forEach(System.out::println);

        System.out.println("------------------------------------- Nivel 3 -------------------------------------");
        System.out.println("Ejercicio 10: ---------------------------------------------------------------------");

        List<FacturaVenta> navegacionRelacion = em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.usuarioCarga.usuario = :user", FacturaVenta.class
        )
                .setParameter("user", "agusbanuls")
                .getResultList();
        navegacionRelacion.forEach(System.out::println);

        System.out.println("Ejercicio 11: ---------------------------------------------------------------------");
        List<FacturaVentaDetalle> innerJoin = em.createQuery(
                "SELECT d FROM FacturaVentaDetalle d INNER JOIN d.factura f WHERE f.puntoVenta.descripcion = :punto", FacturaVentaDetalle.class
        )
                .setParameter("punto", "Sucursal de Mendoza Shopping")
                .getResultList();
        innerJoin.forEach(System.out::println);

        System.out.println("Ejercicio 12: ---------------------------------------------------------------------");
        List<Object[]> leftJoin = em.createQuery(
                "SELECT a.denominacion, m.denominacion FROM Articulo a LEFT JOIN a.marca m", Object[].class
        )
                .getResultList();
        leftJoin.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("Ejercicio 13: ---------------------------------------------------------------------");
        List<FacturaVenta> multinivel = em.createQuery(
                "SELECT f FROM FacturaVenta f INNER JOIN f.detalles d WHERE d.listaPrecioArticulo.articulo.marca.denominacion = :marcaSeñalada", FacturaVenta.class
        )
                .setParameter("marcaSeñalada", "Logitech")
                .getResultList();
        multinivel.forEach(System.out::println);

        System.out.println("Ejercicio 14: ---------------------------------------------------------------------");
        List<FacturaVenta> subcosultas = em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE importeTotal > (SELECT AVG(f2.importeTotal) FROM FacturaVenta f2)", FacturaVenta.class
        ).getResultList();
        subcosultas.forEach(System.out::println);

        System.out.println("------------------------------------- Nivel 4 -------------------------------------");
        System.out.println("Ejercicio 15: ---------------------------------------------------------------------");
        // Obtener la descripción del punto de venta, la cantidad de facturas emitidas por cada uno y la suma total facturada.
        List<Object[]> groupBy = em.createQuery(
                "SELECT f.puntoVenta.descripcion, COUNT(f), SUM(f.importeTotal) FROM FacturaVenta f GROUP BY f.puntoVenta.descripcion", Object[].class
        )
                        .getResultList();
        groupBy.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("Ejercicio 16: ---------------------------------------------------------------------");
        List<Object[]> havingGrupo = em.createQuery(
                "SELECT f.usuarioCarga.nombre FROM FacturaVenta f GROUP BY f.usuarioCarga.nombre HAVING COUNT(f) > 5", Object[].class
        )
                        .getResultList();
        havingGrupo.forEach(fila -> System.out.println(Arrays.toString(fila))); //No devuelve nada porque no tengo a nadie que haya cargado más de 5 facturas

        System.out.println("Ejercicio 17: ---------------------------------------------------------------------");
        List<Object[]> agrupacionAgregacion = em.createQuery(
                "SELECT d.listaPrecioArticulo.articulo.marca.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) FROM FacturaVentaDetalle d GROUP BY d.listaPrecioArticulo.articulo.marca", Object[].class
        )
                        .getResultList();
        agrupacionAgregacion.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("------------------------------------- Nivel 5 -------------------------------------");
        System.out.println("Ejercicio 18: ---------------------------------------------------------------------");
        List<Marca> subconsultaCorrelacionada = em.createQuery(
                "SELECT m FROM Marca m WHERE EXISTS (SELECT d FROM FacturaVentaDetalle d WHERE d.listaPrecioArticulo.articulo.marca = m)", Marca.class
        )
                        .getResultList();
        subconsultaCorrelacionada.forEach(System.out::println);

        System.out.println("Ejercicio 19: ---------------------------------------------------------------------");
        List<Articulo> subconsultaNotExists = em.createQuery(
                "SELECT a FROM Articulo a WHERE NOT EXISTS (SELECT d.listaPrecioArticulo.articulo FROM FacturaVentaDetalle d WHERE d.listaPrecioArticulo.articulo = a)", Articulo.class
        )
                        .getResultList();
        subconsultaNotExists.forEach(System.out::println);

        System.out.println("Ejercicio 20: ---------------------------------------------------------------------");
        List<Object[]> proyeccionCondicional = em.createQuery(
                "SELECT f.numero, f.importeTotal, CASE " +
                        "                               WHEN f.importeTotal > 50000.00 THEN 'ALTO VALOR'" +
                        "                               WHEN f.importeTotal BETWEEN 10000.00 AND 50000.00 THEN 'MEDIO VALOR'" +
                        "                               WHEN f.importeTotal < 10000.00 THEN 'BAJO VALOR'" +
                        "                            END FROM FacturaVenta f", Object[].class
        )
                        .getResultList();
        proyeccionCondicional.forEach(fila -> System.out.println(Arrays.toString(fila)));

        em.close();
        emf.close();
    }
}