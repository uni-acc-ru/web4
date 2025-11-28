package com.pointchecker.config;

import com.pointchecker.repository.PointRepository;
import com.pointchecker.repository.UserRepository;
import com.pointchecker.service.AuthService;
import com.pointchecker.service.PointService;
import com.pointchecker.util.EntityManagerUtil;
import jakarta.persistence.EntityManager;

public class ServiceConfig {
    
    public static UserRepository getUserRepository() {
        UserRepository repo = new UserRepository();
        try {
            java.lang.reflect.Field field = UserRepository.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(repo, EntityManagerUtil.getEntityManager());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set EntityManager", e);
        }
        return repo;
    }
    
    public static PointRepository getPointRepository() {
        PointRepository repo = new PointRepository();
        try {
            java.lang.reflect.Field field = PointRepository.class.getDeclaredField("entityManager");
            field.setAccessible(true);
            field.set(repo, EntityManagerUtil.getEntityManager());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set EntityManager", e);
        }
        return repo;
    }
    
    public static AuthService getAuthService() {
        AuthService service = new AuthService();
        try {
            java.lang.reflect.Field field = AuthService.class.getDeclaredField("userRepository");
            field.setAccessible(true);
            field.set(service, getUserRepository());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set UserRepository", e);
        }
        return service;
    }
    
    public static PointService getPointService() {
        PointService service = new PointService();
        try {
            java.lang.reflect.Field pointRepoField = PointService.class.getDeclaredField("pointRepository");
            pointRepoField.setAccessible(true);
            pointRepoField.set(service, getPointRepository());
            
            java.lang.reflect.Field userRepoField = PointService.class.getDeclaredField("userRepository");
            userRepoField.setAccessible(true);
            userRepoField.set(service, getUserRepository());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set repositories", e);
        }
        return service;
    }
}
