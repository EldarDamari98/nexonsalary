package com.nexonsalary.controller;

import com.nexonsalary.service.CommissionCalculationService;
import com.nexonsalary.service.CommissionQueryService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * REST API controller for commission calculation and querying.
 *
 * Handles all HTTP requests that start with /commissions.
 * This is the most important controller in the system — it triggers the
 * commission engine and serves all commission-related data to the frontend.
 */
@Path("/commissions")
public class CommissionController {

    private final CommissionCalculationService calculationService = new CommissionCalculationService();
    private final CommissionQueryService queryService = new CommissionQueryService();

    /**
     * Triggers commission calculation for a specific month.
     *
     * POST /commissions/calculate?month=YYYY-MM
     *
     * Runs the full commission engine: processes all balance records for the given month,
     * applies perimeter fees, trail commissions, and clawbacks, and saves the results.
     * Will fail if commissions have already been calculated for this month — use
     * /recalculate to redo a month.
     *
     * @param monthStr the month to calculate in YYYY-MM format (e.g. "2025-01")
     * @return 200 OK with a summary of what was calculated, or 400/500 on error
     */
    @POST
    @Path("/calculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculate(@QueryParam("month") String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            var result = calculationService.calculateForMonth(month);
            return Response.ok(result).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM\"}")
                    .build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Calculation failed: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Deletes existing commissions for a month and recalculates from scratch.
     *
     * POST /commissions/recalculate?month=YYYY-MM
     *
     * Useful when the underlying balance data was corrected after a previous calculation.
     * Completely wipes the existing transactions for the month and reruns the engine.
     *
     * @param monthStr the month to recalculate in YYYY-MM format (e.g. "2025-01")
     * @return 200 OK with a fresh calculation summary, or 400/500 on error
     */
    @POST
    @Path("/recalculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recalculate(@QueryParam("month") String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            calculationService.deleteForMonth(month);
            var result = calculationService.calculateForMonth(month);
            return Response.ok(result).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Recalculation failed: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Returns a commission summary for a specific month.
     *
     * GET /commissions/summary?month=YYYY-MM
     *
     * Provides totals broken down by commission type (perimeter fee, trail, clawback)
     * and the net commission amount for the month.
     *
     * @param monthStr the month to fetch in YYYY-MM format
     * @return 200 OK with a JSON summary, or 400/500 on error
     */
    @GET
    @Path("/summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummary(@QueryParam("month") String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            return Response.ok(queryService.getSummaryForMonth(month)).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Failed to fetch summary: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Returns the full list of commission transactions for a month, optionally filtered by agent.
     *
     * GET /commissions/transactions?month=YYYY-MM&agentId=123
     *
     * Each transaction record shows the member, account, balance data,
     * commission amount, direction, and reason.
     *
     * @param monthStr the month to fetch in YYYY-MM format
     * @param agentId  optional — if provided, only returns transactions for this agent
     * @return 200 OK with a JSON array of transactions, or 400/500 on error
     */
    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(@QueryParam("month") String monthStr,
                                    @QueryParam("agentId") Long agentId) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            return Response.ok(queryService.getTransactionsForMonth(month, agentId)).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Failed to fetch transactions: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Returns aggregated commission totals for the explorer view with flexible filtering.
     *
     * GET /commissions/explorer-summary
     *
     * Supports filtering by month, time period (MONTH / YEAR / ALL), member name search,
     * agent, commission reason, and direction (CREDIT/DEBIT).
     *
     * @param monthStr  optional month filter in YYYY-MM format
     * @param period    grouping period — MONTH, YEAR, or ALL (default: MONTH)
     * @param search    optional text search on member name or account number
     * @param agentId   optional agent filter
     * @param reason    optional filter by commission reason (e.g. TRAIL_COMMISSION)
     * @param direction optional filter by direction — CREDIT or DEBIT
     * @return 200 OK with aggregated commission data, or 400/500 on error
     */
    @GET
    @Path("/explorer-summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExplorerSummary(@QueryParam("month") String monthStr,
                                       @QueryParam("period") @DefaultValue("MONTH") String period,
                                       @QueryParam("search") String search,
                                       @QueryParam("agentId") Long agentId,
                                       @QueryParam("reason") String reason,
                                       @QueryParam("direction") String direction) {
        try {
            return Response.ok(
                    queryService.getExplorerSummary(monthStr, period, search, agentId, reason, direction)
            ).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Failed to fetch explorer summary: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Returns a paginated list of individual commission transactions for the explorer view.
     *
     * GET /commissions/explorer-transactions
     *
     * Supports the same filters as explorer-summary, plus pagination and sorting.
     *
     * @param monthStr      optional month filter in YYYY-MM format
     * @param period        grouping period — MONTH, YEAR, or ALL (default: MONTH)
     * @param search        optional text search on member name or account number
     * @param agentId       optional agent filter
     * @param reason        optional commission reason filter
     * @param direction     optional direction filter — CREDIT or DEBIT
     * @param page          page number, starting from 1 (default: 1)
     * @param size          number of records per page (default: 10)
     * @param sortBy        field to sort by (default: balanceDate)
     * @param sortDirection sort order — asc or desc (default: desc)
     * @return 200 OK with a paginated list of transactions, or 400/500 on error
     */
    @GET
    @Path("/explorer-transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExplorerTransactions(@QueryParam("month") String monthStr,
                                            @QueryParam("period") @DefaultValue("MONTH") String period,
                                            @QueryParam("search") String search,
                                            @QueryParam("agentId") Long agentId,
                                            @QueryParam("reason") String reason,
                                            @QueryParam("direction") String direction,
                                            @QueryParam("page") @DefaultValue("1") int page,
                                            @QueryParam("size") @DefaultValue("10") int size,
                                            @QueryParam("sortBy") @DefaultValue("balanceDate") String sortBy,
                                            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection) {
        try {
            return Response.ok(
                    queryService.getExplorerTransactions(
                            monthStr, period, search, agentId, reason, direction,
                            page, size, sortBy, sortDirection)
            ).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Failed to fetch explorer transactions: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
