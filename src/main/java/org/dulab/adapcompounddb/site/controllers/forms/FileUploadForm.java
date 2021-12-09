package org.dulab.adapcompounddb.site.controllers.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.MetaDataMapping.Field;
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
    private String mspSynonymField;
    private String mspExternalIdField;
    private String mspCasNoField;
    private String mspKeggField;
    private String mspPrecursorMzField;
    private String mspRetentionTimeField;
    private String mspRetentionIndexField;
    private String mspMassField;
    private String mspFormulaField;
    private String mspCanonicalSmilesField;
    private String mspInChiField;
    private String mspInChiKeyField;
    private String csvNameField;
    private String csvSynonymField;
    private String csvExternalIdField;
    private String csvCasNoField;
    private String csvKeggField;
    private String csvPrecursorMzField;
    private String csvRetentionTimeField;
    private String csvRetentionIndexField;
    private String csvMassField;
    private String csvFormulaField;
    private String csvCanonicalSmilesField;
    private String csvInChiField;
    private String csvInChiKeyField;

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

    public String getMspSynonymField() {
        return mspSynonymField;
    }

    public void setMspSynonymField(String mspSynonymField) {
        this.mspSynonymField = mspSynonymField;
    }

    public String getMspExternalIdField() {
        return mspExternalIdField;
    }

    public void setMspExternalIdField(String mspExternalIdField) {
        this.mspExternalIdField = mspExternalIdField;
    }

    public String getMspCasNoField() {
        return mspCasNoField;
    }

    public void setMspCasNoField(String mspCasNoField) {
        this.mspCasNoField = mspCasNoField;
    }

    public String getMspKeggField() {
        return mspKeggField;
    }

    public void setMspKeggField(String mspKeggField) {
        this.mspKeggField = mspKeggField;
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

    public String getMspRetentionIndexField() {
        return mspRetentionIndexField;
    }

    public void setMspRetentionIndexField(String mspRetentionIndexField) {
        this.mspRetentionIndexField = mspRetentionIndexField;
    }

    public String getMspMassField() {
        return mspMassField;
    }

    public void setMspMassField(String mspMassField) {
        this.mspMassField = mspMassField;
    }

    public String getMspFormulaField(){
        return mspFormulaField;
    }

    public void setMspFormulaField(String mspFormulaField){
        this.mspFormulaField = mspFormulaField;
    }

    public String getMspCanonicalSmilesField() {
        return mspCanonicalSmilesField;
    }

    public void setMspCanonicalSmilesField(String mspCanonicalSmilesField) {
        this.mspCanonicalSmilesField = mspCanonicalSmilesField;
    }

    public String getMspInChiField() {
        return mspInChiField;
    }

    public void setMspInChiField(String mspInChiField) {
        this.mspInChiField = mspInChiField;
    }

    public String getMspInChiKeyField() {
        return mspInChiKeyField;
    }

    public void setMspInChiKeyField(String mspInChiKeyField) {
        this.mspInChiKeyField = mspInChiKeyField;
    }

    public String getCsvNameField() {
        return csvNameField;
    }

    public void setCsvNameField(String csvNameField) {
        this.csvNameField = csvNameField;
    }

    public String getCsvSynonymField() {
        return csvSynonymField;
    }

    public void setCsvSynonymField(String csvSynonymField) {
        this.csvSynonymField = csvSynonymField;
    }

    public String getCsvExternalIdField() {
        return csvExternalIdField;
    }

    public void setCsvExternalIdField(String csvExternalIdField) {
        this.csvExternalIdField = csvExternalIdField;
    }

    public String getCsvCasNoField() {
        return csvCasNoField;
    }

    public void setCsvCasNoField(String csvCasNoField) {
        this.csvCasNoField = csvCasNoField;
    }

    public String getCsvKeggField() {
        return csvKeggField;
    }

    public void setCsvKeggField(String csvKeggField) {
        this.csvKeggField = csvKeggField;
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

    public String getCsvRetentionIndexField() {
        return csvRetentionIndexField;
    }

    public void setCsvRetentionIndexField(String csvRetentionIndexField) {
        this.csvRetentionIndexField = csvRetentionIndexField;
    }

    public String getCsvMassField() {
        return csvMassField;
    }

    public void setCsvMassField(String csvMassField) {
        this.csvMassField = csvMassField;
    }

    public String getCsvFormulaField(){
        return csvFormulaField;
    }

    public void setCsvFormulaField(String csvFormulaField){
        this.csvFormulaField = csvFormulaField;
    }

    public String getCsvCanonicalSmilesField() {
        return csvCanonicalSmilesField;
    }

    public void setCsvCanonicalSmilesField(String csvCanonicalSmilesField) {
        this.csvCanonicalSmilesField = csvCanonicalSmilesField;
    }

    public String getCsvInChiField() {
        return csvInChiField;
    }

    public void setCsvInChiField(String csvInChiField) {
        this.csvInChiField = csvInChiField;
    }

    public String getCsvInChiKeyField() {
        return csvInChiKeyField;
    }

    public void setCsvInChiKeyField(String csvInChiKeyField) {
        this.csvInChiKeyField = csvInChiKeyField;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(final List<MultipartFile> files) {
        this.files = files;
    }

    public Map<FileType, MetaDataMapping> getMetaDataMappings() {
        Map<FileType, MetaDataMapping> mappings = new HashMap<>();
        mappings.put(FileType.MSP, createMspMapping());
        mappings.put(FileType.CSV, createCsvMapping());
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

    private MetaDataMapping createMspMapping() {
        MetaDataMapping mapping = new MetaDataMapping();
        mapping.setFieldName(Field.NAME, mspNameField);
        mapping.setFieldName(Field.SYNONYM, mspSynonymField);
        mapping.setFieldName(Field.EXTERNAL_ID, mspExternalIdField);
        mapping.setFieldName(Field.CAS_ID, mspCasNoField);
        mapping.setFieldName(Field.KEGG_ID, mspKeggField);
        mapping.setFieldName(Field.PRECURSOR_MZ, mspPrecursorMzField);
        mapping.setFieldName(Field.PRECURSOR_TYPE, null);
        mapping.setFieldName(Field.RETENTION_TIME, mspRetentionTimeField);
        mapping.setFieldName(Field.RETENTION_INDEX, mspRetentionIndexField);
        mapping.setFieldName(Field.MASS, mspMassField);
        mapping.setFieldName(Field.FORMULA, mspFormulaField);
        mapping.setFieldName(Field.SMILES, mspCanonicalSmilesField);
        mapping.setFieldName(Field.INCHI_KEY, mspInChiKeyField);
        mapping.setFieldName(Field.INCHI, mspInChiField);
        return mapping;
    }

    private MetaDataMapping createCsvMapping() {
        MetaDataMapping mapping = new MetaDataMapping();
        mapping.setFieldName(Field.NAME, csvNameField);
        mapping.setFieldName(Field.SYNONYM, csvSynonymField);
        mapping.setFieldName(Field.EXTERNAL_ID, csvExternalIdField);
        mapping.setFieldName(Field.CAS_ID, csvCasNoField);
        mapping.setFieldName(Field.KEGG_ID, csvKeggField);
        mapping.setFieldName(Field.PRECURSOR_MZ, csvPrecursorMzField);
        mapping.setFieldName(Field.PRECURSOR_TYPE, null);
        mapping.setFieldName(Field.RETENTION_TIME, csvRetentionTimeField);
        mapping.setFieldName(Field.RETENTION_INDEX, csvRetentionIndexField);
        mapping.setFieldName(Field.MASS, csvMassField);
        mapping.setFieldName(Field.FORMULA, csvFormulaField);
        mapping.setFieldName(Field.SMILES, csvCanonicalSmilesField);
        mapping.setFieldName(Field.INCHI_KEY, csvInChiKeyField);
        mapping.setFieldName(Field.INCHI, csvInChiField);
        return mapping;
    }
}
