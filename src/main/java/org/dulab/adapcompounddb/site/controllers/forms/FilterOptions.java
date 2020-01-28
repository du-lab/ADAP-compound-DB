package org.dulab.adapcompounddb.site.controllers.forms;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class FilterOptions {

    private final List<String> speciesList;
    private final List<String> sourceList;
    private final List<String> diseaseList;

    public FilterOptions(List<String> speciesList, List<String> sourceList, List<String> diseaseList) {
        this.speciesList = speciesList;
        this.sourceList = sourceList;
        this.diseaseList = diseaseList;
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
