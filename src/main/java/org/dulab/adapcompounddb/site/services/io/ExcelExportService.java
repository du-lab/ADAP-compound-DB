package org.dulab.adapcompounddb.site.services.io;


import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Service
public class ExcelExportService implements ExportService {


    @Override
    public void exportAll(OutputStream outputStream, List<SearchResultDTO> searchResults) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Map<ExportCategory, CellStyle> styles = new HashMap<>();
        for (ExportCategory exportCategory : ExportCategory.values()) {
            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(exportCategory.color.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setBorderRight(BorderStyle.HAIR);
            style.setBorderLeft(BorderStyle.HAIR);
            style.setBorderTop(BorderStyle.HAIR);
            style.setBorderBottom(BorderStyle.HAIR);
            styles.put(exportCategory, style);
        }

        CellStyle highlightStyle = workbook.createCellStyle();
        Font highlightFont = workbook.createFont();
        highlightFont.setColor(IndexedColors.RED.getIndex());
        highlightStyle.setFont(highlightFont);

        Sheet indexSheet = workbook.createSheet("Index");
        Sheet dataSheet = workbook.createSheet("Data");

        fillOutIndexSheet(indexSheet, dataSheet.getSheetName());


        int rowCount = createHeader(0, dataSheet, styles);

        for (SearchResultDTO searchResult : searchResults) {
            Row row = dataSheet.createRow(rowCount++);
            createRow(row, searchResult, searchResult.isMarked(), styles);
        }

        workbook.write(outputStream);
    }

    private void fillOutIndexSheet(Sheet sheet, String dataSheetName) {

        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);

        CellStyle linkStyle = sheet.getWorkbook().createCellStyle();
        Font linkFont = sheet.getWorkbook().createFont();
        linkFont.setUnderline(Font.U_SINGLE);
        linkFont.setColor(IndexedColors.BLUE.getIndex());
        linkFont.setBold(true);
        linkStyle.setFont(linkFont);

        // Header
        createIndexRow(sheet, 0, 0, "Simple export from ADAP-KDB")
                .setCellStyle(boldStyle);

        // Hyperlink
        Cell linkCell = createIndexRow(sheet, 2, 0, "Click here to see results");
        Hyperlink link = sheet.getWorkbook().getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
        link.setAddress(String.format("'%s'!A1", dataSheetName));
        linkCell.setHyperlink(link);
        linkCell.setCellStyle(linkStyle);

        // Description
        createIndexRow(sheet, 4, 0, "Description:")
                .setCellStyle(boldStyle);
        createIndexRow(sheet, 5,0, "This export shows a single top match for each measured " +
                "feature. Every match displays the following information:");
        createIndexRow(sheet, 6, 1, "File").setCellStyle(boldStyle);
        createIndexRow(sheet, 6, 2, "Index of the input file");
        createIndexRow(sheet, 7, 1, "Feature").setCellStyle(boldStyle);
        createIndexRow(sheet, 7, 2, "Index of the measured feature in the input file");
        createIndexRow(sheet, 8, 1, "Signal ID").setCellStyle(boldStyle);
        createIndexRow(sheet, 8, 2, "Identifier of the measured feature (corresponds to the ID field specified on ADAP-KDB upload page)");
        createIndexRow(sheet, 9, 1, "Signal Name").setCellStyle(boldStyle);
        createIndexRow(sheet, 9, 2, "Name of the measured feature (corresponds to the Name field on ADAP-KDB upload page)");
        createIndexRow(sheet, 10, 1, "Precursor m/z").setCellStyle(boldStyle);
        createIndexRow(sheet, 10, 2, "One or more Precursor m/z values of the measured feature, corresponding to different adduct");
        createIndexRow(sheet, 11, 1, "Adduct").setCellStyle(boldStyle);
        createIndexRow(sheet, 11, 2, "One or more adducts of the measured compound");
        createIndexRow(sheet, 12, 1, "Fragmentation score").setCellStyle(boldStyle);
        createIndexRow(sheet, 12, 2, "Spectrum similarity between the measured feature and library compound");
        createIndexRow(sheet, 13, 1, "Mass Error (PPM)").setCellStyle(boldStyle);
        createIndexRow(sheet, 13, 2, "Difference (in PPM) between masses of the measured feature and library compound");
        createIndexRow(sheet, 14, 1, "Ret Time Error (min)").setCellStyle(boldStyle);
        createIndexRow(sheet, 14, 2, "Difference (in min) between retention times of the measured feature and library compound");
        createIndexRow(sheet, 15, 1, "Ontology Level").setCellStyle(boldStyle);
        createIndexRow(sheet, 15, 2, "One of the ontology levels");
        createIndexRow(sheet, 16, 1, "Compound Name").setCellStyle(boldStyle);
        createIndexRow(sheet, 16, 2, "Name of the library compound");
        createIndexRow(sheet, 17, 1, "Compound ID").setCellStyle(boldStyle);
        createIndexRow(sheet, 17, 2, "Identifier of the library compound");
        createIndexRow(sheet, 18, 1, "Formula").setCellStyle(boldStyle);
        createIndexRow(sheet, 18, 2, "Molecular formula of the library compound");
        createIndexRow(sheet, 19, 1, "Mass").setCellStyle(boldStyle);
        createIndexRow(sheet, 19, 2, "Mass (in Da) of the library compound");
        createIndexRow(sheet, 20, 1, "Ret Time (min)").setCellStyle(boldStyle);
        createIndexRow(sheet, 20, 2, "Retention time (in min) of the library compound");
        createIndexRow(sheet, 21, 1, "Library").setCellStyle(boldStyle);
        createIndexRow(sheet, 21, 2, "Name of the compound library");

        sheet.setColumnWidth(1, 20*256);
    }

    private Cell createIndexRow(Sheet sheet, int rowIndex, int columnIndex, String text) {
        Row row = sheet.getRow(rowIndex);
        if (row == null)
            row = sheet.createRow(rowIndex);
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(text);
        return cell;
    }

    private int createHeader(int rowCount, Sheet sheet, Map<ExportCategory, CellStyle> styles) {

        // First row. Contains categories of export fields
        rowCount = createFirstHeaderRow(rowCount, sheet, styles);

        // Second row. Contains names of export fields
        Row row = sheet.createRow(rowCount++);
        int columnCount = 0;
        for (ExportCategory exportCategory : ExportCategory.valuesWithNull()) {
            for (ExportField field : ExportField.values(exportCategory)) {
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(field.name);
                if (field.exportCategory != null) {
                    CellStyle style = styles.get(field.exportCategory);
                    cell.setCellStyle(style);
                }
            }
        }

        return rowCount;
    }

    private int createFirstHeaderRow(int rowCount, Sheet sheet, Map<ExportCategory, CellStyle> styles) {

//        // Create array of categories with the first null value
//        int numCategories = ExportCategory.values().length;
//        ExportCategory[] exportCategories = new ExportCategory[1 + numCategories];
//        System.arraycopy(ExportCategory.values(), 0, exportCategories, 1, numCategories);

        // Create row
        Row row = sheet.createRow(rowCount);
        int columnCount = 0;
        for (ExportCategory exportCategory : ExportCategory.valuesWithNull()) {
            int numFields = ExportField.values(exportCategory).length;
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, columnCount, columnCount + numFields - 1));

            if (exportCategory != null) {
                Cell cell = row.createCell(columnCount);
                cell.setCellValue(exportCategory.label);
                cell.setCellStyle(styles.get(exportCategory));
            }

            columnCount += numFields;
        }
        return rowCount + 1;
    }

    private void createRow(Row row, SearchResultDTO searchResult, boolean highlight, Map<ExportCategory, CellStyle> styles) {
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

                CellStyle style = row.getSheet().getWorkbook().createCellStyle();
                if (field.exportCategory != null) {
                    style.cloneStyleFrom(styles.get(field.exportCategory));
                }
                if (highlight) {
                    Font font = row.getSheet().getWorkbook().createFont();
                    font.setColor(IndexedColors.RED.getIndex());
                    style.setFont(font);
                }
                cell.setCellStyle(style);
            }
        }
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
}
