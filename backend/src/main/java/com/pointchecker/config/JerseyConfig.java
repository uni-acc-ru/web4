package com.pointchecker.config;

import com.pointchecker.controller.AuthController;
import com.pointchecker.controller.HealthController;
import com.pointchecker.controller.PointController;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class JerseyConfig extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        classes.add(AuthController.class);
        classes.add(PointController.class);
        classes.add(HealthController.class);
        
        return classes;
    }
}
