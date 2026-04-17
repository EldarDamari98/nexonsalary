package com.nexonsalary.controller;

import com.nexonsalary.service.CommissionCalculationService;
import com.nexonsalary.service.CommissionQueryService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Path("/commissions")
public class CommissionController {

    private final CommissionCalculationService calculationService = new CommissionCalculationService();
    private final CommissionQueryService queryService = new CommissionQueryService();

    @POST
    @Path("/calculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculate(@QueryParam("month") String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM-DD)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            var result = calculationService.calculateForMonth(month);
            return Response.ok(result).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM-DD\"}")
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

    @POST
    @Path("/recalculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recalculate(@QueryParam("month") String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM-DD)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            calculationService.deleteForMonth(month);
            var result = calculationService.calculateForMonth(month);
            return Response.ok(result).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM-DD\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Recalculation failed: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSummary(@QueryParam("month") String monthStr) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM-DD)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            return Response.ok(queryService.getSummaryForMonth(month)).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM-DD\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Failed to fetch summary: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(@QueryParam("month") String monthStr,
                                     @QueryParam("agentId") Long agentId) {
        if (monthStr == null || monthStr.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"month query param required (format: YYYY-MM-DD)\"}")
                    .build();
        }

        try {
            LocalDate month = YearMonth.parse(monthStr).atDay(1);
            return Response.ok(queryService.getTransactionsForMonth(month, agentId)).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid month format. Use YYYY-MM-DD\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Failed to fetch transactions: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
