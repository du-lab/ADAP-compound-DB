package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchServiceSelector {

    private final Map<ChromatographyType, IndividualSearchService> spectrumSearchServiceMap;

    @Autowired
    public SearchServiceSelector(@Qualifier("spectrumSearchServiceImpl") IndividualSearchService spectrumSearchService,
                                 @Qualifier("spectrumAndPrecursorSearchServiceImpl") IndividualSearchService spectrumAndPrecursorSearchService,
                                 @Qualifier("massSearchService") IndividualSearchService massSearchService) {

        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, spectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, spectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, spectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, spectrumAndPrecursorSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, spectrumAndPrecursorSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.NONE, massSearchService);
    }

    public IndividualSearchService findByChromatographyType(ChromatographyType chromatographyType) {
        IndividualSearchService spectrumSearchService = spectrumSearchServiceMap.get(chromatographyType);
        if (spectrumSearchService == null)
            throw new IllegalArgumentException(
                    "Cannot find IndividualSearchService for chromatography type " + chromatographyType);
        return spectrumSearchService;
    }
}
