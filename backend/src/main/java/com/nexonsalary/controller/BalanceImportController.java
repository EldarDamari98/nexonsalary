package com.nexonsalary.controller;

import com.nexonsalary.dto.BalanceListItemDto;
import com.nexonsalary.dto.ImportResultDto;
import com.nexonsalary.dto.ImportSummaryDto;
import com.nexonsalary.dto.MonthlyMemberBalanceDto;
import com.nexonsalary.service.BalanceQueryService;
import com.nexonsalary.service.ExcelImportService;
import com.nexonsalary.service.MonthlyBalanceService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
                                false, 0, 0, 0, 0, 0, 0,
                                "No file uploaded"
                        ))
                        .build();
            }

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
                    fileDetail.getFileName()
            );

            int importedCount = summary.getCreatedBalances() + summary.getUpdatedBalances();

            return Response.ok(new ImportResultDto(
                    true,
                    importedCount,
                    summary.getCreatedAgents(),
                    summary.getCreatedMembers(),
                    summary.getCreatedAccounts(),
                    summary.getCreatedBalances(),
                    summary.getUpdatedBalances(),
                    "File uploaded and processed successfully"
            )).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ImportResultDto(
                            false, 0, 0, 0, 0, 0, 0,
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
            @jakarta.ws.rs.QueryParam("page") @jakarta.ws.rs.DefaultValue("1") int page,
            @jakarta.ws.rs.QueryParam("size") @jakarta.ws.rs.DefaultValue("10") int size,
            @jakarta.ws.rs.QueryParam("search") String search,
            @jakarta.ws.rs.QueryParam("agent") String agent,
            @jakarta.ws.rs.QueryParam("date") String date,
            @jakarta.ws.rs.QueryParam("sortBy") @jakarta.ws.rs.DefaultValue("balanceDate") String sortBy,
            @jakarta.ws.rs.QueryParam("sortDirection") @jakarta.ws.rs.DefaultValue("desc") String sortDirection
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
}