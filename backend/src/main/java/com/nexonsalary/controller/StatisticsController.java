package com.nexonsalary.controller;

import com.nexonsalary.service.StatisticsJdbcService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/statistics")
public class StatisticsController {

    private final StatisticsJdbcService statisticsJdbcService = new StatisticsJdbcService();

    @GET
    @Path("/overview")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOverview() {
        try {
            return Response.ok(statisticsJdbcService.getOverview()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch overview: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/assets-trend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetsTrend() {
        try {
            return Response.ok(statisticsJdbcService.getAssetsTrend()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch assets trend: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/commission-trend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommissionTrend() {
        try {
            return Response.ok(statisticsJdbcService.getCommissionTrend()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch commission trend: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/top-agents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopAgents() {
        try {
            return Response.ok(statisticsJdbcService.getTopAgents()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch top agents: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/reason-breakdown")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReasonBreakdown() {
        try {
            return Response.ok(statisticsJdbcService.getReasonBreakdown()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch reason breakdown: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/client-movement")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientMovement() {
        try {
            return Response.ok(statisticsJdbcService.getClientMovement()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch client movement: " + e.getMessage())
                    .build();
        }
    }
}