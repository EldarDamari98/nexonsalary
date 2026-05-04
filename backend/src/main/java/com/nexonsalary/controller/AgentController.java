package com.nexonsalary.controller;

import com.nexonsalary.service.AgentQueryService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST API controller for agent-related endpoints.
 *
 * Handles all HTTP requests that start with /agents.
 * Delegates the actual data fetching to AgentQueryService.
 */
@Path("/agents")
public class AgentController {

    private final AgentQueryService agentQueryService = new AgentQueryService();

    /**
     * Returns a list of all agents in the system along with their performance stats.
     *
     * GET /agents
     *
     * Each agent in the response includes: agent code, name, active status,
     * number of members, number of accounts, total assets managed,
     * latest balance date, and current month's salary.
     *
     * @return 200 OK with a JSON array of agents, or 500 if something goes wrong
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAgents() {
        try {
            return Response.ok(agentQueryService.getAllAgents()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch agents: " + e.getMessage())
                    .build();
        }
    }
}
