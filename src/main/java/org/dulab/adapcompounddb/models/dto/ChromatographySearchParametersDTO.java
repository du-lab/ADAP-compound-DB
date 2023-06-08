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

    public static ChromatographySearchParametersDTO buildSearchParametersDTO(SearchParametersForm searchParametersForm) {
        ChromatographySearchParametersDTO chromatographySearchParametersDTO = new ChromatographySearchParametersDTO();
        chromatographySearchParametersDTO.setGas(
                new SearchParametersDTO(searchParametersForm.getGasScoreThreshold(),
                        searchParametersForm.getGasRetentionIndexTolerance(),
                        searchParametersForm.getGasRetentionIndexMatch(),
                        searchParametersForm.getGasMZTolerance(),
                        searchParametersForm.getGasLimit(),
                        searchParametersForm.getGasMZToleranceType(),
                        false));
        chromatographySearchParametersDTO.setLiquid(
                new SearchParametersDTO(searchParametersForm.getLiquidScoreThreshold(),
                        searchParametersForm.getLiquidRetentionIndexTolerance(),
                        searchParametersForm.getLiquidRetentionIndexMatch(),
                        searchParametersForm.getLiquidMZTolerance(),
                        searchParametersForm.getLiquidLimit(),
                        searchParametersForm.getLiquidMZToleranceType(),
                        false));
        chromatographySearchParametersDTO.setOther(
                new SearchParametersDTO(searchParametersForm.getOtherScoreThreshold(),
                        searchParametersForm.getOtherRetentionIndexTolerance(),
                        searchParametersForm.getOtherRetentionIndexMatch(),
                        searchParametersForm.getOtherMZTolerance(),
                        searchParametersForm.getOtherLimit(),
                        searchParametersForm.getOtherMZToleranceType(),
                        false));
        return chromatographySearchParametersDTO;
    }
}
