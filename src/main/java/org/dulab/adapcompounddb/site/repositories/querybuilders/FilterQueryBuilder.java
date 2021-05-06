package org.dulab.adapcompounddb.site.repositories.querybuilders;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class FilterQueryBuilder {

    private final String tagQueryBlock;

    public FilterQueryBuilder(String species, String source, String disease) {

        String pattern = "(SubmissionTag.TagKey = '%s' AND SubmissionTag.TagValue = '%s')";

        String speciesBlock = (species != null && !species.equalsIgnoreCase("all"))
                ? String.format(pattern, "species (common)", species) : null;
        String sourceBlock = (source != null && !source.equalsIgnoreCase("all"))
                ? String.format(pattern, "sample source", source) : null;
        String diseaseBlock = (disease != null && !disease.equalsIgnoreCase("all"))
                ? String.format(pattern, "disease", disease) : null;

        String queryBlock = Arrays.stream(new String[]{speciesBlock, sourceBlock, diseaseBlock})
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" OR "));

        if (queryBlock.length() > 0)
            queryBlock = "AND " + queryBlock;

        tagQueryBlock = queryBlock;
    }

    public String build(Map<BigInteger, List<BigInteger>> countToSpectrumIdsMap) {

        return String.format("SELECT Common, Id FROM (\n%s\n) AS CombinedTable WHERE Tags > 0 GROUP BY Common, Id",
                countToSpectrumIdsMap.entrySet()
                        .stream()
                        .map(e -> buildQueryBlock(e.getKey(), e.getValue()))
                        .collect(Collectors.joining("\nUNION ALL\n")));
    }

    private String buildQueryBlock(BigInteger count, List<BigInteger> spectrumIds) {

        String query = String.format("SELECT %d AS Common, Spectrum.Id, ", count);
        query += String.format(
                "(SELECT COUNT(*) FROM SubmissionTag WHERE SubmissionTag.SubmissionId = File.SubmissionId %s) AS Tags",
                tagQueryBlock);
        query += "\n";

        query += "\tFROM Spectrum LEFT JOIN SpectrumCluster ON SpectrumCluster.ConsensusSpectrumId = Spectrum.Id\n";
        query += "\tLEFT JOIN Spectrum AS ClusteredSpectrum ON ClusteredSpectrum.ClusterId = SpectrumCluster.Id\n";
        query += "\tLEFT JOIN File ON File.Id = ClusteredSpectrum.FileId\n";
        query += String.format("\tWHERE Spectrum.Id IN (%s)",
                spectrumIds.stream()
                        .map(BigInteger::toString)
                        .collect(Collectors.joining(",")));

        return query;
    }
}
