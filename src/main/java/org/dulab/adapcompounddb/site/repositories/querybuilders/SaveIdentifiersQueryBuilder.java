package org.dulab.adapcompounddb.site.repositories.querybuilders;

import org.dulab.adapcompounddb.models.entities.Identifier;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Synonym;
import org.dulab.adapcompounddb.models.enums.IdentifierType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveIdentifiersQueryBuilder {

    private static final String IDENTIFIER_INSERT_SQL_STRING =
            "INSERT INTO `Identifier`(`SpectrumId`, `Type`, `Value`) VALUES ";

    private final List<Spectrum> spectrumList;
    private final List<Long> spectrumIds;
    private final int maxIdentifiersInQuery;


    public SaveIdentifiersQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds) {
        this(spectrumList, spectrumIds, 1000000);
    }

    public SaveIdentifiersQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds, int maxIdentifiersInQuery) {
        this.spectrumList = spectrumList;
        this.spectrumIds = spectrumIds;
        this.maxIdentifiersInQuery = maxIdentifiersInQuery;
    }

    public String[] build() {

        List<List<String>> identifierTriplesList = new ArrayList<>();
        List<String> identifierTriples = new ArrayList<>();
        int identifierCount = 0;
        for (int i = 0; i < spectrumList.size(); i++) {
            Map<IdentifierType, String> identifiers = spectrumList.get(i).getIdentifiersAsMap();
            if (identifiers == null)
                continue;

            for (Map.Entry<IdentifierType, String> identifier : identifiers.entrySet()) {
                identifierTriples.add(String.format("(%d, \"%s\", \"%s\")",
                        spectrumIds.get(i),
                        identifier.getKey().name(),
                        identifier.getValue().replace("\"", "\"\"")));
            }

            identifierCount += identifiers.size();

            if (identifierCount > maxIdentifiersInQuery) {
                identifierTriplesList.add(identifierTriples);
                identifierTriples = new ArrayList<>();
                identifierCount = 0;
            }
        }

        if (identifierCount > 0)
            identifierTriplesList.add(identifierTriples);

        return identifierTriplesList.stream()
                .map(triples -> String.join(",", triples))
                .filter(s -> !s.isEmpty())
                .map(s -> IDENTIFIER_INSERT_SQL_STRING + s)
                .toArray(String[]::new);
    }
}
