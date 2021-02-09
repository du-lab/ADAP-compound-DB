package org.dulab.adapcompounddb.site.controllers.forms;

import java.util.Map;
import java.util.Set;

public class FilterForm {

    private String species;
    private String source;
    private String disease;
    private Set<Long> submissionIds;

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

    public Set<Long> getSubmissionIds() {
        return submissionIds;
    }

    public void setSubmissionIds(Set<Long> submissionIds) {
        this.submissionIds = submissionIds;
    }
}
