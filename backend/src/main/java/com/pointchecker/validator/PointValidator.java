package com.pointchecker.validator;

import com.pointchecker.dto.PointRequest;

public class PointValidator {
    
    private static final double X_MIN = -3.0;
    private static final double X_MAX = 5.0;
    private static final double Y_MIN = -5.0;
    private static final double Y_MAX = 3.0;
    private static final double R_MIN = 1.0;
    private static final double R_MAX = 5.0;
    
    public static String validate(PointRequest request) {
        if (request.getX() == null) {
            return "X coordinate is required";
        }
        
        if (request.getY() == null) {
            return "Y coordinate is required";
        }
        
        if (request.getR() == null) {
            return "R radius is required";
        }
        
        if (request.getX() < X_MIN || request.getX() > X_MAX) {
            return "X must be between -3 and 5";
        }
        
        if (request.getY() < Y_MIN || request.getY() > Y_MAX) {
            return "Y must be between -5 and 3";
        }
        
        if (request.getR() < R_MIN || request.getR() > R_MAX) {
            return "R must be between 1 and 5";
        }
        
        return null;
    }
}
