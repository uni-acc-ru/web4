package com.pointchecker.dto;

public class PointRequest {
    private Double x;
    private Double y;
    private Double r;
    
    public PointRequest() {}
    
    public PointRequest(Double x, Double y, Double r) {
        this.x = x;
        this.y = y;
        this.r = r;
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
}
