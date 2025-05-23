package org.dulab.adapcompounddb.site.controllers.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.enums.FileType;

import java.util.HashMap;
import java.util.Map;

public class MetadataForm {



    private String mspNameField;
    private String mspSynonymField;
    private String mspExternalIdField;
    private String mspCasNoField;
    private String mspKeggField;
    private String mspHmdbField;
    private String mspPubChemField;
    private String mspRefMetField;
    private String mspPrecursorMzField;
    private String mspRetentionTimeField;
    private String mspRetentionIndexField;
    private String mspMassField;
    private String mspFormulaField;
    private String mspCanonicalSmilesField;
    private String mspInChiField;
    private String mspInChiKeyField;
    private String mspIsotopeField;

    private String csvNameField;
    private String csvSynonymField;
    private String csvExternalIdField;
    private String csvCasNoField;
    private String csvKeggField;
    private String csvHmdbField;
    private String csvPubChemField;
    private String csvRefMetField;
    private String csvPrecursorMzField;
    private String csvRetentionTimeField;
    private String csvRetentionIndexField;
    private String csvMassField;
    private String csvFormulaField;
    private String csvCanonicalSmilesField;
    private String csvInChiField;
    private String csvInChiKeyField;
    private String csvIsotopeField;

    private String mgfNameField;
    private String mgfSynonymField;
    private String mgfExternalIdField;
    private String mgfCasNoField;
    private String mgfKeggField;
    private String mgfHmdbField;
    private String mgfPubChemField;
    private String mgfPrecursorMzField;
    private String mgfRetentionTimeField;



    private String mgfRetentionIndexField;
    private String mgfMassField;
    private String mgfFormulaField;
    private String mgfCanonicalSmilesField;
    private String mgfInChiField;
    private String mgfInChiKeyField;
    private String mgfIsotopeField;
    private boolean mergeFiles;
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

    public String getMspHmdbField() {
        return mspHmdbField;
    }

    public void setMspHmdbField(String mspHmdbField) {
        this.mspHmdbField = mspHmdbField;
    }

    public String getMspPubChemField() {
        return mspPubChemField;
    }

    public void setMspPubChemField(String mspPubChemField) {
        this.mspPubChemField = mspPubChemField;
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

    public String getMspIsotopeField() {
        return mspIsotopeField;
    }

    public void setMspIsotopeField(String mspIsotopeField) {
        this.mspIsotopeField = mspIsotopeField;
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

    public String getCsvHmdbField() {
        return csvHmdbField;
    }

    public void setCsvHmdbField(String csvHmdbField) {
        this.csvHmdbField = csvHmdbField;
    }

    public String getCsvPubChemField() {
        return csvPubChemField;
    }

    public void setCsvPubChemField(String csvPubChemField) {
        this.csvPubChemField = csvPubChemField;
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

    public String getCsvIsotopeField() {
        return csvIsotopeField;
    }

    public void setCsvIsotopeField(String csvIsotopeField) {
        this.csvIsotopeField = csvIsotopeField;
    }


    public String getMgfNameField() {
        return mgfNameField;
    }

    public void setMgfNameField(String mgfNameField) {
        this.mgfNameField = mgfNameField;
    }

    public String getMgfSynonymField() {
        return mgfSynonymField;
    }

    public void setMgfSynonymField(String mgfSynonymField) {
        this.mgfSynonymField = mgfSynonymField;
    }

    public String getMgfExternalIdField() {
        return mgfExternalIdField;
    }

    public void setMgfExternalIdField(String mgfExternalIdField) {
        this.mgfExternalIdField = mgfExternalIdField;
    }

    public String getMgfCasNoField() {
        return mgfCasNoField;
    }

    public void setMgfCasNoField(String mgfCasNoField) {
        this.mgfCasNoField = mgfCasNoField;
    }

    public String getMgfKeggField() {
        return mgfKeggField;
    }

    public void setMgfKeggField(String mgfKeggField) {
        this.mgfKeggField = mgfKeggField;
    }

    public String getMgfHmdbField() {
        return mgfHmdbField;
    }

    public void setMgfHmdbField(String mgfHmdbField) {
        this.mgfHmdbField = mgfHmdbField;
    }

    public String getMgfPubChemField() {
        return mgfPubChemField;
    }

    public void setMgfPubChemField(String mgfPubChemField) {
        this.mgfPubChemField = mgfPubChemField;
    }

    public String getMgfPrecursorMzField() {
        return mgfPrecursorMzField;
    }

    public void setMgfPrecursorMzField(String mgfPrecursorMzField) {
        this.mgfPrecursorMzField = mgfPrecursorMzField;
    }

    public String getMgfRetentionTimeField() {
        return mgfRetentionTimeField;
    }

    public void setMgfRetentionTimeField(String mgfRetentionTimeField) {
        this.mgfRetentionTimeField = mgfRetentionTimeField;
    }

    public String getMgfRetentionIndexField() {
        return mgfRetentionIndexField;
    }

    public void setMgfRetentionIndexField(String mgfRetentionIndexField) {
        this.mgfRetentionIndexField = mgfRetentionIndexField;
    }

    public String getMgfMassField() {
        return mgfMassField;
    }

    public void setMgfMassField(String mgfMassField) {
        this.mgfMassField = mgfMassField;
    }

    public String getMgfFormulaField() {
        return mgfFormulaField;
    }

    public void setMgfFormulaField(String mgfFormulaField) {
        this.mgfFormulaField = mgfFormulaField;
    }

    public String getMgfCanonicalSmilesField() {
        return mgfCanonicalSmilesField;
    }

    public void setMgfCanonicalSmilesField(String mgfCanonicalSmilesField) {
        this.mgfCanonicalSmilesField = mgfCanonicalSmilesField;
    }

    public String getMgfInChiField() {
        return mgfInChiField;
    }

    public void setMgfInChiField(String mgfInChiField) {
        this.mgfInChiField = mgfInChiField;
    }

    public String getMgfInChiKeyField() {
        return mgfInChiKeyField;
    }

    public void setMgfInChiKeyField(String mgfInChiKeyField) {
        this.mgfInChiKeyField = mgfInChiKeyField;
    }

    public String getMgfIsotopeField() {
        return mgfIsotopeField;
    }

    public void setMgfIsotopeField(String mgfIsotopeField) {
        this.mgfIsotopeField = mgfIsotopeField;
    }

    public String getMspRefMetField() {
        return mspRefMetField;
    }

    public void setMspRefMetField(String mspRefMetField) {
        this.mspRefMetField = mspRefMetField;
    }

    public String getCsvRefMetField() {
        return csvRefMetField;
    }

    public void setCsvRefMetField(String csvRefMetField) {
        this.csvRefMetField = csvRefMetField;
    }

    @JsonIgnore
    public Map<FileType, MetaDataMapping> getMetaDataMappings() {
        Map<FileType, MetaDataMapping> mappings = new HashMap<>();
        mappings.put(FileType.MSP, createMspMapping());
        mappings.put(FileType.CSV, createCsvMapping());
        mappings.put(FileType.MGF, createMgfMapping());
        return mappings;
    }

//    public byte[] toJsonBytes() {
//        try {
//            return OBJECT_MAPPER.writeValueAsBytes(this);
//        } catch (JsonProcessingException e) {
//            LOGGER.warn("Cannot convert FileUploadForm to Json: " + e.getMessage(), e);
//            return new byte[0];
//        }
//    }

//    public static FileUploadForm fromJsonBytes(byte[] jsonBytes) throws IOException {
//        return OBJECT_MAPPER.readValue(jsonBytes, FileUploadForm.class);
//    }

    @JsonIgnore
    private MetaDataMapping createMspMapping() {
        MetaDataMapping mapping = new MetaDataMapping();
        mapping.setFieldName(MetaDataMapping.Field.NAME, mspNameField);
        mapping.setFieldName(MetaDataMapping.Field.SYNONYM, mspSynonymField);
        mapping.setFieldName(MetaDataMapping.Field.EXTERNAL_ID, mspExternalIdField);
        mapping.setFieldName(MetaDataMapping.Field.CAS_ID, mspCasNoField);
        mapping.setFieldName(MetaDataMapping.Field.HMDB_ID, mspHmdbField);
        mapping.setFieldName(MetaDataMapping.Field.KEGG_ID, mspKeggField);
        mapping.setFieldName(MetaDataMapping.Field.PUBCHEM_ID, mspPubChemField);
        mapping.setFieldName(MetaDataMapping.Field.REFMET_ID, mspRefMetField);
        mapping.setFieldName(MetaDataMapping.Field.PRECURSOR_MZ, mspPrecursorMzField);
        mapping.setFieldName(MetaDataMapping.Field.PRECURSOR_TYPE, null);
        mapping.setFieldName(MetaDataMapping.Field.RETENTION_TIME, mspRetentionTimeField);
        mapping.setFieldName(MetaDataMapping.Field.RETENTION_INDEX, mspRetentionIndexField);
        mapping.setFieldName(MetaDataMapping.Field.MASS, mspMassField);
        mapping.setFieldName(MetaDataMapping.Field.FORMULA, mspFormulaField);
        mapping.setFieldName(MetaDataMapping.Field.SMILES, mspCanonicalSmilesField);
        mapping.setFieldName(MetaDataMapping.Field.INCHI_KEY, mspInChiKeyField);
        mapping.setFieldName(MetaDataMapping.Field.INCHI, mspInChiField);
        mapping.setFieldName(MetaDataMapping.Field.ISOTOPIC_DISTRIBUTION, mspIsotopeField);
        return mapping;
    }

    @JsonIgnore
    private MetaDataMapping createCsvMapping() {
        MetaDataMapping mapping = new MetaDataMapping();
        mapping.setFieldName(MetaDataMapping.Field.NAME, csvNameField);
        mapping.setFieldName(MetaDataMapping.Field.SYNONYM, csvSynonymField);
        mapping.setFieldName(MetaDataMapping.Field.EXTERNAL_ID, csvExternalIdField);
        mapping.setFieldName(MetaDataMapping.Field.CAS_ID, csvCasNoField);
        mapping.setFieldName(MetaDataMapping.Field.HMDB_ID, csvHmdbField);
        mapping.setFieldName(MetaDataMapping.Field.KEGG_ID, csvKeggField);
        mapping.setFieldName(MetaDataMapping.Field.PUBCHEM_ID, csvPubChemField);
        mapping.setFieldName(MetaDataMapping.Field.REFMET_ID, csvRefMetField);
        mapping.setFieldName(MetaDataMapping.Field.PRECURSOR_MZ, csvPrecursorMzField);
        mapping.setFieldName(MetaDataMapping.Field.PRECURSOR_TYPE, null);
        mapping.setFieldName(MetaDataMapping.Field.RETENTION_TIME, csvRetentionTimeField);
        mapping.setFieldName(MetaDataMapping.Field.RETENTION_INDEX, csvRetentionIndexField);
        mapping.setFieldName(MetaDataMapping.Field.MASS, csvMassField);
        mapping.setFieldName(MetaDataMapping.Field.FORMULA, csvFormulaField);
        mapping.setFieldName(MetaDataMapping.Field.SMILES, csvCanonicalSmilesField);
        mapping.setFieldName(MetaDataMapping.Field.INCHI_KEY, csvInChiKeyField);
        mapping.setFieldName(MetaDataMapping.Field.INCHI, csvInChiField);
        mapping.setFieldName(MetaDataMapping.Field.ISOTOPIC_DISTRIBUTION, csvIsotopeField);
        return mapping;
    }

    @JsonIgnore
    private MetaDataMapping createMgfMapping() {
        MetaDataMapping mapping = new MetaDataMapping();
        mapping.setFieldName(MetaDataMapping.Field.NAME, mgfNameField);
        mapping.setFieldName(MetaDataMapping.Field.SYNONYM, mgfSynonymField);
        mapping.setFieldName(MetaDataMapping.Field.EXTERNAL_ID, mgfExternalIdField);
        mapping.setFieldName(MetaDataMapping.Field.CAS_ID, mgfCasNoField);
        mapping.setFieldName(MetaDataMapping.Field.HMDB_ID, mgfHmdbField);
        mapping.setFieldName(MetaDataMapping.Field.KEGG_ID, mgfKeggField);
        mapping.setFieldName(MetaDataMapping.Field.PUBCHEM_ID, mgfPubChemField);
        mapping.setFieldName(MetaDataMapping.Field.PRECURSOR_MZ, mgfPrecursorMzField);
        mapping.setFieldName(MetaDataMapping.Field.PRECURSOR_TYPE, null);
        mapping.setFieldName(MetaDataMapping.Field.RETENTION_TIME, mgfRetentionTimeField);
        mapping.setFieldName(MetaDataMapping.Field.RETENTION_INDEX, mgfRetentionIndexField);
        mapping.setFieldName(MetaDataMapping.Field.MASS, mgfMassField);
        mapping.setFieldName(MetaDataMapping.Field.FORMULA, mgfFormulaField);
        mapping.setFieldName(MetaDataMapping.Field.SMILES, mgfCanonicalSmilesField);
        mapping.setFieldName(MetaDataMapping.Field.INCHI_KEY, mgfInChiKeyField);
        mapping.setFieldName(MetaDataMapping.Field.INCHI, mgfInChiField);
        mapping.setFieldName(MetaDataMapping.Field.ISOTOPIC_DISTRIBUTION, mgfIsotopeField);
        return mapping;
    }
}
