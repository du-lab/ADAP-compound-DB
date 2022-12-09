package org.dulab.adapcompounddb.site.services.io;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.dto.SpectrumProperty;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReaderService implements FileReaderService {

    @Override
    public List<Spectrum> read(InputStream inputStream, MetaDataMapping mapping, String filename,
                               ChromatographyType chromatographyType) throws IOException {

        List<Spectrum> spectra = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] header = csvReader.readNext();
            if (header == null)
                return new ArrayList<>(0);

//            Map<String, Integer> headerToIndexMap = createHeaderToIndexMap(header);

            String[] row;
            while((row = csvReader.readNext()) != null) {

//                // Set spectrum name
//                Integer nameIndex = headerToIndexMap.get(NAME_HEADER);
//                if (nameIndex == null || nameIndex < 0 || nameIndex >= row.length)
//                    throw new IOException("Column Name is missing");
//                String name = row[nameIndex].trim();

//                // Set molecular weight
//                Double molecularWeight = null;
//                Integer weightIndex = headerToIndexMap.get(WEIGHT_HEADER);
//                if (weightIndex != null && weightIndex >= 0 && weightIndex < row.length) {
//                    molecularWeight = parse(row[weightIndex]);
//                }

                // Set spectrum properties
                List<SpectrumProperty> properties = new ArrayList<>(header.length);
                for (int i = 0; i < header.length; ++i) {
                    SpectrumProperty property = new SpectrumProperty();
                    property.setName(header[i].trim());
                    property.setValue(row[i].trim());
                    properties.add(property);
                }

                Spectrum spectrum = new Spectrum();
//                spectrum.setChromatographyType(type);
//                spectrum.setName(name);
//                spectrum.setMolecularWeight(molecularWeight);
                spectrum.setProperties(properties, mapping);

                spectra.add(spectrum);
            }

        } catch (CsvValidationException e) {
            throw new IOException("Cannot read CSV file: " + e.getMessage(), e);
        }

        return spectra;
    }

    @Override
    public MetaDataMapping validateMetaDataMapping(MetaDataMapping mapping) {
        if (mapping == null)
            mapping = new MetaDataMapping();
        return mapping;
    }

//    private Map<String, Integer> createHeaderToIndexMap(String[] header) throws IOException {
//        Map<String, Integer> headerToIndexMap = new HashMap<>();
//        for (int i = 0; i < header.length; ++i) {
//            String value = header[i].toLowerCase().trim();
//            if (value.equals(NAME_HEADER) || value.equals(WEIGHT_HEADER))
//                headerToIndexMap.put(value, i);
//        }
//
//        if (!headerToIndexMap.containsKey(NAME_HEADER))
//            throw new IOException("Column Name is missing");
//
//        return headerToIndexMap;
//    }

//    private Double parse(String string) {
//        Pattern pattern = Pattern.compile("([0-9]+.?[0-9]+)");
//        Matcher matcher = pattern.matcher(string);
//        if (matcher.matches())
//            return Double.parseDouble(matcher.group(1));
//        return null;
//    }
}
