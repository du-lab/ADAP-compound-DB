package org.dulab.adapcompounddb.site.services.io;

import org.apache.logging.log4j.util.PropertySource;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ExportService {

    String CHECK_CHARACTER = String.valueOf('\u2713');


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

//            selectedSearchResults.sort(Comparator
//                    .comparing(SearchResultDTO::getOntologyPriority, Comparator.nullsLast(Comparator.naturalOrder()))
//                    .thenComparing(SearchResultDTO::getScore, Comparator.nullsLast(Comparator.reverseOrder()))
//                    .thenComparing(SearchResultDTO::getMassErrorPPM, Comparator.nullsLast(Comparator.naturalOrder()))
//                    .thenComparing(SearchResultDTO::getMassError, Comparator.nullsLast(Comparator.naturalOrder()))
//                    .thenComparing(SearchResultDTO::getRetTimeError, Comparator.nullsLast(Comparator.naturalOrder())));
//
//            SearchResultDTO topResult = selectedSearchResults.get(0).clone();
//            topResult.setQueryPrecursorMzs(selectedSearchResults.stream()
//                    .map(SearchResultDTO::getQueryPrecursorMz).filter(Objects::nonNull)
//                    .distinct().mapToDouble(Double::doubleValue).toArray());
//            topResult.setQueryPrecursorTypes(selectedSearchResults.stream()
//                    .map(SearchResultDTO::getQueryPrecursorType).filter(Objects::nonNull)
//                    .distinct().toArray(String[]::new));
//
//            topResults.add(topResult);
        }

//        IntStream.range(0, topResults.size())
//                .forEach(i -> topResults.get(i).setPosition(i));

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
        QUERY_FILE_ID("File #", null, r -> r.getQueryFileIndex() != null ? Integer.toString(r.getQueryFileIndex() + 1) : null),
        QUERY_SPECTRUM_ID("Feature #", null, r -> r.getQuerySpectrumIndex() != null ? Integer.toString(r.getQuerySpectrumIndex() + 1) : null),
        MATCH_INDEX("Match #", null, r -> r.getMatchIndex() != null ? Integer.toString(r.getMatchIndex() + 1) : null),
//        QUERY_EXTERNAL_ID("Signal ID", ExportCategory.MEASURED, SearchResultDTO::getQueryExternalId),
        QUERY_NAME("Signal", ExportCategory.MEASURED, SearchResultDTO::getQuerySpectrumShortName),
        QUERY_RET_TIME("Ret time (min)", ExportCategory.MEASURED, r -> formatDouble(r.getQueryRetTime(), 3)),
        QUERY_PRECURSOR_MZ("Precursor m/z", ExportCategory.MEASURED, r -> formatDoubleArray(r.getQueryPrecursorMzs(), 4)),
        QUERY_PRECURSOR_TYPE("Adduct", ExportCategory.MEASURED, r -> formatStringArray(r.getQueryPrecursorTypes())),
        QUERY_MASS("Mass (Da)", ExportCategory.MEASURED, r -> formatDouble(r.getQueryMass(), 4)),
        FRAGMENTATION_SPECTRUM("Fragmentation spectrum", ExportCategory.MEASURED, r -> formatBoolean(r.isQueryWithPeaks())),
        MATCH_NAME("Compound Name", ExportCategory.MATCHED, SearchResultDTO::getName),
        MATCH_EXTERNAL_ID("Compound ID", ExportCategory.MATCHED, SearchResultDTO::getExternalId),
        SCORE("Fragmentation Score", ExportCategory.DIFFERENCE, r -> r.getScore() != null ? Double.toString(r.getNISTScore()) : null),
        //        MASS_ERROR("Mass Error (Da)", r -> r.getMassError() != null ? Double.toString(r.getMassError()) : null),
        MASS_ERROR_PPM("Mass Error (PPM)", ExportCategory.DIFFERENCE, r -> formatDouble(r.getMassErrorPPM(), 4)),
        RET_TIME_ERROR("Ret Time Error (min)", ExportCategory.DIFFERENCE, r -> formatDouble(r.getRetTimeError(), 3)),
        ONTOLOGY_LEVEL("Ontology Level", ExportCategory.DIFFERENCE, SearchResultDTO::getOntologyLevel),
        MARKED("Best Match", ExportCategory.DIFFERENCE, r -> formatBoolean(r.isMarked())),
        FORMULA("Formula", ExportCategory.MATCHED, SearchResultDTO::getFormula),
        MASS("Mass (Da)", ExportCategory.MATCHED, r -> formatDouble(r.getMass(), 4)),
        RET_TIME("Ret Time (min)", ExportCategory.MATCHED, r -> formatDouble(r.getRetTime(), 3)),
        PRECURSOR_TYPE("Adduct", ExportCategory.MATCHED, SearchResultDTO::getPrecursorType),
        SUBMISSION_NAME("Library", ExportCategory.MATCHED, SearchResultDTO::getSubmissionName);


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

        private static String formatBoolean(boolean x) {
            return x ? CHECK_CHARACTER : null;
        }

        private static String formatDouble(Double x, int digits) {
            if (x == null)
                return null;
            String format = String.format("%%.%df", digits);
            return String.format(format, x);
        }

        private static String formatDoubleArray(double[] xs, int digits) {
            if (xs == null)
                return null;
            return Arrays.stream(xs).mapToObj(x -> formatDouble(x, digits))
                    .collect(Collectors.joining(", "));
        }

        private static String formatStringArray(String[] strings) {
            if (strings == null)
                return null;
            return String.join(", ", strings);
        }
    }

    enum ExportCategory {

        MEASURED("Signal information measured by instrument or calculated from the measured data", IndexedColors.LIGHT_TURQUOISE),
        DIFFERENCE("Library matching values", IndexedColors.LIGHT_YELLOW),
        MATCHED("Compound information", IndexedColors.LIGHT_GREEN);

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
