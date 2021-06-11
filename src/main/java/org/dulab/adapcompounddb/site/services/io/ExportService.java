package org.dulab.adapcompounddb.site.services.io;

import org.apache.poi.ss.usermodel.IndexedColors;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ExportService {

    void exportAll(OutputStream outputStream, List<SearchResultDTO> searchResults) throws IOException;

    default void export(OutputStream outputStream, List<SearchResultDTO> searchResults) throws IOException {
//        exportAll(outputStream, selectTopResults(searchResults));
        exportAll(outputStream, searchResults);
    }

    static List<SearchResultDTO> selectTopResults(List<SearchResultDTO> searchResults) {

        Object[] queryIds = searchResults.stream()
                .map(ExportService::getQueryId)
                .distinct()
                .toArray();

        List<SearchResultDTO> topResults = new ArrayList<>();
        for (Object queryId : queryIds) {

            List<SearchResultDTO> selectedSearchResults = searchResults.stream()
                    .filter(r -> queryId.equals(getQueryId(r)))
                    .collect(Collectors.toList());

            if (selectedSearchResults.isEmpty()) continue;

            selectedSearchResults.sort(Comparator
                    .comparing(SearchResultDTO::getOntologyPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(SearchResultDTO::getScore, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(SearchResultDTO::getMassErrorPPM, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(SearchResultDTO::getMassError, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(SearchResultDTO::getRetTimeError, Comparator.nullsLast(Comparator.naturalOrder())));

            SearchResultDTO topResult = selectedSearchResults.get(0).clone();
            topResult.setQueryPrecursorMzs(selectedSearchResults.stream()
                    .map(SearchResultDTO::getQueryPrecursorMz).filter(Objects::nonNull)
                    .distinct().mapToDouble(Double::doubleValue).toArray());
            topResult.setQueryPrecursorTypes(selectedSearchResults.stream()
                    .map(SearchResultDTO::getQueryPrecursorType).filter(Objects::nonNull)
                    .distinct().toArray(String[]::new));

            topResults.add(topResult);
        }

//        IntStream.range(0, topResults.size())
//                .forEach(i -> topResults.get(i).setPosition(i));

        return topResults;
    }

    /**
     * Returns QuerySpectrumId if it exists. Otherwise, returns a unique integer corresponding to the QueryFileIndex
     * and QuerySpectrumIndex
     *
     * @param searchResult search result
     * @return query ID
     */
    static Object getQueryId(SearchResultDTO searchResult) {
        // Use ExternalId if available
        if (searchResult.getQueryExternalId() != null)
            return searchResult.getQueryExternalId();

        // Use QuerySpectrumId if available
        if (searchResult.getQuerySpectrumId() != null && searchResult.getQuerySpectrumId() > 0)
            return searchResult.getQuerySpectrumId();

        // Otherwise, use the Cantor pairing function
        return searchResult.getQuerySpectrumIndex()
                + (searchResult.getQueryFileIndex() + searchResult.getQuerySpectrumIndex())
                * (searchResult.getQueryFileIndex() + searchResult.getQuerySpectrumIndex() + 1) / 2;
    }


    /**
     * Standard fields that we export in every CSV files
     */
    enum ExportField {

        //        POSITION("Position", r -> Integer.toString(r.getPosition())),
        QUERY_FILE_ID("File", null, r -> r.getQueryFileIndex() != null ? Integer.toString(r.getQueryFileIndex() + 1) : null),
        QUERY_SPECTRUM_ID("Feature", null, r -> r.getQuerySpectrumIndex() != null ? Integer.toString(r.getQuerySpectrumIndex() + 1) : null),
        QUERY_EXTERNAL_ID("Signal ID", ExportCategory.MEASURED, SearchResultDTO::getQueryExternalId),
        QUERY_NAME("Signal Name", ExportCategory.MEASURED, SearchResultDTO::getQuerySpectrumName),
        QUERY_PRECURSOR_MZ("Precursor m/z", ExportCategory.MEASURED, r -> formatDoubleArray(r.getQueryPrecursorMzs(), 4)),
        QUERY_PRECURSOR_TYPE("Adduct", ExportCategory.MEASURED, r -> formatStringArray(r.getQueryPrecursorTypes())),
        QUERY_MASS("Mass (Da)", ExportCategory.MEASURED, r -> formatDouble(r.getQueryMass(), 4)),
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
            return x ? "Best match" : null;
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

        MEASURED("Measured values", IndexedColors.LIGHT_TURQUOISE),
        DIFFERENCE("", IndexedColors.LIGHT_YELLOW),
        MATCHED("Matched values", IndexedColors.LIGHT_GREEN);

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
