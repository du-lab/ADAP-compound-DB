package org.dulab.adapcompounddb.site.controllers.forms;
import lombok.Getter;
import lombok.Setter;
import org.dulab.adapcompounddb.site.services.search.SearchParameters;

@Getter
@Setter
public class SearchParametersForm {
    private Integer scoreThresholdGas;
    private Integer retentionIndexToleranceGas;
    private SearchParameters.RetIndexMatchType retentionIndexMatchGas = SearchParameters.RetIndexMatchType.IGNORE_MATCH;
    private Double mzToleranceGas;
    private SearchParameters.MzToleranceType mzToleranceTypeGas = SearchParameters.MzToleranceType.DA;
    private Integer limitGas = 100;

    private Integer scoreThresholdLiquid;
    private Integer retentionIndexToleranceLiquid;
    private SearchParameters.RetIndexMatchType retentionIndexMatchLiquid = SearchParameters.RetIndexMatchType.IGNORE_MATCH;
    private Double mzToleranceLiquid;
    private SearchParameters.MzToleranceType mzToleranceTypeLiquid = SearchParameters.MzToleranceType.DA;
    private Integer limitLiquid = 100;

    private Integer scoreThresholdOther;
    private Integer retentionIndexToleranceOther;
    private SearchParameters.RetIndexMatchType retentionIndexMatchOther = SearchParameters.RetIndexMatchType.IGNORE_MATCH;
    private Double mzToleranceOther;
    private SearchParameters.MzToleranceType mzToleranceTypeOther = SearchParameters.MzToleranceType.DA;
    private Integer limitOther = 100;
}
