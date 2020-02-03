package org.dulab.adapcompounddb.site.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.SubmissionCategory;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SubmissionService {

    Submission findSubmission(long submissionId);

    List<Submission> findSubmissionsByUserId(long userId);

    List<Submission> findSubmissionsWithTagsByUserId(long userId);

    void saveSubmission(@NotNull(message = "The submission is required.") Submission submission);

    void deleteSubmission(Submission submission);

    void delete(long submissionId);

    List<String> findUniqueTagStrings();

    List<SubmissionCategory> findAllCategories();

    List<SubmissionCategory> findAllCategories(SubmissionCategoryType type);

    long countSubmissionsByCategoryId(long submissionCategoryId);

    Optional<SubmissionCategory> findSubmissionCategory(long submissionCategoryId);

    void saveSubmissionCategory(SubmissionCategory category);

    void deleteSubmissionCategory(long submissionCategoryId);

    DataTableResponse findAllSubmissionsForResponse(String searchStr, Integer start, Integer length, Integer column,
            String orderDirection);

//    List<String> findTagsFromACluster(Long clusterId);

    Map<String, List<String>> groupTags(List<String> tags);
}
