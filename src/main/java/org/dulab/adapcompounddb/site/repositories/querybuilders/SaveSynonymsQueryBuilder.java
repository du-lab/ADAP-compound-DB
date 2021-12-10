package org.dulab.adapcompounddb.site.repositories.querybuilders;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Synonym;

import java.util.ArrayList;
import java.util.List;

public class SaveSynonymsQueryBuilder {

    private static final String SYNONYM_INSERT_SQL_STRING =
            "INSERT INTO `Synonym`(`SpectrumId`, `Name`) VALUES ";

    private final List<Spectrum> spectrumList;
    private final List<Long> spectrumIds;
    private final int maxSynonymsInQuery;


    public SaveSynonymsQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds) {
        this(spectrumList, spectrumIds, 1000000);
    }

    public SaveSynonymsQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds, int maxSynonymsInQuery) {
        this.spectrumList = spectrumList;
        this.spectrumIds = spectrumIds;
        this.maxSynonymsInQuery = maxSynonymsInQuery;
    }

    public String[] build() {

        List<List<String>> synonymTriplesList = new ArrayList<>();
        List<String> synonymTriples = new ArrayList<>();
        int synonymCount = 0;
        for (int i = 0; i < spectrumList.size(); i++) {
            List<Synonym> synonyms = spectrumList.get(i).getSynonyms();
            if (synonyms == null)
                continue;

            for (Synonym synonym : synonyms) {
                synonymTriples.add(String.format("(%d, \"%s\")",
                        spectrumIds.get(i),
                        synonym.getName().replace("\"", "\"\"")));
            }

            synonymCount += synonyms.size();

            if (synonymCount > maxSynonymsInQuery) {
                synonymTriplesList.add(synonymTriples);
                synonymTriples = new ArrayList<>();
                synonymCount = 0;
            }
        }

        if (synonymCount > 0)
            synonymTriplesList.add(synonymTriples);

        return synonymTriplesList.stream()
                .map(triples -> String.join(",", triples))
                .filter(s -> !s.isEmpty())
                .map(s -> SYNONYM_INSERT_SQL_STRING + s)
                .toArray(String[]::new);
    }
}
