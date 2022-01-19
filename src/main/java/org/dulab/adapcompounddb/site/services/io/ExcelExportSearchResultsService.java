package org.dulab.adapcompounddb.site.services.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static org.dulab.adapcompounddb.site.services.io.ExportUtils.isDouble;
import static org.dulab.adapcompounddb.site.services.io.ExportUtils.isInteger;

@Service
public class ExcelExportSearchResultsService implements ExportSearchResultsService {

    private static final Logger LOGGER = LogManager.getLogger(ExcelExportSearchResultsService.class);


    @Override
    public void exportAll(OutputStream outputStream, List<SearchResultDTO> searchResults) throws IOException {

        LOGGER.info("Exporting search results to Excel...");

        InputStream templateStream = this.getClass().getResourceAsStream("export_template.xlsx");

        Workbook workbook = new XSSFWorkbook(Objects.requireNonNull(templateStream));

        ExcelCellStyleSupplier styleSupplier = new ExcelCellStyleSupplier(workbook);

        Sheet dataSheet = workbook.getSheet("Data");

        int rowCount = createHeader(0, dataSheet, styleSupplier);

        for (SearchResultDTO searchResult : searchResults) {
            Row row = dataSheet.createRow(rowCount++);
            createRow(row, searchResult, searchResult.isMarked(), styleSupplier);
        }

        workbook.write(outputStream);

        LOGGER.info("Completed exporting search results to Excel");
    }

    private int createHeader(int rowCount, Sheet sheet, ExcelCellStyleSupplier styleSupplier) {

        // First row. Contains categories of export fields
        rowCount = createFirstHeaderRow(rowCount, sheet, styleSupplier);

        // Second row. Contains names of export fields
        Row row = sheet.createRow(rowCount++);
        int columnCount = 0;
        for (ExportCategory exportCategory : ExportCategory.valuesWithNull()) {
            for (ExportField field : ExportField.values(exportCategory)) {
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(field.name);
                if (field.exportCategory != null) {
                    CellStyle style = styleSupplier.getCellStyle(
                            exportCategory.color, BorderStyle.HAIR, IndexedColors.BLACK);
                    cell.setCellStyle(style);
                }
            }
        }

        return rowCount;
    }

    private int createFirstHeaderRow(int rowCount, Sheet sheet, ExcelCellStyleSupplier styleSupplier) {

//        // Create array of categories with the first null value
//        int numCategories = ExportCategory.values().length;
//        ExportCategory[] exportCategories = new ExportCategory[1 + numCategories];
//        System.arraycopy(ExportCategory.values(), 0, exportCategories, 1, numCategories);

        // Create row
        Row row = sheet.createRow(rowCount);
        int columnCount = 0;
        for (ExportCategory exportCategory : ExportCategory.valuesWithNull()) {
            int numFields = ExportField.values(exportCategory).length;
            if (numFields > 1)
                sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, columnCount, columnCount + numFields - 1));

            if (exportCategory != null) {
                Cell cell = row.createCell(columnCount);
                cell.setCellValue(exportCategory.label);
                cell.setCellStyle(styleSupplier.getCellStyle(
                        exportCategory.color, BorderStyle.HAIR, IndexedColors.BLACK));
            }

            columnCount += numFields;
        }
        return rowCount + 1;
    }

    private void createRow(Row row, SearchResultDTO searchResult, boolean highlight, ExcelCellStyleSupplier styleSupplier) {
        int columnCount = 0;
        for (ExportCategory exportCategory : ExportCategory.valuesWithNull()) {
            for (ExportField field : ExportField.values(exportCategory)) {
                Cell cell = row.createCell(columnCount++);
                String value = field.getter.apply(searchResult);
                if (isDouble(value))
                    cell.setCellValue(Double.parseDouble(value));
                else if (isInteger(value))
                    cell.setCellValue(Integer.parseInt(value));
                else
                    cell.setCellValue(value);

                CellStyle style = styleSupplier.getCellStyle(
                        field.exportCategory != null ? field.exportCategory.color : IndexedColors.WHITE,
                        BorderStyle.HAIR,
                        highlight ? IndexedColors.RED : IndexedColors.BLACK);
                cell.setCellStyle(style);
            }
        }
    }


}
