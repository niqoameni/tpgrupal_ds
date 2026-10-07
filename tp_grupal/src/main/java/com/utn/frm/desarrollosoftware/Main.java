package com.utn.frm.desarrollosoftware;

import com.utn.frm.desarrollosoftware.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
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

        Usuario niqoameni = new Usuario("niqoameni", "admin", "Nicolas", "Ameni");

        TipoMoneda pesos = new TipoMoneda(niqoameni, "PES", "Pesos Argentinos", "$");
        CondicionIva iva = new CondicionIva(niqoameni, "Consumidor Final", 5);
        PuntoVenta sucursalCentro = new PuntoVenta(niqoameni, 1, "Sucursal Centro", "ELECTRONICA", "Av. San Martin 123");
        Rubro informatica = new Rubro(niqoameni, "Informatica", 1);
        Marca samsung = new Marca(niqoameni, "Samsung", 1);

        Contacto contacto1 = new Contacto("ameninicolas@gmail.com", "4450073", "2612414004");
        Domicilio domicilio1 = new Domicilio("Correa Saa", "1540");
        Cliente cliente1 = new Cliente(niqoameni, "23447568519", "Nicolas Ameni", contacto1, domicilio1);


        Articulo tablet1 = new Articulo(niqoameni, informatica, "ART-001", "Tablet S6 Lite", samsung);
        ListaPrecio listaMin = new ListaPrecio(niqoameni, "LP-01", "Lista General Minorista");
        ListaPrecioArticulo la_tablet = new ListaPrecioArticulo(niqoameni, listaMin, 300000.00, tablet1);

        em.persist(niqoameni);
        em.persist(pesos);
        em.persist(iva);
        em.persist(sucursalCentro);
        em.persist(informatica);
        em.persist(samsung);
        em.persist(contacto1);
        em.persist(domicilio1);
        em.persist(cliente1);
        em.persist(tablet1);
        em.persist(listaMin);
        em.persist(la_tablet);

        FacturaVenta factura1 = new FacturaVenta(niqoameni, 1001L, cliente1, iva, pesos, sucursalCentro, 363000.00, 0.0, 363000.00, null, null, null, null, "EMITIDA", null, null);

        FacturaVentaDetalle detalle1 = new FacturaVentaDetalle(factura1, la_tablet, "Tablet Samsung S6 Lite", 1, 300000.00, 0.00);

        factura1.addDetalle(detalle1);

        em.persist(factura1);

        em.getTransaction().commit();
        */ /*
        ===============================================================================================================
                                                    TP GRUPAL 3 - JPQL
        =============================================================================================================== */

        System.out.println("Nivel 1 -----------------------------------------------------------");
        System.out.println("Ejercicio 1: ");
        List<FacturaVenta> facturasRegistradas = em.createQuery(
                "SELECT f FROM FacturaVenta f", FacturaVenta.class
        ).getResultList();
        facturasRegistradas.forEach(System.out::println);

        System.out.println("Ejercicio 2: ");
        List<Object[]> atributosEspecificos = em.createQuery(
                "SELECT f.numero, f.fechaEmision, f.importeTotal FROM FacturaVenta f", Object[].class
        ).getResultList();
        atributosEspecificos.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("Ejercicio 3: ");
        List<Articulo> filtradoIgual = em.createQuery(
                "SELECT a FROM Articulo a WHERE a.rubro.denominacion = :denominacionRubro", Articulo.class)
                .setParameter("denominacionRubro", "Informatica")
                .getResultList();
        filtradoIgual.forEach(System.out::println);

        System.out.println("Ejercicio 4: ");
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

        System.out.println("Nivel 2 -----------------------------------------------------------");
        System.out.println("Ejercicio 5: ");

        List<FacturaVenta> condicionalesComplejos = em.createQuery(
                "SELECT f FROM FacturaVenta f WHERE f.estado = :estado AND f.importeTotal > :importeMinimo AND f.fechaAnulacion IS NULL", FacturaVenta.class
        )
                .setParameter("estado", "EMITIDA")
                .setParameter("importeMinimo", 10000.00)
                .getResultList();
        condicionalesComplejos.forEach(System.out::println);

        System.out.println("Ejercicio 6: ");
        List<Cliente> filtroPatronTexto = em.createQuery(
                "SELECT c FROM Cliente c WHERE LOWER(c.denominacion) LIKE LOWER(:textoParcial) OR c.cuitCuil LIKE :prefijoCuit", Cliente.class
        )
                .setParameter("textoParcial", "%as%")
                .setParameter("prefijoCuit", "20-%")
                .getResultList();
        filtroPatronTexto.forEach(System.out::println);

        System.out.println("Ejercicio 7: "); //TODO: Revisar si está bien, agregando más objetos
        List<FacturaVenta> ordenamientoSinDuplicados = em.createQuery(
                "SELECT DISTINCT f FROM FacturaVenta f ORDER BY f.estado ASC", FacturaVenta.class
        )
                .getResultList();
        ordenamientoSinDuplicados.forEach(System.out::println);

        System.out.println("Ejercicio 8: ");
        List<Object[]> agregacionSimple = em.createQuery(
                "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) FROM FacturaVenta f WHERE f.estado = :estado", Object[].class
        )
                .setParameter("estado", "EMITIDA")
                .getResultList();
        agregacionSimple.forEach(fila -> System.out.println(Arrays.toString(fila)));

        System.out.println("Ejercicio 9: ");
        List<Integer> numerosPuntoVenta = Arrays.asList(1, 2, 3, 4, 5);
        List<PuntoVenta> operadorInclusion = em.createQuery(
               "SELECT p FROM PuntoVenta p WHERE p.numero IN (:listaPuntos)", PuntoVenta.class
        )
                .setParameter("listaPuntos", numerosPuntoVenta)
                .getResultList();
        operadorInclusion.forEach(System.out::println);

        System.out.println("Nivel 3 -----------------------------------------------------------");
        System.out.println("Ejercicio 10: ");

        em.close();
        emf.close();
    }
}