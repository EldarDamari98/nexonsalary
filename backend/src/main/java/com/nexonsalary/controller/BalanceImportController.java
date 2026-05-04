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

/**
 * REST API controller for importing balance data and querying balance records.
 *
 * Handles all HTTP requests that start with /balances.
 * The main job of this controller is to accept Excel files from the frontend,
 * parse them, and save the data into the database.
 */
@Path("/balances")
public class BalanceImportController {

    private final ExcelImportService excelImportService = new ExcelImportService();
    private final MonthlyBalanceService monthlyBalanceService = new MonthlyBalanceService();
    private final BalanceQueryService balanceQueryService = new BalanceQueryService();

    /**
     * Accepts an Excel (.xlsx) file upload and imports the balance data into the database.
     *
     * POST /balances/upload
     * Content-Type: multipart/form-data
     *
     * The file is saved to a temporary location, parsed row by row, then grouped
     * by account and date before being saved. The original temp file is deleted afterward.
     * Hebrew filenames are handled by re-encoding from ISO-8859-1 to UTF-8.
     *
     * @param uploadedInputStream the raw file bytes received from the browser
     * @param fileDetail          metadata about the uploaded file (name, size, etc.)
     * @return 200 OK with an ImportResultDto showing how many records were created/updated,
     *         or 500 with an error message if the upload fails
     */
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

            // Re-encode filename from ISO-8859-1 to UTF-8 to support Hebrew file names
            String fileName = new String(
                    fileDetail.getFileName().getBytes("ISO-8859-1"),
                    StandardCharsets.UTF_8
            );

            // Write the uploaded bytes to a temp file so Apache POI can read it
            tempFile = File.createTempFile("balances-", ".xlsx");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                uploadedInputStream.transferTo(out);
            }

            // Parse the Excel file and group rows by account + date
            List<MonthlyMemberBalanceDto> groupedBalances =
                    excelImportService.groupMonthlyBalances(
                            excelImportService.readExcel(tempFile.getAbsolutePath())
                    );

            // Save everything to the database and return a summary of what was created/updated
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
            // Always clean up the temp file, even if an error occurred
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Returns a paginated, filterable list of monthly balance records.
     *
     * GET /balances?page=1&size=10&search=&agent=&date=&sortBy=balanceDate&sortDirection=desc
     *
     * @param page          page number starting from 1 (default: 1)
     * @param size          number of records per page (default: 10)
     * @param search        optional text search on member name or account number
     * @param agent         optional filter by agent name or code
     * @param date          optional filter by balance date (format: YYYY-MM-DD)
     * @param sortBy        field to sort by (default: balanceDate)
     * @param sortDirection sort order — asc or desc (default: desc)
     * @return 200 OK with a paginated response containing balance records, or 500 on error
     */
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
                    balanceQueryService.getBalances(page, size, search, agent, date, sortBy, sortDirection)
            ).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("Failed to fetch balances: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Returns the history of all Excel files that have been imported.
     *
     * GET /balances/uploads
     *
     * Each upload record shows the file name, import date, and counts of
     * how many agents, members, accounts, and balances were created or updated.
     *
     * @return 200 OK with a JSON array of upload history records, or 500 on error
     */
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

    /**
     * Deletes a specific upload and all balance records that came from it.
     *
     * DELETE /balances/uploads/{uploadId}
     *
     * Cascades the deletion to all MonthlyMemberBalance records that were created
     * by this upload. This is useful for correcting a bad import.
     *
     * @param uploadId the database ID of the upload to delete
     * @return 200 OK with a success message, 404 if the upload doesn't exist, or 500 on error
     */
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
