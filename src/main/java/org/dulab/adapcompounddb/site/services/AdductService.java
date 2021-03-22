package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.Adduct;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.site.repositories.AdductRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdductService {

    private final AdductRepository adductRepository;
    private final Map<ChromatographyType, List<Adduct>> chromatographyToAdductsMap;


    @Autowired
    public AdductService(AdductRepository adductRepository) {
        this.adductRepository = adductRepository;
        chromatographyToAdductsMap = new HashMap<>();
        for (ChromatographyType chromatography : ChromatographyType.values()) {
            chromatographyToAdductsMap.put(
                    chromatography,
                    MappingUtils.toList(adductRepository.findByChromatography(chromatography)));
        }
    }

    public List<Adduct> findAdductsByChromatography(ChromatographyType chromatographyType) {
        return chromatographyToAdductsMap.get(chromatographyType);
    }
}
