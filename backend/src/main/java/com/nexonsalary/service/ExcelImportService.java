package com.nexonsalary.service;

import com.nexonsalary.dto.ExcelRowDto;
import com.nexonsalary.dto.MonthlyMemberBalanceDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelImportService {

    public List<ExcelRowDto> readExcel(String filePath) throws IOException {
        List<ExcelRowDto> rows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return rows;
            }

            Map<String, Integer> headers = extractHeaders(sheet.getRow(0));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String nationalId = getCellString(row, headers.get("תז/חפ"));
                String memberName = getCellString(row, headers.get("שם עמית"));
                String agentCode = getCellString(row, headers.get("מספר סוכן משני"));
                String agentName = getCellString(row, headers.get("סוכן משני"));
                BigDecimal balance = getCellBigDecimal(row, headers.get("יתרה"));
                LocalDate balanceDate = getCellDate(row, headers.get("תאריך היתרה"));

                if (isBlank(nationalId) || isBlank(memberName) || balance == null || balanceDate == null) {
                    continue;
                }

                rows.add(new ExcelRowDto(
                        balanceDate,
                        memberName.trim(),
                        nationalId.trim(),
                        agentCode != null ? agentCode.trim() : "",
                        agentName != null ? agentName.trim() : "",
                        balance
                ));
            }
        }

        return rows;
    }

    public List<MonthlyMemberBalanceDto> groupMonthlyBalances(List<ExcelRowDto> rows) {
        Map<String, List<ExcelRowDto>> grouped = rows.stream()
                .collect(Collectors.groupingBy(row ->
                        row.getBalanceDate() + "|" +
                                row.getNationalId() + "|" +
                                safe(row.getSecondaryAgentCode())
                ));

        List<MonthlyMemberBalanceDto> result = new ArrayList<>();

        for (List<ExcelRowDto> group : grouped.values()) {
            ExcelRowDto first = group.get(0);

            BigDecimal totalBalance = group.stream()
                    .map(ExcelRowDto::getBalance)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.add(new MonthlyMemberBalanceDto(
                    first.getBalanceDate(),
                    first.getMemberName(),
                    first.getNationalId(),
                    first.getSecondaryAgentCode(),
                    first.getSecondaryAgentName(),
                    totalBalance
            ));
        }

        result.sort(Comparator
                .comparing(MonthlyMemberBalanceDto::getBalanceDate)
                .thenComparing(MonthlyMemberBalanceDto::getNationalId));

        return result;
    }

    private Map<String, Integer> extractHeaders(Row headerRow) {
        Map<String, Integer> headers = new HashMap<>();
        if (headerRow == null) {
            return headers;
        }

        for (Cell cell : headerRow) {
            headers.put(getCellString(cell), cell.getColumnIndex());
        }

        return headers;
    }

    private String getCellString(Row row, Integer index) {
        if (index == null) {
            return null;
        }
        return getCellString(row.getCell(index));
    }

    private String getCellString(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private BigDecimal getCellBigDecimal(Row row, Integer index) {
        if (index == null) {
            return null;
        }

        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }

        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING -> {
                    String value = cell.getStringCellValue();
                    if (value == null || value.trim().isEmpty()) {
                        yield null;
                    }
                    value = value.replace(",", "").trim();
                    yield new BigDecimal(value);
                }
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getCellDate(Row row, Integer index) {
        if (index == null) {
            return null;
        }

        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}