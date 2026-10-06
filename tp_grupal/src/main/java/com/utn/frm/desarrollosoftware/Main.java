package com.utn.frm.desarrollosoftware;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        System.out.println(">>> ¡Conexión con MySQL establecida correctamente! <<<");

        em.close();
        emf.close();
    }
}