package com.nexonsalary.controller;

import com.nexonsalary.service.DashboardQueryService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API controller for the main dashboard screen.
 *
 * Handles HTTP requests that start with /dashboard.
 * Returns the high-level summary numbers shown at the top of the app.
 */
@Path("/dashboard")
public class DashboardController {

    private final DashboardQueryService dashboardQueryService = new DashboardQueryService();

    /**
     * Returns a snapshot of the key numbers across the entire system.
     *
     * GET /dashboard/summary
     *
     * Response includes: total number of agents, members, accounts,
     * total assets under management, and the current month's total salary.
     *
     * @return 200 OK with a JSON summary object, or 500 if something goes wrong
     */
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
