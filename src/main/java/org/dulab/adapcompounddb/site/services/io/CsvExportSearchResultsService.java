package org.dulab.adapcompounddb.site.services.io;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.dto.SpectrumProperty;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CsvExportSearchResultsService implements ExportSearchResultsService {

    private final SpectrumService spectrumService;

    @Value("${info.version}")
    private String applicationVersion;

    @Autowired
    public CsvExportSearchResultsService(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    @Override
    public void exportAll(OutputStream outputStream, List<SearchResultDTO> searchResults,
        Collection<String> libraries, String searchParametersAsString) throws IOException {

//        long[] matchIds = searchResults.stream()
//                .mapToLong(SearchResultDTO::getSpectrumId)
//                .toArray();

//        // Retrieve spectrum properties
//        SortedSet<String> propertyNames = new TreeSet<>();
//        Map<Long, List<SpectrumProperty>> spectrumIdToPropertiesMap = new HashMap<>();
//        if (matchIds != null && matchIds.length > 0) {
//            List<SpectrumProperty> properties = spectrumService.findSpectrumPropertiesBySpectrumId(matchIds);
//            for (SpectrumProperty property : properties) {
//                propertyNames.add(property.getName());
//                spectrumIdToPropertiesMap
//                        .computeIfAbsent(property.getSpectrum().getId(), k -> new ArrayList<>())
//                        .add(property);
//            }
//        }

        try (ZipOutputStream zipOutput = new ZipOutputStream(outputStream);
            InputStream dictionaryInput = this.getClass().getResourceAsStream("export_dictionary.pdf")) {

            zipOutput.putNextEntry(new ZipEntry("export_dictionary.pdf"));
            byte[] bytes = new byte[1024];
            int length;
            while ((length = dictionaryInput.read(bytes)) >= 0) {
                zipOutput.write(bytes, 0, length);
            }

            zipOutput.putNextEntry(new ZipEntry("export_data.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(zipOutput));
            for (String[] row : createHeader(applicationVersion, libraries, searchParametersAsString)) {
                csvWriter.writeNext(row);
            }
            for (SearchResultDTO searchResult : searchResults) {
                csvWriter.writeNext(createRow(searchResult));
            }
            csvWriter.close();
        }
    }

    private String[][] createHeader(String applicationVersion, Collection<String> libraryNames, String searchParametersAsString) {
        //The first line: "Library matching results produced by ADAP-KDB v0.0.1"
        String[] firstRow = new String[]{"Library matching results produced by ADAP-KDB version " + applicationVersion + " on " + LocalDate.now()};

        //The second line: "Libraries used for matching: LibraryName1, LibraryName2"
        String[] secondRow = new String[]{"Libraries used for matching: " + String.join(", ", libraryNames)};
        String [] parametersRow = new String[]{"Parameters: " + searchParametersAsString};
        // Third line
        Set<String> writtenCategories = new HashSet<>();
        String[] thirdRow = Arrays.stream(ExportField.values())
                .map(field -> field.exportCategory)
                .map(c -> {
                    if (c == null || writtenCategories.contains(c.label)) {
                        return "";
                    } else {
                        writtenCategories.add(c.label);
                        return c.label;
                    }
                })
                .toArray(String[]::new);

        // Fourth line
        String[] fourthRow = Arrays.stream(ExportField.values())
                .map(field -> field.name)
                .toArray(String[]::new);

//        fields.addAll(propertyNames);

        return new String[][] {firstRow, parametersRow, secondRow, thirdRow, fourthRow};
    }

    private String[] createRow(SearchResultDTO searchResult) {

        return Arrays.stream(ExportField.values())
                .map(field -> field.getter.apply(searchResult))
                .toArray(String[]::new);
    }



}
