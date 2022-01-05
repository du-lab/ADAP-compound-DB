package org.dulab.adapcompounddb.site.services.io;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.dulab.adapcompounddb.site.services.io.ExportUtils.*;

public interface ExportSearchResultsService {


    void exportAll(OutputStream outputStream, List<SearchResultDTO> searchResults) throws IOException;

    default void export(OutputStream outputStream, List<SearchResultDTO> searchResults) throws IOException {
        exportAll(outputStream, selectTopResults(searchResults));
//        exportAll(outputStream, searchResults);
    }

    static List<SearchResultDTO> selectTopResults(List<SearchResultDTO> searchResults) {

        Object[] queryIds = searchResults.stream()
                .map(r -> getId(r.getQueryExternalId(), r.getQuerySpectrumName(), r.getSpectrumId(), r.getQueryFileIndex(), r.getQuerySpectrumIndex()))
                .distinct()
                .toArray();

        List<SearchResultDTO> allTopResults = new ArrayList<>();

        for (Object queryId : queryIds) {

            List<SearchResultDTO> topResults = new ArrayList<>();

            List<SearchResultDTO> selectedSearchResults = searchResults.stream()
                    .filter(r -> queryId.equals(getId(r.getQueryExternalId(), r.getQuerySpectrumName(), r.getSpectrumId(), r.getQueryFileIndex(), r.getQuerySpectrumIndex())))
                    .collect(Collectors.toList());

            if (selectedSearchResults.isEmpty()) continue;

            // Collect all different matches
            Map<Object, List<SearchResultDTO>> matchIdToSearchResultsMap = new HashMap<>();
            for (SearchResultDTO searchResult : selectedSearchResults) {
                Object matchSpectrumId = getId(searchResult.getExternalId(), searchResult.getName(), searchResult.getSpectrumId(), null, null);
                if (matchSpectrumId == null) {
                    topResults.add(searchResult);
                    continue;
                }

                matchIdToSearchResultsMap.computeIfAbsent(matchSpectrumId, k -> new ArrayList<>())
                        .add(searchResult);
            }

            // Merge search results with the identical matches
            for (List<SearchResultDTO> identicalSearchResults : matchIdToSearchResultsMap.values()) {

                if (identicalSearchResults == null || identicalSearchResults.isEmpty()) continue;

                identicalSearchResults.sort(Comparator
                        .comparing(SearchResultDTO::getOntologyPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SearchResultDTO::getScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(SearchResultDTO::getMassErrorPPM, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SearchResultDTO::getMassError, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SearchResultDTO::getRetTimeError, Comparator.nullsLast(Comparator.naturalOrder())));

                SearchResultDTO topResult = identicalSearchResults.get(0).clone();

                topResult.setQueryPrecursorMzs(identicalSearchResults.stream()
                        .map(SearchResultDTO::getQueryPrecursorMz).filter(Objects::nonNull)
                        .distinct().mapToDouble(Double::doubleValue).toArray());

                topResult.setQueryPrecursorTypes(identicalSearchResults.stream()
                        .map(SearchResultDTO::getQueryPrecursorType).filter(Objects::nonNull)
                        .distinct().toArray(String[]::new));

                topResult.setMarked(identicalSearchResults.stream()
                        .map(SearchResultDTO::isMarked)
                        .reduce(false, (previous, marked) -> previous || marked));

                topResults.add(topResult);
            }

            topResults.sort(Comparator.comparing(
                    SearchResultDTO::getMatchIndex, Comparator.nullsLast(Comparator.naturalOrder())));

            allTopResults.addAll(topResults);
        }

        return allTopResults;
    }

    /**
     * Returns QuerySpectrumId if it exists. Otherwise, returns a unique integer corresponding to the QueryFileIndex
     * and QuerySpectrumIndex
     *
     * @param externalId External ID of a spectrum,
     * @param spectrumId Spectrum ID of a spectrum,
     * @param fileIndex index of the file containing that spectrum
     * @param spectrumIndex index of the spectrum in that file
     * @return query ID
     */
    static Object getId(String externalId, String spectrumName, Long spectrumId, Integer fileIndex, Integer spectrumIndex) {

        // Use ExternalId if available
        if (externalId != null && !externalId.isEmpty())
            return externalId;

        // Use Name if available
        if (spectrumName != null && !spectrumName.isEmpty())
            return spectrumName;

        // Use QuerySpectrumId if available
        if (spectrumId != null && spectrumId > 0)
            return spectrumId;

        // Use the Cantor pairing function
        if (fileIndex != null && spectrumIndex != null)
            return spectrumIndex + (fileIndex + spectrumIndex) * (fileIndex + spectrumIndex + 1) / 2;

        return null;
    }


    /**
     * Standard fields that we export in every CSV files
     */
    enum ExportField {

        //        POSITION("Position", r -> Integer.toString(r.getPosition())),
        QUERY_FILE_ID("File Index", null, r -> r.getQueryFileIndex() != null ? Integer.toString(r.getQueryFileIndex() + 1) : null),
        QUERY_SPECTRUM_ID("Numerical Signal ID assigned by ADAP-KDB", null, r -> r.getQuerySpectrumIndex() != null ? Integer.toString(r.getQuerySpectrumIndex() + 1) : null),
        MATCH_INDEX("Match #", null, r -> r.getMatchIndex() != null ? Integer.toString(r.getMatchIndex() + 1) : null),
//        QUERY_EXTERNAL_ID("Signal ID", ExportCategory.MEASURED, SearchResultDTO::getQueryExternalId),
        QUERY_NAME("Query Signal Name", ExportCategory.MEASURED, SearchResultDTO::getQuerySpectrumShortName),
        QUERY_RET_TIME("Retention time (min)", ExportCategory.MEASURED, r -> formatDouble(r.getQueryRetTime(), 3)),
        QUERY_PRECURSOR_MZ("Precursor m/z", ExportCategory.MEASURED, r -> formatDoubleArray(r.getQueryPrecursorMzs(), 4)),
        QUERY_PRECURSOR_TYPE("Adduct", ExportCategory.MEASURED, r -> formatStringArray(r.getQueryPrecursorTypes())),
        QUERY_MASS("Neutral Mass (Da)", ExportCategory.MEASURED, r -> formatDouble(r.getQueryMass(), 4)),
        FRAGMENTATION_SPECTRUM("With Fragmentation Spectrum or not", ExportCategory.MEASURED, r -> formatBoolean(r.isQueryWithPeaks())),
        RET_TIME_ERROR("Retention Time Error (min)", ExportCategory.DIFFERENCE, r -> formatDouble(r.getRetTimeError(), 3)),
        MASS_ERROR_PPM("Precursor Mass Error (ppm)", ExportCategory.DIFFERENCE, r -> formatDouble(r.getMassErrorPPM(), 4)),
        PRECURSOR_TYPE("Matching Adduct", ExportCategory.DIFFERENCE, SearchResultDTO::getPrecursorType),
        ISOTOPIC_SIMILARITY("Isotopic Similarity", ExportCategory.DIFFERENCE,
                r -> r.getIsotopicSimilarity() != null ? String.format("%.0f", 1000 * r.getIsotopicSimilarity()) : null),
        SCORE("Fragmentation Score by matching with Experimental spectra", ExportCategory.DIFFERENCE,
                r -> r.getScore() != null ? Long.toString(r.getNISTScore()) : null),
        THEORETICAL_SCORE("Fragmentation Score by matching with theoretical spectra", ExportCategory.DIFFERENCE, r -> null),
        //        MASS_ERROR("Mass Error (Da)", r -> r.getMassError() != null ? Double.toString(r.getMassError()) : null),
        ONTOLOGY_LEVEL("Ontology Level", ExportCategory.DIFFERENCE, SearchResultDTO::getOntologyLevel),
        MARKED("Is this the best match", ExportCategory.DIFFERENCE, r -> formatBoolean(r.isMarked())),
        MATCH_EXTERNAL_ID("Compound ID", ExportCategory.MATCHED, SearchResultDTO::getExternalId),
        MATCH_NAME("Compound Name", ExportCategory.MATCHED, SearchResultDTO::getName),
        FORMULA("Chemical Formula", ExportCategory.MATCHED, SearchResultDTO::getFormula),
        MASS("Library Monoisotopic Mass (Da)", ExportCategory.MATCHED, r -> formatDouble(r.getMass(), 4)),
        RET_TIME("Library Retention Time (min)", ExportCategory.MATCHED, r -> formatDouble(r.getRetTime(), 3)),
        CAS_ID("CASNO", ExportCategory.MATCHED, SearchResultDTO::getCasId),
        HMDB_ID("HMDB ID", ExportCategory.MATCHED, SearchResultDTO::getHmdbId),
        PUBCHEM_ID("PubChem ID", ExportCategory.MATCHED, SearchResultDTO::getPubChemId),
        SUBMISSION_NAME("Library Category", ExportCategory.MATCHED, SearchResultDTO::getSubmissionName),
        NOTES("NOTES", ExportCategory.MISC, r -> null);


        final String name;
        final ExportCategory exportCategory;
        final Function<SearchResultDTO, String> getter;

        ExportField(String name, ExportCategory exportCategory, Function<SearchResultDTO, String> getter) {
            this.name = name;
            this.exportCategory = exportCategory;
            this.getter = getter;
        }

        static ExportField[] values(ExportCategory exportCategory) {
            return Arrays.stream(ExportField.values())
                    .filter(field -> field.exportCategory == exportCategory)
                    .toArray(ExportField[]::new);
        }
    }

    enum ExportCategory {

        MEASURED("Signal information measured by instrument or calculated from the measured data", IndexedColors.LIGHT_TURQUOISE),
        DIFFERENCE("Library matching values", IndexedColors.LIGHT_YELLOW),
        MATCHED("Compound information", IndexedColors.LIGHT_GREEN),
        MISC("Misc", IndexedColors.LIGHT_ORANGE);

        final String label;
        final IndexedColors color;

        ExportCategory(String label, IndexedColors color) {
            this.label = label;
            this.color = color;
        }

        static ExportCategory[] valuesWithNull() {
            // Create array of categories with the first null value
            int numCategories = ExportCategory.values().length;
            ExportCategory[] exportCategories = new ExportCategory[1 + numCategories];
            System.arraycopy(ExportCategory.values(), 0, exportCategories, 1, numCategories);
            return exportCategories;
        }
    }
}
