package org.dulab.adapcompounddb.site.controllers.forms;
import lombok.Getter;
import lombok.Setter;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;

@Getter
@Setter
public class SearchParametersForm {
    private Integer gasScoreThreshold;
    private Integer gasRetentionIndexTolerance;
    private SearchParameters.RetIndexMatchType gasRetentionIndexMatch = SearchParameters.RetIndexMatchType.IGNORE_MATCH;
    private Double gasMZTolerance;
    private SearchParameters.MzToleranceType gasMZToleranceType = SearchParameters.MzToleranceType.DA;
    private Integer gasLimit = 100;

    private Integer liquidScoreThreshold;
    private Integer liquidRetentionIndexTolerance;
    private SearchParameters.RetIndexMatchType liquidRetentionIndexMatch = SearchParameters.RetIndexMatchType.IGNORE_MATCH;
    private Double liquidMZTolerance;
    private SearchParameters.MzToleranceType liquidMZToleranceType = SearchParameters.MzToleranceType.DA;
    private Integer liquidLimit = 100;

    private Integer otherScoreThreshold;
    private Integer otherRetentionIndexTolerance;
    private SearchParameters.RetIndexMatchType otherRetentionIndexMatch = SearchParameters.RetIndexMatchType.IGNORE_MATCH;
    private Double otherMZTolerance;
    private SearchParameters.MzToleranceType otherMZToleranceType = SearchParameters.MzToleranceType.DA;
    private Integer otherLimit = 100;
}
