package com.nexonsalary.controller;

import com.nexonsalary.dto.ImportResultDto;
import com.nexonsalary.dto.MonthlyMemberBalanceDto;
import com.nexonsalary.service.ExcelImportService;
import com.nexonsalary.service.MonthlyBalanceService;
import jakarta.ws.rs.Consumes;
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
                        .entity(new ImportResultDto(false, 0, "No file uploaded"))
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

            int importedCount = monthlyBalanceService.saveMonthlyBalances(
                    groupedBalances,
                    fileDetail.getFileName()
            );

            return Response.ok(new ImportResultDto(
                    true,
                    importedCount,
                    "File uploaded and processed successfully"
            )).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ImportResultDto(false, 0, "Upload failed: " + e.getMessage()))
                    .build();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}