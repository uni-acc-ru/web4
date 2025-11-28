package com.pointchecker.service;

import com.pointchecker.dto.PointRequest;
import com.pointchecker.dto.PointResponse;
import com.pointchecker.model.Point;
import com.pointchecker.model.User;
import com.pointchecker.repository.PointRepository;
import com.pointchecker.repository.UserRepository;
import com.pointchecker.validator.PointValidator;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PointService {
    
    @jakarta.ejb.EJB
    private PointRepository pointRepository;
    
    @jakarta.ejb.EJB
    private UserRepository userRepository;
    
    public PointService() {
    }
    
    public PointResponse checkPoint(PointRequest request, Long userId) throws Exception {
        String validationError = PointValidator.validate(request);
        if (validationError != null) {
            throw new Exception(validationError);
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));
        
        long startTime = System.nanoTime();
        
        boolean hit = checkHit(request.getX(), request.getY(), request.getR());
        
        long endTime = System.nanoTime();
        long scriptTime = (endTime - startTime) / 1000;
        
        Point point = new Point(request.getX(), request.getY(), request.getR(), hit, scriptTime);
        point.setUser(user);
        point = pointRepository.save(point);
        
        return toResponse(point);
    }
    
    public List<PointResponse> getUserPoints(Long userId) {
        List<Point> points = pointRepository.findByUserId(userId);
        return points.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public void clearUserPoints(Long userId) {
        pointRepository.deleteByUserId(userId);
    }
    
    private boolean checkHit(double x, double y, double r) {
        if (r <= 0) {
            return false;
        }
        
        if (x <= 0 && y >= 0) {
            return (x * x + y * y) <= (r * r);
        }
        
        if (x <= 0 && y <= 0) {
            return x >= -r && y >= -r;
        }
        
        if (x >= 0 && y <= 0) {
            return x + Math.abs(y) <= r;
        }
        
        return false;
    }
    
    private PointResponse toResponse(Point point) {
        return new PointResponse(
            point.getId(),
            point.getX(),
            point.getY(),
            point.getR(),
            point.getHit(),
            point.getRequestTime(),
            point.getScriptTime()
        );
    }
}
