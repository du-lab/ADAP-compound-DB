package org.dulab.adapcompounddb.site.services.io;

import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExcelCellStyleSupplier {

    private final Workbook workbook;
    private final Map<Integer, CellStyle> cellStyleMap;

    public ExcelCellStyleSupplier(Workbook workbook) {
        this.workbook = workbook;
        this.cellStyleMap = new HashMap<>();
    }

    public CellStyle getCellStyle(IndexedColors fillColor, BorderStyle borderStyle,
                                  IndexedColors fontColor) {

        int hash = Objects.hash(fillColor, borderStyle, fontColor);

        CellStyle cellStyle = cellStyleMap.get(hash);

        if (cellStyle != null)
            return cellStyle;

        cellStyle = workbook.createCellStyle();

        cellStyle.setFillForegroundColor(fillColor.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);

        Font font = workbook.createFont();
        font.setColor(fontColor.getIndex());
        cellStyle.setFont(font);

        cellStyleMap.put(hash, cellStyle);

        return cellStyle;
    }
}
