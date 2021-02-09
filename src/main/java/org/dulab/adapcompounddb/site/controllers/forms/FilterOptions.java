package org.dulab.adapcompounddb.site.controllers.forms;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.util.List;
import java.util.SortedMap;

public class FilterOptions {

    private final ChromatographyType[] chromatographyTypes;
    private final List<String> speciesList;
    private final List<String> sourceList;
    private final List<String> diseaseList;
    private final SortedMap<Long, String> submissions;

    public FilterOptions(List<String> speciesList, List<String> sourceList, List<String> diseaseList,
                         SortedMap<Long, String> submissions) {
        this.speciesList = speciesList;
        this.sourceList = sourceList;
        this.diseaseList = diseaseList;
        this.chromatographyTypes = ChromatographyType.values();
        this.submissions = submissions;
    }

    public ChromatographyType[] getChromatographyTypes() {
        return chromatographyTypes;
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

    public SortedMap<Long, String> getSubmissions() {
        return submissions;
    }
}
