package org.dulab.adapcompounddb.site.controllers.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.validation.ContainsFiles;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUploadForm {

    private static final Logger LOGGER = LogManager.getLogger(FileUploadForm.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @JsonIgnore
    @NotNull(message = "Chromatography type must be selected.")
    private ChromatographyType chromatographyType;

//    @NotNull(message = "File format must be chosen.")
//    private FileType fileType;

    private boolean mergeFiles;

    private String mspNameField;
    private String mspExternalIdField;
    private String mspPrecursorMzField;
    private String mspRetentionTimeField;
    private String mspMassField;
    private String csvNameField;
    private String csvExternalIdField;
    private String csvPrecursorMzField;
    private String csvRetentionTimeField;
    private String csvMassField;

    @JsonIgnore
    @ContainsFiles
    private List<MultipartFile> files;


    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(final ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

//    public FileType getFileType() {
//        return fileType;
//    }
//
//    public void setFileType(final FileType fileType) {
//        this.fileType = fileType;
//    }


    public boolean isMergeFiles() {
        return mergeFiles;
    }

    public void setMergeFiles(boolean mergeFiles) {
        this.mergeFiles = mergeFiles;
    }

    public String getMspNameField() {
        return mspNameField;
    }

    public void setMspNameField(String mspNameField) {
        this.mspNameField = mspNameField;
    }

    public String getMspExternalIdField() {
        return mspExternalIdField;
    }

    public void setMspExternalIdField(String mspExternalIdField) {
        this.mspExternalIdField = mspExternalIdField;
    }

    public String getMspPrecursorMzField() {
        return mspPrecursorMzField;
    }

    public void setMspPrecursorMzField(String mspPrecursorMzField) {
        this.mspPrecursorMzField = mspPrecursorMzField;
    }

    public String getMspRetentionTimeField() {
        return mspRetentionTimeField;
    }

    public void setMspRetentionTimeField(String mspRetentionTimeField) {
        this.mspRetentionTimeField = mspRetentionTimeField;
    }

    public String getMspMassField() {
        return mspMassField;
    }

    public void setMspMassField(String mspMassField) {
        this.mspMassField = mspMassField;
    }

    public String getCsvNameField() {
        return csvNameField;
    }

    public void setCsvNameField(String csvNameField) {
        this.csvNameField = csvNameField;
    }

    public String getCsvExternalIdField() {
        return csvExternalIdField;
    }

    public void setCsvExternalIdField(String csvExternalIdField) {
        this.csvExternalIdField = csvExternalIdField;
    }

    public String getCsvPrecursorMzField() {
        return csvPrecursorMzField;
    }

    public void setCsvPrecursorMzField(String csvPrecursorMzField) {
        this.csvPrecursorMzField = csvPrecursorMzField;
    }

    public String getCsvRetentionTimeField() {
        return csvRetentionTimeField;
    }

    public void setCsvRetentionTimeField(String csvRetentionTimeField) {
        this.csvRetentionTimeField = csvRetentionTimeField;
    }

    public String getCsvMassField() {
        return csvMassField;
    }

    public void setCsvMassField(String csvMassField) {
        this.csvMassField = csvMassField;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(final List<MultipartFile> files) {
        this.files = files;
    }

    public Map<FileType, MetaDataMapping> getMetaDataMappings() {
        Map<FileType, MetaDataMapping> mappings = new HashMap<>();
        mappings.put(FileType.MSP, new MetaDataMapping(
                mspNameField, mspExternalIdField, mspPrecursorMzField, null, mspRetentionTimeField, mspMassField, null));
        mappings.put(FileType.CSV, new MetaDataMapping(
                csvNameField, csvExternalIdField, csvPrecursorMzField, null, csvRetentionTimeField, csvMassField, null));
        return mappings;
    }

    public byte[] toJsonBytes() {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(this);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Cannot convert FileUploadForm to Json: " + e.getMessage(), e);
            return new byte[0];
        }
    }

    public static FileUploadForm fromJsonBytes(byte[] jsonBytes) throws IOException {
        return OBJECT_MAPPER.readValue(jsonBytes, FileUploadForm.class);
    }
}
