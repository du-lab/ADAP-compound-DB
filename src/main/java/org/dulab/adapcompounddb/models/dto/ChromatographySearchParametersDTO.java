package org.dulab.adapcompounddb.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.controllers.forms.SearchParametersForm;

import java.util.Objects;

@Getter
@Setter
public class ChromatographySearchParametersDTO {
    //TODO switch names
    @JsonProperty("GAS")
    public SearchParametersDTO gas;
    @JsonProperty("LIQUID")
    public SearchParametersDTO liquid;

    @JsonProperty("OTHER")
    public SearchParametersDTO other;

    public ChromatographySearchParametersDTO() {
        this.gas = new SearchParametersDTO();
        this.liquid = new SearchParametersDTO();
        this.other = new SearchParametersDTO();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChromatographySearchParametersDTO that = (ChromatographySearchParametersDTO) o;
        return Objects.equals(gas, that.gas) && Objects.equals(liquid, that.liquid) && Objects.equals(other, that.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gas, liquid, other);
    }

    public SearchParametersDTO getChromatographySearchParameters(ChromatographyType chromatographyType) {
        switch (chromatographyType) {
            case GAS:
                return gas;
            case NONE:
                return other;
            case LIQUID_POSITIVE: case LIQUID_NEGATIVE: case LC_MSMS_POS: case LC_MSMS_NEG:
                return liquid;
            default:
                return new SearchParametersDTO();
        }
    }

    public static ChromatographySearchParametersDTO buildSearchParametersDTO(SearchParametersForm searchParametersForm) {
        ChromatographySearchParametersDTO chromatographySearchParametersDTO = new ChromatographySearchParametersDTO();
        chromatographySearchParametersDTO.setGas(
                new SearchParametersDTO(searchParametersForm.getScoreThresholdGas(),
                        searchParametersForm.getRetentionIndexToleranceGas(),
                        searchParametersForm.getRetentionIndexMatchGas(),
                        searchParametersForm.getMzToleranceGas(),
                        searchParametersForm.getLimitGas(),
                        searchParametersForm.getMzToleranceTypeGas(),
                        false));
        chromatographySearchParametersDTO.setLiquid(
                new SearchParametersDTO(searchParametersForm.getScoreThresholdLiquid(),
                        searchParametersForm.getRetentionIndexToleranceLiquid(),
                        searchParametersForm.getRetentionIndexMatchLiquid(),
                        searchParametersForm.getMzToleranceLiquid(),
                        searchParametersForm.getLimitLiquid(),
                        searchParametersForm.getMzToleranceTypeLiquid(),
                        false));
        chromatographySearchParametersDTO.setOther(
                new SearchParametersDTO(searchParametersForm.getScoreThresholdOther(),
                        searchParametersForm.getRetentionIndexToleranceOther(),
                        searchParametersForm.getRetentionIndexMatchOther(),
                        searchParametersForm.getMzToleranceOther(),
                        searchParametersForm.getLimitOther(),
                        searchParametersForm.getMzToleranceTypeOther(),
                        false));
        return chromatographySearchParametersDTO;
    }
}
