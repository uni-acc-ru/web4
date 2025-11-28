package com.pointchecker.repository;

import com.pointchecker.model.Point;
import com.pointchecker.model.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PointRepository {
    
    @PersistenceContext(unitName = "pointcheckerPU")
    private EntityManager entityManager;
    
    public PointRepository() {
    }
    
    public Point save(Point point) {
        if (point.getId() == null) {
            entityManager.persist(point);
            return point;
        } else {
            return entityManager.merge(point);
        }
    }
    
    public List<Point> findByUser(User user) {
        return entityManager.createQuery(
            "SELECT p FROM Point p WHERE p.user = :user ORDER BY p.requestTime DESC", Point.class)
            .setParameter("user", user)
            .getResultList();
    }
    
    public List<Point> findByUserId(Long userId) {
        return entityManager.createQuery(
            "SELECT p FROM Point p WHERE p.user.id = :userId ORDER BY p.requestTime DESC", Point.class)
            .setParameter("userId", userId)
            .getResultList();
    }
    
    public void deleteByUser(User user) {
        entityManager.createQuery("DELETE FROM Point p WHERE p.user = :user")
            .setParameter("user", user)
            .executeUpdate();
    }
    
    public void deleteByUserId(Long userId) {
        entityManager.createQuery("DELETE FROM Point p WHERE p.user.id = :userId")
            .setParameter("userId", userId)
            .executeUpdate();
    }
}
