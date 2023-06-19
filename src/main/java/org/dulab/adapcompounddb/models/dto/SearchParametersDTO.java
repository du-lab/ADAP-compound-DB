package org.dulab.adapcompounddb.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.SearchParametersForm;

import java.util.Objects;

@Getter
@Setter
public class SearchParametersDTO {
    //TODO switch names
    @JsonProperty("GAS")
    public ChromatographySearchParametersDTO gas;
    @JsonProperty("LIQUID")
    public ChromatographySearchParametersDTO liquid;

    @JsonProperty("OTHER")
    public ChromatographySearchParametersDTO other;

    public SearchParametersDTO() {
        this.gas = new ChromatographySearchParametersDTO();
        this.liquid = new ChromatographySearchParametersDTO();
        this.other = new ChromatographySearchParametersDTO();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchParametersDTO that = (SearchParametersDTO) o;
        return Objects.equals(gas, that.gas) && Objects.equals(liquid, that.liquid) && Objects.equals(other, that.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gas, liquid, other);
    }

    public ChromatographySearchParametersDTO getChromatographySearchParameters(ChromatographyType chromatographyType) {
        switch (chromatographyType) {
            case GAS:
                return gas;
            case NONE:
                return other;
            case LIQUID_POSITIVE: case LIQUID_NEGATIVE: case LC_MSMS_POS: case LC_MSMS_NEG:
                return liquid;
            default:
                return new ChromatographySearchParametersDTO();
        }
    }

    public static SearchParametersDTO buildSearchParametersDTO(SearchParametersForm searchParametersForm) {
        SearchParametersDTO SearchParametersDTO = new SearchParametersDTO();
        SearchParametersDTO.setGas(
                new ChromatographySearchParametersDTO(searchParametersForm.getScoreThresholdGas(),
                        searchParametersForm.getRetentionIndexToleranceGas(),
                        searchParametersForm.getRetentionIndexMatchGas(),
                        searchParametersForm.getMzToleranceGas(),
                        searchParametersForm.getLimitGas(),
                        searchParametersForm.getMzToleranceTypeGas(),
                        false));
        SearchParametersDTO.setLiquid(
                new ChromatographySearchParametersDTO(searchParametersForm.getScoreThresholdLiquid(),
                        searchParametersForm.getRetentionIndexToleranceLiquid(),
                        searchParametersForm.getRetentionIndexMatchLiquid(),
                        searchParametersForm.getMzToleranceLiquid(),
                        searchParametersForm.getLimitLiquid(),
                        searchParametersForm.getMzToleranceTypeLiquid(),
                        false));
        SearchParametersDTO.setOther(
                new ChromatographySearchParametersDTO(searchParametersForm.getScoreThresholdOther(),
                        searchParametersForm.getRetentionIndexToleranceOther(),
                        searchParametersForm.getRetentionIndexMatchOther(),
                        searchParametersForm.getMzToleranceOther(),
                        searchParametersForm.getLimitOther(),
                        searchParametersForm.getMzToleranceTypeOther(),
                        false));
        return SearchParametersDTO;
    }
}
