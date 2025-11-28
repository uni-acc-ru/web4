package com.pointchecker.controller;

import com.pointchecker.dto.*;
import com.pointchecker.service.AuthService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {
    
    @EJB
    private AuthService authService;
    
    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("REGISTRATION_FAILED", e.getMessage()))
                .build();
        }
    }
    
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("LOGIN_FAILED", e.getMessage()))
                .build();
        }
    }
}
