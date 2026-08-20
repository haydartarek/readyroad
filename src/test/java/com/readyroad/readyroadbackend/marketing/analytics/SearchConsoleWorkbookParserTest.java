package com.readyroad.readyroadbackend.marketing.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class SearchConsoleWorkbookParserTest {

    private final SearchConsoleWorkbookParser parser = new SearchConsoleWorkbookParser();

    @Test
    void parsesTheArabicSearchConsoleWorkbookContractAndDerivesThePeriod() throws IOException {
        byte[] workbook = workbook(false);

        SearchConsoleWorkbookParser.ParsedWorkbook parsed =
                parser.parse("readyroad-search-console.xlsx", workbook);

        assertThat(parsed.sourceFileName()).isEqualTo("readyroad-search-console.xlsx");
        assertThat(parsed.sha256()).hasSize(64);
        assertThat(parsed.queries()).hasSize(2);
        assertThat(parsed.pages()).hasSize(2);
        assertThat(parsed.chart()).hasSize(2);
        assertThat(parsed.periodStart()).hasToString("2026-08-18");
        assertThat(parsed.periodEnd()).hasToString("2026-08-19");
        assertThat(parsed.filters()).containsEntry("نوع البحث", "الويب");
        assertThat(parsed.warnings()).isEmpty();
    }

    @Test
    void rejectsFormulaCellsInsteadOfEvaluatingUploadedWorkbookLogic() throws IOException {
        assertThatThrownBy(() -> parser.parse("search-console.xlsx", workbook(true)))
                .isInstanceOf(InvalidSearchConsoleWorkbookException.class)
                .hasMessageContaining("Formula cells are not allowed");
    }

    private static byte[] workbook(boolean formula) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            writeMetrics(workbook, SearchConsoleWorkbookParser.QUERIES, "أهم طلبات البحث", formula,
                    new Object[][] {
                        {"readyroad", 12d, 38d, 0.3158d, 3d},
                        {"autoweg autosnelweg verschil belgië", 0d, 100d, 0d, 12d}
                    });
            writeMetrics(workbook, SearchConsoleWorkbookParser.PAGES, "أهم الصفحات", false,
                    new Object[][] {
                        {"https://readyroad.be/fr", 1d, 1158d, 0.0009d, 5.01d},
                        {"https://readyroad.be/nl/traffic-signs/A13", 0d, 189d, 0d, 8.38d}
                    });
            writeMetrics(workbook, SearchConsoleWorkbookParser.COUNTRIES, "البلد", false,
                    new Object[][] {{"Belgium", 38d, 4867d, 0.0078d, 9.2d}});
            writeMetrics(workbook, SearchConsoleWorkbookParser.DEVICES, "الجهاز", false,
                    new Object[][] {{"Mobile", 27d, 2436d, 0.0111d, 8.4d}});
            writeMetrics(workbook, SearchConsoleWorkbookParser.SEARCH_APPEARANCE, "مظهر البحث", false,
                    new Object[][] {{"Web Light Result", 38d, 4867d, 0.0078d, 9.2d}});
            writeChart(workbook);
            Sheet filters = workbook.createSheet(SearchConsoleWorkbookParser.FILTERS);
            filters.createRow(0).createCell(0).setCellValue("الفلتر");
            Row filter = filters.createRow(1);
            filter.createCell(0).setCellValue("نوع البحث");
            filter.createCell(1).setCellValue("الويب");
            workbook.write(output);
            return output.toByteArray();
        }
    }

    private static void writeMetrics(
            XSSFWorkbook workbook,
            String sheetName,
            String primaryHeader,
            boolean formula,
            Object[][] values) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue(primaryHeader);
        header.createCell(1).setCellValue("النقرات");
        header.createCell(2).setCellValue("عدد الظهور");
        header.createCell(3).setCellValue("نسبة النقر إلى الظهور");
        header.createCell(4).setCellValue("موضع");
        for (int index = 0; index < values.length; index++) {
            Row row = sheet.createRow(index + 1);
            row.createCell(0).setCellValue((String) values[index][0]);
            if (formula && index == 0) {
                row.createCell(1).setCellFormula("1+1");
            } else {
                row.createCell(1).setCellValue((double) values[index][1]);
            }
            row.createCell(2).setCellValue((double) values[index][2]);
            row.createCell(3).setCellValue((double) values[index][3]);
            row.createCell(4).setCellValue((double) values[index][4]);
        }
    }

    private static void writeChart(XSSFWorkbook workbook) {
        Sheet sheet = workbook.createSheet(SearchConsoleWorkbookParser.CHART);
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("التاريخ");
        header.createCell(1).setCellValue("النقرات");
        header.createCell(2).setCellValue("عدد الظهور");
        header.createCell(3).setCellValue("نسبة النقر إلى الظهور");
        header.createCell(4).setCellValue("موضع");
        Object[][] values = {
            {"2026-08-18", 20d, 2500d, 0.008d, 9d},
            {"2026-08-19", 18d, 2367d, 0.0076d, 9.4d}
        };
        for (int index = 0; index < values.length; index++) {
            Row row = sheet.createRow(index + 1);
            row.createCell(0).setCellValue((String) values[index][0]);
            row.createCell(1).setCellValue((double) values[index][1]);
            row.createCell(2).setCellValue((double) values[index][2]);
            row.createCell(3).setCellValue((double) values[index][3]);
            row.createCell(4).setCellValue((double) values[index][4]);
        }
    }
}
