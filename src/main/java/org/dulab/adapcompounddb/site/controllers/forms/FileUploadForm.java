package org.dulab.adapcompounddb.site.controllers.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.MetaDataMapping.Field;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.FileType;
import org.dulab.adapcompounddb.validation.ContainsFiles;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUploadForm {

    private static final Logger LOGGER = LogManager.getLogger(FileUploadForm.class);


    @JsonIgnore
    @NotNull(message = "Chromatography type must be selected.")
    private ChromatographyType chromatographyType;

//    @NotNull(message = "File format must be chosen.")
//    private FileType fileType;

    private boolean mergeFiles;
    private boolean roundMzValues;
    private boolean editMetadata;

    @JsonIgnore
    @ContainsFiles
    private List<MultipartFile> files;

    private boolean editNameField;
    private boolean editSynonymField;
    private boolean editExternalIdField;
    private boolean editCasNoField;
    private boolean editHmdbField;

    private boolean editKeggField;
    private boolean editPubChemField;
    private boolean editPrecursorMzField;
    private boolean editRetentionTimeField;
    private boolean editRetentionIndexField;
    private boolean editMassField;
    private boolean editFormulaField;
    private boolean editCanonicalSmilesField;
    private boolean editInChiField;

    private boolean editInChiKeyField;
    private boolean editIsotopeField;



    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(final ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }
    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(final List<MultipartFile> files) {
        this.files = files;
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

    public boolean isRoundMzValues() {
        return roundMzValues;
    }

    public void setRoundMzValues(boolean roundMzValues) {
        this.roundMzValues = roundMzValues;
    }

    public boolean isEditMetadata() {
        return editMetadata;
    }

    public void setEditMetadata(boolean editMetadata) {
        this.editMetadata = editMetadata;
    }

    public boolean isEditNameField() {
        return editNameField;
    }

    public void setEditNameField(boolean editNameField) {
        this.editNameField = editNameField;
    }

    public boolean isEditSynonymField() {
        return editSynonymField;
    }

    public void setEditSynonymField(boolean editSynonymField) {
        this.editSynonymField = editSynonymField;
    }

    public boolean isEditExternalIdField() {
        return editExternalIdField;
    }

    public void setEditExternalIdField(boolean editExternalIdField) {
        this.editExternalIdField = editExternalIdField;
    }

    public boolean isEditCasNoField() {
        return editCasNoField;
    }

    public void setEditCasNoField(boolean editCasNoField) {
        this.editCasNoField = editCasNoField;
    }

    public boolean isEditHmdbField() {
        return editHmdbField;
    }

    public void setEditHmdbField(boolean editHmdbField) {
        this.editHmdbField = editHmdbField;
    }

    public boolean isEditPubChemField() {
        return editPubChemField;
    }

    public void setEditPubChemField(boolean editPubChemField) {
        this.editPubChemField = editPubChemField;
    }

    public boolean isEditPrecursorMzField() {
        return editPrecursorMzField;
    }

    public void setEditPrecursorMzField(boolean editPrecursorMzField) {
        this.editPrecursorMzField = editPrecursorMzField;
    }

    public boolean isEditRetentionTimeField() {
        return editRetentionTimeField;
    }

    public void setEditRetentionTimeField(boolean editRetentionTimeField) {
        this.editRetentionTimeField = editRetentionTimeField;
    }

    public boolean isEditRetentionIndexField() {
        return editRetentionIndexField;
    }

    public void setEditRetentionIndexField(boolean editRetentionIndexField) {
        this.editRetentionIndexField = editRetentionIndexField;
    }

    public boolean isEditMassField() {
        return editMassField;
    }

    public void setEditMassField(boolean editMassField) {
        this.editMassField = editMassField;
    }

    public boolean isEditFormulaField() {
        return editFormulaField;
    }

    public void setEditFormulaField(boolean editFormulaField) {
        this.editFormulaField = editFormulaField;
    }

    public boolean isEditCanonicalSmilesField() {
        return editCanonicalSmilesField;
    }

    public void setEditCanonicalSmilesField(boolean editCanonicalSmilesField) {
        this.editCanonicalSmilesField = editCanonicalSmilesField;
    }

    public boolean isEditInChiField() {
        return editInChiField;
    }

    public void setEditInChiField(boolean editInChiField) {
        this.editInChiField = editInChiField;
    }

    public boolean isEditInChiKeyField() {
        return editInChiKeyField;
    }

    public void setEditInChiKeyField(boolean editInChiKeyField) {
        this.editInChiKeyField = editInChiKeyField;
    }

    public boolean isEditIsotopeField() {
        return editIsotopeField;
    }

    public void setEditIsotopeField(boolean editIsotopeField) {
        this.editIsotopeField = editIsotopeField;
    }
    public boolean isEditKeggField() {
        return editKeggField;
    }

    public void setEditKeggField(boolean editKeggField) {
        this.editKeggField = editKeggField;
    }


    
    
}
