package com.utn.frm.desarrollosoftware;

import com.utn.frm.desarrollosoftware.entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transaction;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

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

        em.close();
        emf.close();
    }
}