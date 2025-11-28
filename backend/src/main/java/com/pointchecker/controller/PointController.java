package com.pointchecker.controller;

import com.pointchecker.dto.*;
import com.pointchecker.service.AuthService;
import com.pointchecker.service.PointService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/points")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointController {
    
    @EJB
    private PointService pointService;
    
    @EJB
    private AuthService authService;
    
    @POST
    public Response checkPoint(
            @HeaderParam("Authorization") String authHeader,
            PointRequest request) {
        try {
            Long userId = validateAuth(authHeader);
            PointResponse response = pointService.checkPoint(request, userId);
            return Response.ok(response).build();
        } catch (UnauthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("UNAUTHORIZED", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("CHECK_FAILED", e.getMessage()))
                .build();
        }
    }
    
    @GET
    public Response getUserPoints(@HeaderParam("Authorization") String authHeader) {
        try {
            Long userId = validateAuth(authHeader);
            List<PointResponse> points = pointService.getUserPoints(userId);
            return Response.ok(points).build();
        } catch (UnauthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("UNAUTHORIZED", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("GET_FAILED", e.getMessage()))
                .build();
        }
    }
    
    @DELETE
    public Response clearPoints(@HeaderParam("Authorization") String authHeader) {
        try {
            Long userId = validateAuth(authHeader);
            pointService.clearUserPoints(userId);
            return Response.ok().build();
        } catch (UnauthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("UNAUTHORIZED", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("DELETE_FAILED", e.getMessage()))
                .build();
        }
    }
    
    private Long validateAuth(String authHeader) throws UnauthorizedException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid authorization header");
        }
        
        String token = authHeader.substring(7);
        Long userId = authService.validateSession(token);
        
        if (userId == null) {
            throw new UnauthorizedException("Invalid session token");
        }
        
        return userId;
    }
    
    private static class UnauthorizedException extends Exception {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
