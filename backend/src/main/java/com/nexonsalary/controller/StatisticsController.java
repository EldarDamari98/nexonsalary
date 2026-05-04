package com.nexonsalary.controller;

import com.nexonsalary.service.StatisticsJdbcService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API controller for the statistics and analytics page.
 *
 * Handles all HTTP requests that start with /statistics.
 * Each endpoint returns data for a specific chart or table on the Statistics page.
 * All queries are executed via StatisticsJdbcService using raw JDBC for performance.
 */
@Path("/statistics")
public class StatisticsController {

    private final StatisticsJdbcService statisticsJdbcService = new StatisticsJdbcService();

    /**
     * Returns high-level system-wide statistics.
     *
     * GET /statistics/overview
     *
     * Includes total agents, members, accounts, assets under management,
     * and the current month's total salary.
     *
     * @return 200 OK with a JSON overview object, or 500 on error
     */
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

    /**
     * Returns total assets under management grouped by month — used for the trend line chart.
     *
     * GET /statistics/assets-trend
     *
     * Each point in the response contains a date and the total balance across all accounts
     * for that month.
     *
     * @return 200 OK with a JSON array of monthly data points, or 500 on error
     */
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

    /**
     * Returns total commissions earned grouped by month — used for the commission trend chart.
     *
     * GET /statistics/commission-trend
     *
     * Each point in the response contains a date and the net commission total for that month.
     *
     * @return 200 OK with a JSON array of monthly data points, or 500 on error
     */
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

    /**
     * Returns the top-performing agents ranked by total commission earned.
     *
     * GET /statistics/top-agents
     *
     * Useful for a leaderboard view. Each entry includes agent name and total commission.
     *
     * @return 200 OK with a JSON array of agents sorted by commission descending, or 500 on error
     */
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

    /**
     * Returns a breakdown of commission totals grouped by reason type — used for the pie chart.
     *
     * GET /statistics/reason-breakdown
     *
     * Reasons: PERIMETER_FEE_NEW, TRAIL_COMMISSION, PERIMETER_FEE_CLAWBACK, PERIMETER_FEE_DELTA.
     *
     * @return 200 OK with a JSON array of reason/amount pairs, or 500 on error
     */
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

    /**
     * Returns new vs. lost clients per month — used for the client movement bar chart.
     *
     * GET /statistics/client-movement
     *
     * Each data point contains a month, the number of new clients acquired,
     * and the number of clients who left that month.
     *
     * @return 200 OK with a JSON array of monthly movement data, or 500 on error
     */
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
