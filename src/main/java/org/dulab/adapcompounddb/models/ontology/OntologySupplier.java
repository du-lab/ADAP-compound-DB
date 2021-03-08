package org.dulab.adapcompounddb.models.ontology;

import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OntologySupplier {

    private static final Map<ChromatographyType, List<OntologyLevel>> chromatographyTypeToOntologyLevelsMap;

    static {
        chromatographyTypeToOntologyLevelsMap = new HashMap<>();
        chromatographyTypeToOntologyLevelsMap.put(ChromatographyType.LC_MSMS_POS, new ArrayList<>());
        chromatographyTypeToOntologyLevelsMap.put(ChromatographyType.LC_MSMS_NEG, new ArrayList<>());

        OntologyLevel ol1 = new OntologyLevel("OL_1", 1, true, Parameters.MZ_TOLERANCE,
                Parameters.SCORE_THRESHOLD, Parameters.PRECURSOR_TOLERANCE, Parameters.MASS_TOLERANCE_PPM,
                Parameters.RET_TIME_TOLERANCE);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(ol1);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(ol1);

        OntologyLevel ol2a = new OntologyLevel("OL_2a", 2, true, null,
                null, null,  Parameters.MASS_TOLERANCE_PPM, Parameters.RET_TIME_TOLERANCE);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(ol2a);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(ol2a);

        OntologyLevel ol2b = new OntologyLevel("OL_2b", 2, true, Parameters.MZ_TOLERANCE,
                Parameters.SCORE_THRESHOLD, Parameters.PRECURSOR_TOLERANCE, Parameters.MASS_TOLERANCE_PPM, null);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(ol2b);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(ol2b);

        OntologyLevel pda = new OntologyLevel("PD_A", 2, false, Parameters.MZ_TOLERANCE,
                Parameters.SCORE_THRESHOLD, Parameters.PRECURSOR_TOLERANCE, Parameters.MASS_TOLERANCE_PPM, null);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_POS).add(pda);
        chromatographyTypeToOntologyLevelsMap.get(ChromatographyType.LC_MSMS_NEG).add(pda);
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
}
