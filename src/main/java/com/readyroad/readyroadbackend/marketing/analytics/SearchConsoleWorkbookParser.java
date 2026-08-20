package com.readyroad.readyroadbackend.marketing.analytics;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class SearchConsoleWorkbookParser {

    static final String QUERIES = "طلبات البحث";
    static final String PAGES = "الصفحات";
    static final String COUNTRIES = "البلدان";
    static final String DEVICES = "الأجهزة";
    static final String FILTERS = "الفلاتر";
    static final String SEARCH_APPEARANCE = "شكل الظهور في البحث";
    static final String CHART = "رسم بياني";

    private static final List<String> METRIC_HEADERS = List.of(
            "النقرات", "عدد الظهور", "نسبة النقر إلى الظهور", "موضع");

    public ParsedWorkbook parse(String sourceFileName, byte[] content) {
        if (content.length < 4 || content[0] != 'P' || content[1] != 'K') {
            throw new InvalidSearchConsoleWorkbookException("The uploaded file is not a valid XLSX workbook");
        }

        String safeName = sanitizeFileName(sourceFileName);
        List<String> warnings = new ArrayList<>();
        MutableCount ignoredRows = new MutableCount();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(content))) {
            if (!workbook.getExternalLinksTable().isEmpty()) {
                throw new InvalidSearchConsoleWorkbookException("External workbook links are not allowed");
            }

            List<MetricRow> queries = parseMetrics(
                    workbook.getSheet(QUERIES), "أهم طلبات البحث", QUERIES, warnings, ignoredRows);
            List<MetricRow> pages = parseMetrics(
                    workbook.getSheet(PAGES), "أهم الصفحات", PAGES, warnings, ignoredRows);
            if (queries.isEmpty() || pages.isEmpty()) {
                throw new InvalidSearchConsoleWorkbookException(
                        "The workbook must contain non-empty query and page sheets");
            }

            List<MetricRow> countries = parseMetrics(
                    workbook.getSheet(COUNTRIES), "البلد", COUNTRIES, warnings, ignoredRows);
            List<MetricRow> devices = parseMetrics(
                    workbook.getSheet(DEVICES), "الجهاز", DEVICES, warnings, ignoredRows);
            List<MetricRow> searchAppearance = parseMetrics(
                    workbook.getSheet(SEARCH_APPEARANCE), "مظهر البحث", SEARCH_APPEARANCE,
                    warnings, ignoredRows);
            List<ChartRow> chart = parseChart(workbook.getSheet(CHART), warnings, ignoredRows);
            if (chart.isEmpty()) {
                throw new InvalidSearchConsoleWorkbookException(
                        "The workbook chart sheet must contain dated property totals");
            }

            Map<String, String> filters = parseFilters(workbook.getSheet(FILTERS), warnings);
            LocalDate periodStart = chart.stream().map(ChartRow::date).min(LocalDate::compareTo).orElseThrow();
            LocalDate periodEnd = chart.stream().map(ChartRow::date).max(LocalDate::compareTo).orElseThrow();

            return new ParsedWorkbook(
                    safeName,
                    sha256(content),
                    content.length,
                    periodStart,
                    periodEnd,
                    List.copyOf(queries),
                    List.copyOf(pages),
                    List.copyOf(countries),
                    List.copyOf(devices),
                    List.copyOf(searchAppearance),
                    List.copyOf(chart),
                    Map.copyOf(filters),
                    List.copyOf(warnings),
                    ignoredRows.value);
        } catch (InvalidSearchConsoleWorkbookException error) {
            throw error;
        } catch (IOException | RuntimeException error) {
            throw new InvalidSearchConsoleWorkbookException("The XLSX workbook could not be parsed safely", error);
        }
    }

    private List<MetricRow> parseMetrics(
            Sheet sheet,
            String primaryHeader,
            String sheetName,
            List<String> warnings,
            MutableCount ignoredRows) {
        if (sheet == null) {
            warnings.add("MISSING_OPTIONAL_SHEET:" + sheetName);
            return List.of();
        }
        validateHeaders(sheet, primaryHeader, sheetName);
        List<MetricRow> rows = new ArrayList<>();
        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            Row row = sheet.getRow(index);
            if (row == null || value(row.getCell(0)).isBlank()) {
                ignoredRows.value++;
                continue;
            }
            try {
                String dimension = value(row.getCell(0)).trim();
                double clicks = metric(row.getCell(1), false);
                double impressions = metric(row.getCell(2), false);
                double ctr = metric(row.getCell(3), impressions == 0);
                double position = metric(row.getCell(4), impressions == 0);
                validateMetrics(clicks, impressions, ctr, position);
                rows.add(new MetricRow(dimension, clicks, impressions, ctr, position));
            } catch (IllegalArgumentException error) {
                ignoredRows.value++;
                warnings.add("INVALID_ROW:" + sheetName + ":" + (index + 1) + ":" + error.getMessage());
            }
        }
        return rows;
    }

    private List<ChartRow> parseChart(Sheet sheet, List<String> warnings, MutableCount ignoredRows) {
        if (sheet == null) {
            warnings.add("MISSING_OPTIONAL_SHEET:" + CHART);
            return List.of();
        }
        validateHeaders(sheet, "التاريخ", CHART);
        List<ChartRow> rows = new ArrayList<>();
        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            Row row = sheet.getRow(index);
            if (row == null || value(row.getCell(0)).isBlank()) {
                ignoredRows.value++;
                continue;
            }
            try {
                LocalDate date = date(row.getCell(0));
                double clicks = metric(row.getCell(1), false);
                double impressions = metric(row.getCell(2), false);
                double ctr = metric(row.getCell(3), impressions == 0);
                double position = metric(row.getCell(4), impressions == 0);
                validateMetrics(clicks, impressions, ctr, position);
                rows.add(new ChartRow(date, clicks, impressions, ctr, position));
            } catch (IllegalArgumentException error) {
                ignoredRows.value++;
                warnings.add("INVALID_ROW:" + CHART + ":" + (index + 1) + ":" + error.getMessage());
            }
        }
        return rows;
    }

    private Map<String, String> parseFilters(Sheet sheet, List<String> warnings) {
        if (sheet == null) {
            warnings.add("MISSING_OPTIONAL_SHEET:" + FILTERS);
            return Map.of();
        }
        Map<String, String> filters = new LinkedHashMap<>();
        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            Row row = sheet.getRow(index);
            if (row != null) {
                String key = value(row.getCell(0)).trim();
                String filterValue = value(row.getCell(1)).trim();
                if (!key.isBlank() && !filterValue.isBlank()) {
                    filters.put(key, filterValue);
                }
            }
        }
        return filters;
    }

    private void validateHeaders(Sheet sheet, String primaryHeader, String sheetName) {
        Row header = sheet.getRow(0);
        if (header == null || !primaryHeader.equals(value(header.getCell(0)).trim())) {
            throw new InvalidSearchConsoleWorkbookException("Unexpected primary header in sheet: " + sheetName);
        }
        for (int index = 0; index < METRIC_HEADERS.size(); index++) {
            if (!METRIC_HEADERS.get(index).equals(value(header.getCell(index + 1)).trim())) {
                throw new InvalidSearchConsoleWorkbookException("Unexpected metric header in sheet: " + sheetName);
            }
        }
    }

    private static void validateMetrics(double clicks, double impressions, double ctr, double position) {
        if (!Double.isFinite(clicks) || clicks < 0) {
            throw new IllegalArgumentException("clicks must be non-negative");
        }
        if (!Double.isFinite(impressions) || impressions < 0) {
            throw new IllegalArgumentException("impressions must be non-negative");
        }
        if (!Double.isFinite(ctr) || ctr < 0 || ctr > 1) {
            throw new IllegalArgumentException("CTR must be between 0 and 1");
        }
        if (!Double.isFinite(position) || (impressions > 0 && position <= 0)) {
            throw new IllegalArgumentException("position must be positive when impressions exist");
        }
    }

    private static double metric(Cell cell, boolean blankAllowed) {
        String raw = value(cell).trim();
        if (raw.isBlank()) {
            if (blankAllowed) {
                return 0;
            }
            throw new IllegalArgumentException("required metric is blank");
        }
        boolean percent = raw.endsWith("%");
        String normalized = raw.replace("%", "").replace(',', '.').trim();
        try {
            double result = Double.parseDouble(normalized);
            return percent ? result / 100.0 : result;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("metric is not numeric");
        }
    }

    private static LocalDate date(Cell cell) {
        rejectFormula(cell);
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        try {
            return LocalDate.parse(value(cell).trim());
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException("date is not ISO-8601");
        }
    }

    private static String value(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        rejectFormula(cell);
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> Double.toString(cell.getNumericCellValue());
            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
            default -> cell.toString();
        };
    }

    private static void rejectFormula(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.FORMULA) {
            throw new InvalidSearchConsoleWorkbookException("Formula cells are not allowed in the import workbook");
        }
    }

    private static String sanitizeFileName(String value) {
        String normalized = value == null ? "search-console.xlsx" : value.replace('\\', '/');
        String name = normalized.substring(normalized.lastIndexOf('/') + 1).trim();
        if (!name.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new InvalidSearchConsoleWorkbookException("Only .xlsx Search Console exports are accepted");
        }
        if (name.isBlank() || name.length() > 255) {
            throw new InvalidSearchConsoleWorkbookException("The workbook filename is invalid");
        }
        return name;
    }

    private static String sha256(byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 is unavailable", error);
        }
    }

    private static final class MutableCount {
        private int value;
    }

    public record MetricRow(String dimension, double clicks, double impressions, double ctr, double position) {}

    public record ChartRow(LocalDate date, double clicks, double impressions, double ctr, double position) {}

    public record ParsedWorkbook(
            String sourceFileName,
            String sha256,
            long fileSizeBytes,
            LocalDate periodStart,
            LocalDate periodEnd,
            List<MetricRow> queries,
            List<MetricRow> pages,
            List<MetricRow> countries,
            List<MetricRow> devices,
            List<MetricRow> searchAppearance,
            List<ChartRow> chart,
            Map<String, String> filters,
            List<String> warnings,
            int ignoredRowCount) {}
}
