package com.nexonsalary.controller;

import com.nexonsalary.service.DashboardQueryService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
public class DashboardController {

    private final DashboardQueryService dashboardQueryService = new DashboardQueryService();

    @GET
    @Path("/summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummary() {
        try {
            return Response.ok(dashboardQueryService.getSummary()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch dashboard summary: " + e.getMessage())
                    .build();
        }
    }
}