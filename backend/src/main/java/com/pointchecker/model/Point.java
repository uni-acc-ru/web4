package com.pointchecker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "points")
public class Point {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Double x;
    
    @Column(nullable = false)
    private Double y;
    
    @Column(nullable = false)
    private Double r;
    
    @Column(nullable = false)
    private Boolean hit;
    
    @Column(nullable = false)
    private LocalDateTime requestTime;
    
    @Column(nullable = false)
    private Long scriptTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public Point() {
        this.requestTime = LocalDateTime.now();
    }
    
    public Point(Double x, Double y, Double r, Boolean hit, Long scriptTime) {
        this();
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.scriptTime = scriptTime;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Double getX() {
        return x;
    }
    
    public void setX(Double x) {
        this.x = x;
    }
    
    public Double getY() {
        return y;
    }
    
    public void setY(Double y) {
        this.y = y;
    }
    
    public Double getR() {
        return r;
    }
    
    public void setR(Double r) {
        this.r = r;
    }
    
    public Boolean getHit() {
        return hit;
    }
    
    public void setHit(Boolean hit) {
        this.hit = hit;
    }
    
    public LocalDateTime getRequestTime() {
        return requestTime;
    }
    
    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
    
    public Long getScriptTime() {
        return scriptTime;
    }
    
    public void setScriptTime(Long scriptTime) {
        this.scriptTime = scriptTime;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
