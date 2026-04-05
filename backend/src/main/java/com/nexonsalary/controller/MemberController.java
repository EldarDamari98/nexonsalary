package com.nexonsalary.controller;

import com.nexonsalary.service.MemberQueryService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/members")
public class MemberController {

    private final MemberQueryService memberQueryService = new MemberQueryService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMembers() {
        try {
            return Response.ok(memberQueryService.getAllMembers()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch members: " + e.getMessage())
                    .build();
        }
    }
}