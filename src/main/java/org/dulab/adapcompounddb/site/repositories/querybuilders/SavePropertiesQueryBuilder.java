package org.dulab.adapcompounddb.site.repositories.querybuilders;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;

import java.util.ArrayList;
import java.util.List;

public class SavePropertiesQueryBuilder {

    private static final String PROPERTY_INSERT_SQL_STRING =
            "INSERT INTO `SpectrumProperty`(`SpectrumId`, `Name`, `Value`) VALUES ";

    private final List<Spectrum> spectrumList;
    private final List<Long> spectrumIds;
    private final int maxPropertiesInQuery;


    public SavePropertiesQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds) {
        this(spectrumList, spectrumIds, 1000000);
    }

    public SavePropertiesQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds, int maxPropertiesInQuery) {
        this.spectrumList = spectrumList;
        this.spectrumIds = spectrumIds;
        this.maxPropertiesInQuery = maxPropertiesInQuery;
    }

    public String[] build() {

        List<List<String>> propertyTriplesList = new ArrayList<>();
        List<String> propertyTriples = new ArrayList<>();
        int propertyCount = 0;
        for (int i = 0; i < spectrumList.size(); i++) {
            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();

            if (properties == null)
                continue;

            for (SpectrumProperty property : properties) {
                propertyTriples.add(String.format("(%d, \"%s\", \"%s\")",
                        spectrumIds.get(i),
                        property.getName().replace("\"", "\"\""),
                        property.getValue().replace("\"", "\"\"")));
            }

            propertyCount += properties.size();

            if (propertyCount > maxPropertiesInQuery) {
                propertyTriplesList.add(propertyTriples);
                propertyTriples = new ArrayList<>();
                propertyCount = 0;
            }
        }

        if (propertyCount > 0)
            propertyTriplesList.add(propertyTriples);

        return propertyTriplesList.stream()
                .map(triples -> String.join(",", triples))
                .filter(s -> !s.isEmpty())
                .map(s -> PROPERTY_INSERT_SQL_STRING + s)
                .toArray(String[]::new);
    }
}
