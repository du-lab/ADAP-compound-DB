package org.dulab.adapcompounddb.models;

import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.dto.SpectrumProperty;
import org.dulab.adapcompounddb.models.entities.Synonym;
import org.dulab.adapcompounddb.models.enums.IdentifierType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static org.dulab.adapcompounddb.site.services.utils.MappingUtils.parseDouble;

public class MetaDataMapping {

    public enum Field {
        NAME, SYNONYM, EXTERNAL_ID, CAS_ID, HMDB_ID, KEGG_ID, PUBCHEM_ID, PRECURSOR_MZ, PRECURSOR_TYPE, RETENTION_TIME,
        RETENTION_INDEX, MASS, FORMULA, SMILES, INCHI_KEY, INCHI, ISOTOPIC_DISTRIBUTION
    }

    private static final Map<Field, BiConsumer<Spectrum, String>> fieldToFunctionMap = new HashMap<>();

    static {
        fieldToFunctionMap.put(Field.NAME, Spectrum::setName);
        fieldToFunctionMap.put(Field.SYNONYM, (spectrum, value) -> {
            List<Synonym> synonyms = Objects.requireNonNullElseGet(spectrum.getSynonyms(), ArrayList::new);
            Synonym synonym = new Synonym();
            synonym.setName(value);
            synonym.setSpectrum(spectrum);
            synonyms.add(synonym);
            spectrum.setSynonyms(synonyms);
        });
        fieldToFunctionMap.put(Field.EXTERNAL_ID, Spectrum::setExternalId);
        fieldToFunctionMap.put(Field.CAS_ID, (s, v) -> s.addIdentifier(IdentifierType.CAS, v));
        fieldToFunctionMap.put(Field.HMDB_ID, (s, v) -> s.addIdentifier(IdentifierType.HMDB, v));
        fieldToFunctionMap.put(Field.KEGG_ID, (s, v) -> s.addIdentifier(IdentifierType.KEGG, v));
        fieldToFunctionMap.put(Field.PUBCHEM_ID, (s, v) -> s.addIdentifier(IdentifierType.PUBCHEM, v));
        fieldToFunctionMap.put(Field.PRECURSOR_MZ, (s, v) -> s.setPrecursor(parseDouble(v)));
        fieldToFunctionMap.put(Field.PRECURSOR_TYPE, Spectrum::setPrecursorType);
        fieldToFunctionMap.put(Field.RETENTION_TIME, (s, v) -> s.setRetentionTime(parseDouble(v)));
        fieldToFunctionMap.put(Field.RETENTION_INDEX, (spectrum, value) -> {
            Double retentionIndex = parseDouble(value);
            spectrum.setRetentionIndex((retentionIndex != null && retentionIndex > 0) ? retentionIndex : null);
        });
        fieldToFunctionMap.put(Field.MASS, (s, v) -> s.setMass(parseDouble(v)));
        fieldToFunctionMap.put(Field.FORMULA, Spectrum::setFormula);
        fieldToFunctionMap.put(Field.SMILES, Spectrum::setCanonicalSmiles);
        fieldToFunctionMap.put(Field.INCHI_KEY, Spectrum::setInChiKey);
        fieldToFunctionMap.put(Field.INCHI, Spectrum::setInChi);
        fieldToFunctionMap.put(Field.ISOTOPIC_DISTRIBUTION, (spectrum, value) ->
                spectrum.setIsotopes(Arrays.stream(value.split("-"))
                        .mapToDouble(Double::parseDouble)
                        .toArray()));
    }

    private final Map<Field, String> fieldToNameMap = new HashMap<>();


    public String getFieldName(Field field) {
        return fieldToNameMap.get(field);
    }

    public void setFieldName(Field field, String fieldName) {
        fieldToNameMap.put(field, (fieldName != null && !fieldName.isEmpty()) ? fieldName.trim().toLowerCase() : null);
    }


    public void map(SpectrumProperty property, Spectrum spectrum) {
        String propertyName = property.getName().trim().toLowerCase();
        String propertyValue = property.getValue();

        for (Field field : Field.values()) {
            if (propertyName.equals(fieldToNameMap.get(field)))
                fieldToFunctionMap.get(field).accept(spectrum, propertyValue);
        }
    }

    public boolean check(String fieldName) {
        fieldName = fieldName.trim().toLowerCase();
        for (Field field : Field.values())
            if (fieldName.equals(fieldToNameMap.get(field)))
                return true;
        return false;
    }

    public List<String> getNonEmptyFields() {
        return fieldToNameMap.entrySet().stream()
                .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
