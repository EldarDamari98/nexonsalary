package com.nexonsalary.controller;

import com.nexonsalary.dto.ImportResultDto;
import com.nexonsalary.dto.ImportSummaryDto;
import com.nexonsalary.dto.MonthlyMemberBalanceDto;
import com.nexonsalary.service.BalanceQueryService;
import com.nexonsalary.service.ExcelImportService;
import com.nexonsalary.service.MonthlyBalanceService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Path("/balances")
public class BalanceImportController {

    private final ExcelImportService excelImportService = new ExcelImportService();
    private final MonthlyBalanceService monthlyBalanceService = new MonthlyBalanceService();
    private final BalanceQueryService balanceQueryService = new BalanceQueryService();

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadBalanceFile(@FormDataParam("file") InputStream uploadedInputStream,
                                      @FormDataParam("file") FormDataContentDisposition fileDetail) {
        File tempFile = null;

        try {
            if (uploadedInputStream == null || fileDetail == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ImportResultDto(
                                false, null, 0, 0, 0, 0, 0, 0,
                                "No file uploaded"
                        ))
                        .build();
            }

            // 🔥 FIX: convert filename to UTF-8
            String fileName = new String(
                    fileDetail.getFileName().getBytes("ISO-8859-1"),
                    StandardCharsets.UTF_8
            );

            tempFile = File.createTempFile("balances-", ".xlsx");

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                uploadedInputStream.transferTo(out);
            }

            List<MonthlyMemberBalanceDto> groupedBalances =
                    excelImportService.groupMonthlyBalances(
                            excelImportService.readExcel(tempFile.getAbsolutePath())
                    );

            ImportSummaryDto summary = monthlyBalanceService.saveMonthlyBalances(
                    groupedBalances,
                    fileName
            );

            return Response.ok(new ImportResultDto(
                    true,
                    summary.getUploadId(),
                    summary.getImportedRows(),
                    summary.getCreatedAgents(),
                    summary.getCreatedMembers(),
                    summary.getCreatedAccounts(),
                    summary.getCreatedBalances(),
                    summary.getUpdatedBalances(),
                    "File uploaded and processed successfully"
            )).build();

        } catch (Exception e) {
            e.printStackTrace();

            return Response.serverError()
                    .entity(new ImportResultDto(
                            false, null, 0, 0, 0, 0, 0, 0,
                            "Upload failed: " + e.getMessage()
                    ))
                    .build();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBalances(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("search") String search,
            @QueryParam("agent") String agent,
            @QueryParam("date") String date,
            @QueryParam("sortBy") @DefaultValue("balanceDate") String sortBy,
            @QueryParam("sortDirection") @DefaultValue("desc") String sortDirection
    ) {
        try {
            return Response.ok(
                    balanceQueryService.getBalances(
                            page,
                            size,
                            search,
                            agent,
                            date,
                            sortBy,
                            sortDirection
                    )
            ).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch balances: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/uploads")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUploads() {
        try {
            return Response.ok(monthlyBalanceService.getAllUploads()).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch uploads: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/uploads/{uploadId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUpload(@PathParam("uploadId") Long uploadId) {
        try {
            monthlyBalanceService.deleteUpload(uploadId);
            return Response.ok("{\"message\":\"Upload deleted successfully\"}").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"Failed to delete upload: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}