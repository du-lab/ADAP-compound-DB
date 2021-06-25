package org.dulab.adapcompounddb.site.services.io;

import com.opencsv.CSVWriter;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CsvExportSearchResultsService implements ExportSearchResultsService {

    private final SpectrumService spectrumService;

    @Autowired
    public CsvExportSearchResultsService(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    @Override
    public void exportAll(OutputStream outputStream, List<SearchResultDTO> searchResults) throws IOException {

        long[] matchIds = searchResults.stream()
                .mapToLong(SearchResultDTO::getSpectrumId)
                .toArray();

        // Retrieve spectrum properties
        SortedSet<String> propertyNames = new TreeSet<>();
        Map<Long, List<SpectrumProperty>> spectrumIdToPropertiesMap = new HashMap<>();
        if (matchIds != null && matchIds.length > 0) {
            List<SpectrumProperty> properties = spectrumService.findSpectrumPropertiesBySpectrumId(matchIds);
            for (SpectrumProperty property : properties) {
                propertyNames.add(property.getName());
                spectrumIdToPropertiesMap
                        .computeIfAbsent(property.getSpectrum().getId(), k -> new ArrayList<>())
                        .add(property);
            }
        }

        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream));
        csvWriter.writeNext(createHeader(propertyNames));
        for (SearchResultDTO searchResult : searchResults) {
            csvWriter.writeNext(createRow(propertyNames, spectrumIdToPropertiesMap, searchResult));
        }
        csvWriter.close();

    }

    private String[] createHeader(SortedSet<String> propertyNames) {
        List<String> fields = Arrays.stream(ExportField.values())
                .map(field -> field.name)
                .collect(Collectors.toList());
//        fields.addAll(propertyNames);
        return fields.toArray(new String[0]);
    }

    private String[] createRow(SortedSet<String> propertyNames,
                               Map<Long, List<SpectrumProperty>> spectrumIdToPropertiesMap,
                               SearchResultDTO searchResult) {

        List<String> values = Arrays.stream(ExportField.values())
                .map(field -> field.getter.apply(searchResult))
                .collect(Collectors.toList());

//        List<SpectrumProperty> properties = spectrumIdToPropertiesMap.get(searchResult.getSpectrumId());
//        for (String propertyName : propertyNames) {
//            String value = null;
//            if (properties != null) {
//                for (SpectrumProperty property : properties) {
//                    if (property.getName().equals(propertyName)) {
//                        value = property.getValue();
//                        break;
//                    }
//                }
//            }
//            values.add(value);
//        }

        return values.toArray(new String[0]);
    }



}
