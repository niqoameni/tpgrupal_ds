package com.utn.frm.desarrollosoftware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transaction;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        //TODO: Instanciar los objetos necesarios.
        //TODO: Crear una cabecera de FacturaVenta y asignale uno o más FacturaVentaDetalle (bidireccional)
        //TODO: Persistir solo el objeto cabecera de FacturaVenta usando un solo llamado a em.persist(facturaVenta)
        //TODO: Verificar que al persistir la factura, se persistan sus detalles
        //TODO: Finalizar la tx con .commit() y cerrar tanto el em y emf

        em.close();
        emf.close();
    }
}