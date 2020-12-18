package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchServiceSelector {

    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;

    @Autowired
    public SearchServiceSelector(@Qualifier("spectrumSearchServiceGCImpl") SpectrumSearchService gcSpectrumSearchService,
                                 @Qualifier("spectrumSearchServiceLCImpl") SpectrumSearchService lcSpectrumSearchService,
                                 @Qualifier("massSearchService") SpectrumSearchService massSearchService) {

        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, gcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.NONE, massSearchService);
    }

    public SpectrumSearchService findByChromatographyType(ChromatographyType chromatographyType) {
        SpectrumSearchService spectrumSearchService = spectrumSearchServiceMap.get(chromatographyType);
        if (spectrumSearchService == null)
            throw new IllegalArgumentException(
                    "Cannot find SpectrumSearchService for chromatography type " + chromatographyType);
        return spectrumSearchService;
    }
}
