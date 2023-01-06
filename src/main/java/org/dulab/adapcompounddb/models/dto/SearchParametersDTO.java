package org.dulab.adapcompounddb.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchParametersDTO {

    private Double scoreThreshold = 0.5;

    private Double retentionIndexTolerance = 50.0;

    private SearchParameters.RetIndexMatchType retentionIndexMatch = SearchParameters.RetIndexMatchType.IGNORE_MATCH;

    private Double mzTolerance = 0.01;

    private Integer limit = 100;

    private SearchParameters.MzToleranceType mzToleranceType = SearchParameters.MzToleranceType.DA;
}
