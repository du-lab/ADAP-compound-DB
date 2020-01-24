package org.dulab.adapcompounddb.site.controllers.forms;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class FilterForm {

    @NotBlank
    private String species;

    @NotBlank
    private String source;

    @NotBlank
    private String disease;

    private final List<String> speciesList;
    private final List<String> sourceList;
    private final List<String> diseaseList;

    public FilterForm(List<String> speciesList, List<String> sourceList, List<String> diseaseList) {
        this.speciesList = speciesList;
        this.sourceList = sourceList;
        this.diseaseList = diseaseList;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public List<String> getSpeciesList() {
        return speciesList;
    }

    public List<String> getSourceList() {
        return sourceList;
    }

    public List<String> getDiseaseList() {
        return diseaseList;
    }
}
