package org.dulab.adapcompounddb.site.services.io;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucar.httpservices.HTTPSession;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import static org.dulab.adapcompounddb.site.services.io.ExportUtils.*;
import static org.dulab.adapcompounddb.site.services.io.ExportUtils.isInteger;

@Service
public class ExcelExportSubmissionService {

    private final SubmissionRepository submissionRepository;

    @Autowired
    public ExcelExportSubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission exportSubmission(HttpSession session, OutputStream outputStream, long submissionId) throws IOException {
        Submission submission;
        if (submissionId == 0) {
            submission = (Submission) session.getAttribute("submission");
            if (submission == null)
                throw new IllegalStateException("No submission is found in the session");
        } else {
            submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Cannot find submission with ID = %d", submissionId)));
        }
        return exportSubmission(outputStream, submission);
    }

    public Submission exportSubmission(OutputStream outputStream, Submission submission) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int rowCount = 0;

        ExcelCellStyleSupplier styleSupplier = new ExcelCellStyleSupplier(workbook);
        ExportField[] fields = ExportField.values();

        // Header
        Row headerRow = sheet.createRow(rowCount++);
        for (int i = 0; i < fields.length; ++i) {
            ExportField field = fields[i];
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(field.name);
        }

        // Body
        for (File file : submission.getFiles()) {
            for (Spectrum spectrum : file.getSpectra()) {
                Row row = sheet.createRow(rowCount++);
                for (int i = 0; i < fields.length; ++i) {
                    ExportField field = fields[i];
                    Cell cell = row.createCell(i);
                    String value = field.getter.apply(spectrum);
                    if (isDouble(value))
                        cell.setCellValue(Double.parseDouble(value));
                    else if (isInteger(value))
                        cell.setCellValue(Integer.parseInt(value));
                    else
                        cell.setCellValue(value);
                }
            }
        }

        workbook.write(outputStream);

        return submission;
    }


    enum ExportField {

        NAME("Name", Spectrum::getShortName),
        EXTERNAL_ID("ID", Spectrum::getExternalId),
        SPECTRUM("Fragmentation Spectrum", s -> formatBoolean(s.getPeaks() != null)),
        MASS_RESOLUTION("Mass Resolution", s -> s.isIntegerMz() ? "Low-Res" : "High-Res"),
        PRECURSOR_MZ("Precursor m/z (Da)", s -> formatDouble(s.getPrecursor(), 4)),
        PRECURSOR_TYPE("Precursor Type", Spectrum::getPrecursorType),
        RETENTION_TIME("Retention time (min)", s -> formatDouble(s.getRetentionTime(), 3)),
        MASS("Exact Mass (Da)", s -> formatDouble(s.getMass(), 4)),
        FORMULA("Formula", Spectrum::getFormula),
        SIGNIFICANCE("Significance p-value", s -> formatDouble(s.getSignificance(), 4));


        final String name;
        final Function<Spectrum, String> getter;

        ExportField(String name, Function<Spectrum, String> getter) {
            this.name = name;
            this.getter = getter;
        }
    }
}
