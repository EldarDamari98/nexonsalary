package com.nexonsalary.controller;

import com.nexonsalary.service.AgentQueryService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/agents")
public class AgentController {

    private final AgentQueryService agentQueryService = new AgentQueryService();

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