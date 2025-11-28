package com.pointchecker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PointResponse {
    private Long id;
    private Double x;
    private Double y;
    private Double r;
    private Boolean hit;
    
    @JsonProperty("timestamp")
    private String requestTime;
    
    @JsonProperty("executionTime")
    private Long scriptTime;
    
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    
    public PointResponse() {}
    
    public PointResponse(Long id, Double x, Double y, Double r, Boolean hit, 
                        LocalDateTime requestTime, Long scriptTime) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.requestTime = requestTime != null ? requestTime.format(formatter) : null;
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
    
    public String getRequestTime() {
        return requestTime;
    }
    
    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }
    
    public Long getScriptTime() {
        return scriptTime;
    }
    
    public void setScriptTime(Long scriptTime) {
        this.scriptTime = scriptTime;
    }
}
