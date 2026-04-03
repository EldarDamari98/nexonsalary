package com.nexonsalary.dto;

public class ImportResultDto {

    private boolean success;
    private int importedRows;
    private String message;

    public ImportResultDto() {
    }

    public ImportResultDto(boolean success, int importedRows, String message) {
        this.success = success;
        this.importedRows = importedRows;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getImportedRows() {
        return importedRows;
    }

    public void setImportedRows(int importedRows) {
        this.importedRows = importedRows;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}