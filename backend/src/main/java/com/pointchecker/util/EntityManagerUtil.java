package com.pointchecker.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtil {
    
    private static EntityManagerFactory entityManagerFactory;
    private static final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
    
    public static void init() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("pointcheckerPU");
        }
    }
    
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            init();
        }
        
        EntityManager em = threadLocalEntityManager.get();
        if (em == null || !em.isOpen()) {
            if (em != null) {
                threadLocalEntityManager.remove();
            }
            em = entityManagerFactory.createEntityManager();
            threadLocalEntityManager.set(em);
        }
        return em;
    }
    
    public static void closeEntityManager() {
        EntityManager em = threadLocalEntityManager.get();
        if (em != null && em.isOpen()) {
            em.close();
            threadLocalEntityManager.remove();
        }
    }
    
    public static void beginTransaction() {
        EntityManager em = getEntityManager();
        if (em.getTransaction() != null && !em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }
    
    public static void commitTransaction() {
        EntityManager em = getEntityManager();
        if (em.getTransaction() != null && em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }
    
    public static void rollbackTransaction() {
        EntityManager em = getEntityManager();
        if (em.getTransaction() != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
    
    public static void close() {
        closeEntityManager();
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
