package org.dulab.adapcompounddb.site.controllers.forms;

import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.dulab.adapcompounddb.models.entities.SubmissionTag;
import org.dulab.adapcompounddb.validation.LogicalAOrNotB;
import org.hibernate.validator.constraints.URL;
import org.json.JSONArray;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@LogicalAOrNotB.List({
        @LogicalAOrNotB(fieldA = "isPrivate", fieldB = "isLibrary", message = "Creating a library is allowed only for private submissions"),
        @LogicalAOrNotB(fieldA = "isLibrary", fieldB = "isInHouseLibrary", message = "Invalid values of the Library and In-House fields")})
public class SubmissionForm {

    private Long id;

    @NotBlank(message = "The field Name is required.")
    private String name;
    private String externalId;

    private String source;
    private String description;
    private boolean isPrivate = true;
    private boolean isLibrary;
    private boolean isInHouseLibrary;
    @URL(message = "The field Reference must be a valid URL.")
    private String reference;
    private String tags;
    private List<Long> submissionCategoryIds;
    private boolean authorized;


    public SubmissionForm() {
    }

    public SubmissionForm(Submission submission) {
        setId(submission.getId());
        setExternalId(submission.getExternalId());
        setName(submission.getName());
        setDescription(submission.getDescription());
        setIsPrivate(submission.isPrivate());
        //setIsInHouseLibrary(submission.isInHouse());
        setReference(submission.getUrl());

        if (submission.getTags() != null) {
            //format tag into the same format created by tagify which is JsonArray
            JSONArray jsonArray = new JSONArray();
            for (SubmissionTag submissionTag : submission.getTags()) {
                jsonArray.put(submissionTag.toString());
            }
            setTags(jsonArray.toString());
        }

        if (submission.getCategories() != null) {
            setSubmissionCategoryIds(submission.getCategories().stream().filter(Objects::nonNull)
                    .map(SubmissionCategory::getId).collect(Collectors.toList()));
        }
    }

    public Long getId() {
        return id;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(final boolean authorized) {
        this.authorized = authorized;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean getIsLibrary() {
        return isLibrary;
    }

    public void setIsLibrary(boolean reference) {
        isLibrary = reference;
    }

    public boolean getIsInHouseLibrary() {
        return isInHouseLibrary;
    }

    public void setIsInHouseLibrary(boolean inHouseLibrary) {
        isInHouseLibrary = inHouseLibrary;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public List<Long> getSubmissionCategoryIds() {
        return submissionCategoryIds;
    }

    public void setSubmissionCategoryIds(final List<Long> submissionCategoryIds) {
        this.submissionCategoryIds = submissionCategoryIds;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
