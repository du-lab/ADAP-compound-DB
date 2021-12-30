package org.dulab.adapcompounddb.models.ontology;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dulab.adapcompounddb.models.ontology.Parameters.*;


public class OntologySupplier {

    private static final Map<ChromatographyType, List<OntologyLevel>> chromatographyTypeToOntologyLevelsMap;

    static {
        chromatographyTypeToOntologyLevelsMap = new HashMap<>();
        chromatographyTypeToOntologyLevelsMap.put(ChromatographyType.LC_MSMS_POS, new ArrayList<>());
        chromatographyTypeToOntologyLevelsMap.put(ChromatographyType.LC_MSMS_NEG, new ArrayList<>());

        OntologyLevel ol1 = new OntologyLevel("OL_1", 1, true, MZ_TOLERANCE_PPM,
                SCORE_THRESHOLD, PRECURSOR_TOLERANCE_PPM, MASS_TOLERANCE_PPM, RET_TIME_TOLERANCE,
                null);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(ol1);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(ol1);

        OntologyLevel ol2a = new OntologyLevel("OL_2a", 2, true, null,
                null, null,MASS_TOLERANCE_PPM, RET_TIME_TOLERANCE,
                null);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(ol2a);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(ol2a);

        OntologyLevel ol2b = new OntologyLevel("OL_2b", 2, true, MZ_TOLERANCE_PPM,
                SCORE_THRESHOLD, PRECURSOR_TOLERANCE_PPM, MASS_TOLERANCE_PPM,
                null, null);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(ol2b);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(ol2b);

        OntologyLevel pda = new OntologyLevel("PD_A", 2, false, MZ_TOLERANCE_PPM,
                SCORE_THRESHOLD, PRECURSOR_TOLERANCE_PPM, MASS_TOLERANCE_PPM,
                null, null);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(pda);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(pda);

        OntologyLevel pdc = new OntologyLevel("PD_C", 3, false, null,
                null, null, MASS_TOLERANCE_PPM, null,
                ISOTOPIC_SIMILARITY_THRESHOLD);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(pdc);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(pdc);

        OntologyLevel pdd = new OntologyLevel("PD_D", 4, false, null,
                null, null, MASS_TOLERANCE_PPM, null,
                null);

        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(pdd);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(pdd);
    }


    /**
     * Returns sorted priority values of all ontology levels for a given chromatography type
     *
     * @param chromatographyType chromatography type
     * @return sorted arrays of priorities
     */
    @Nullable
    public static int[] findPrioritiesByChromatographyType(ChromatographyType chromatographyType) {
        List<OntologyLevel> ontologyLevels = chromatographyTypeToOntologyLevelsMap.get(chromatographyType);
        if (ontologyLevels == null)
            return null;

        return ontologyLevels.stream()
                .mapToInt(OntologyLevel::getPriority)
                .distinct()
                .sorted()
                .toArray();
    }

    /**
     * Returns all ontology levels for a given chromatography type and priority
     *
     * @param chromatographyType chromatography type
     * @param priority           priority
     * @return array of ontology levels
     */
    @Nullable
    public static OntologyLevel[] findByChromatographyTypeAndPriority(
            ChromatographyType chromatographyType, int priority) {

        List<OntologyLevel> ontologyLevels = chromatographyTypeToOntologyLevelsMap.get(chromatographyType);
        if (ontologyLevels == null)
            return null;

        return ontologyLevels.stream()
                .filter(l -> l.getPriority() == priority)
                .toArray(OntologyLevel[]::new);
    }

    /**
     * Selects the ontology levels based on the provided score and error values
     *
     * @param chromatographyType chromatography type
     * @param score              fragmentation score
     * @param precursorErrorPPM  precursor error in PPM
     * @param massErrorPPM       mass error in PPM
     * @param retTimeError       retention time error
     * @return ontology level or null
     */
    @Nullable
    public static OntologyLevel select(ChromatographyType chromatographyType, boolean inHouseLibrary,
                                       @Nullable Double score, @Nullable Double precursorErrorPPM,
                                       @Nullable Double massErrorPPM, @Nullable Double retTimeError,
                                       @Nullable Double isotopicSimilarity) {

        int[] priorities = findPrioritiesByChromatographyType(chromatographyType);
        if (priorities == null)
            return null;

        for (int priority : priorities) {
            OntologyLevel[] ontologyLevels = findByChromatographyTypeAndPriority(chromatographyType, priority);
            if (ontologyLevels == null)
                continue;

            for (OntologyLevel level : ontologyLevels) {

                if (level.isInHouseLibrary() ^ inHouseLibrary) continue;
                if (level.getScoreThreshold() != null && score == null) continue;
                if (level.getPrecursorTolerancePPM() != null && precursorErrorPPM == null) continue;
                if (level.getMassTolerancePPM() != null && massErrorPPM == null) continue;
                if (level.getRetTimeTolerance() != null && retTimeError == null) continue;
                if (level.getIsotopicSimilarityThreshold() != null && isotopicSimilarity == null) continue;

                if ((level.getScoreThreshold() == null || score > level.getScoreThreshold())
                        && (level.getPrecursorTolerancePPM() == null || precursorErrorPPM < level.getPrecursorTolerancePPM())
                        && (level.getMassTolerancePPM() == null || massErrorPPM < level.getMassTolerancePPM())
                        && (level.getRetTimeTolerance() == null || retTimeError < level.getRetTimeTolerance())
                        && (level.getIsotopicSimilarityThreshold() == null || isotopicSimilarity > level.getIsotopicSimilarityThreshold())) {
                    return level;
                }
            }
        }

        return null;
    }
}
