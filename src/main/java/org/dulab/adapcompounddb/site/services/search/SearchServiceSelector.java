package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Deprecated
public class SearchServiceSelector {

    private final Map<ChromatographyType, IndividualSearchService> spectrumSearchServiceMap;

    @Autowired
    public SearchServiceSelector(IndividualSearchService individualSearchService) {  // @Qualifier("spectrumSearchServiceImpl")

        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, individualSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, individualSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, individualSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, individualSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, individualSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.NONE, individualSearchService);
    }

    public IndividualSearchService findByChromatographyType(ChromatographyType chromatographyType) {
        IndividualSearchService spectrumSearchService = spectrumSearchServiceMap.get(chromatographyType);
        if (spectrumSearchService == null)
            throw new IllegalArgumentException(
                    "Cannot find IndividualSearchService for chromatography type " + chromatographyType);
        return spectrumSearchService;
    }
}
