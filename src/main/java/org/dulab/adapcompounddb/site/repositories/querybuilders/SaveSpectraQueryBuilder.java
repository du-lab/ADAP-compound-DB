package org.dulab.adapcompounddb.site.repositories.querybuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Identifier;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepositoryImpl;
import org.dulab.adapcompounddb.site.services.utils.ByteArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveSpectraQueryBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveSpectraQueryBuilder.class);

    public static final String COMMA = ",";

    private static final SqlField[] spectrumFields = new SqlField[]{
            new SqlField("Name", "%s", s -> quote(s.getName())),
            new SqlField("ExternalId", "%s", s -> quote(s.getExternalId())),
            new SqlField("Precursor", "%f", Spectrum::getPrecursor),
            new SqlField("PrecursorType", "%s", s -> quote(s.getPrecursorType())),
            new SqlField("RetentionTime", "%f", Spectrum::getRetentionTime),
            new SqlField("RetentionIndex", "%f", Spectrum::getRetentionIndex),
            new SqlField("Significance", "%f", Spectrum::getSignificance),
            new SqlField("ClusterId", "%d", s -> s.getCluster() != null ? s.getCluster().getId() : null),
            new SqlField("Consensus", "%b", Spectrum::isConsensus),
            new SqlField("Reference", "%b", Spectrum::isReference),
            new SqlField("InHouseReference", "%b", Spectrum::isInHouseReference),
            new SqlField("IntegerMz", "%b", Spectrum::isIntegerMz),
            new SqlField("ChromatographyType", "%s", s -> quote(s.getChromatographyType().name())),
            new SqlField("FileId", "%d", s -> s.getFile().getId()),
            new SqlField("Mass", "%f", Spectrum::getMass),
            new SqlField("Formula", "%s", s -> quote(s.getFormula())),
            new SqlField("CanonicalSMILES", "%s", s -> quote(s.getCanonicalSmiles())),
            new SqlField("InChi", "%s", s -> quote(s.getInChi())),
            new SqlField("InChiKey", "%s", s -> quote(s.getInChiKey())),
            new SqlField("OmegaFactor", "%f", Spectrum::getOmegaFactor),
            new SqlField("TopMz1", "%f", Spectrum::getTopMz1),
            new SqlField("TopMz2", "%f", Spectrum::getTopMz2),
            new SqlField("TopMz3", "%f", Spectrum::getTopMz3),
            new SqlField("TopMz4", "%f", Spectrum::getTopMz4),
            new SqlField("TopMz5", "%f", Spectrum::getTopMz5),
            new SqlField("TopMz6", "%f", Spectrum::getTopMz6),
            new SqlField("TopMz7", "%f", Spectrum::getTopMz7),
            new SqlField("TopMz8", "%f", Spectrum::getTopMz8),
            new SqlField("TopMz9", "%f", Spectrum::getTopMz9),
            new SqlField("TopMz10", "%f", Spectrum::getTopMz10),
            new SqlField("TopMz11", "%f", Spectrum::getTopMz11),
            new SqlField("TopMz12", "%f", Spectrum::getTopMz12),
            new SqlField("TopMz13", "%f", Spectrum::getTopMz13),
            new SqlField("TopMz14", "%f", Spectrum::getTopMz14),
            new SqlField("TopMz15", "%f", Spectrum::getTopMz15),
            new SqlField("TopMz16", "%f", Spectrum::getTopMz16),
            new SqlField("PeakDataEncoded", "%s", s -> quote(s.getPeakDataEncoded())),
            new SqlField("IdentifiersJson", "%s", s -> quote(s.getIdentifiersJson()))
    };


    private final List<File> fileList;
    private List<Spectrum> spectrumList;


    public SaveSpectraQueryBuilder(List<File> fileList) {
        this.fileList = fileList;
    }

    public String build() {

        spectrumList = new ArrayList<>();

        StringBuilder insertSql = new StringBuilder(
                String.format("INSERT INTO `Spectrum`(%s) VALUES ",
                        Arrays.stream(spectrumFields)
                                .map(SqlField::getName)
                                .map(x -> String.format("`%s`", x))
                                .collect(Collectors.joining(", "))));

        for (int i = 0; i < fileList.size(); i++) {
            final List<Spectrum> spectra = fileList.get(i).getSpectra();
            if (spectra == null) continue;
            spectrumList.addAll(spectra);

            for (int j = 0; j < spectra.size(); j++) {
                if (i != 0 || j != 0) {
                    insertSql.append(COMMA);
                }
                final Spectrum spectrum = spectra.get(j);
                spectrum.setFile(fileList.get(i));

                // Set peakDataEncoded
                if (spectrum.getPeakDataEncoded() == null && spectrum.getPeaks() != null && !spectrum.getPeaks().isEmpty()) {
                    double[] mz = spectrum.getPeaks().stream().mapToDouble(Peak::getMz).toArray();
                    double[] intensity = spectrum.getPeaks().stream().mapToDouble(Peak::getIntensity).toArray();
                    try {
                        spectrum.setPeakDataEncoded(ByteArrayUtils.compressDoubleArrays(mz, intensity));
                    }
                    catch (Exception e) {
                        LOGGER.error("Failed to encode peaks for spectrum: " + spectrum.getName() + " (ID: " + spectrum.getId() + ")");
                    }
                }
                // Set identifiersJson
                if (spectrum.getIdentifiersJson() == null && spectrum.getIdentifiers() != null && !spectrum.getIdentifiers().isEmpty()) {
                    Map<String, String> jsonMap = new LinkedHashMap<>();
                    for (Identifier identifier : spectrum.getIdentifiers()) {
                        jsonMap.put(identifier.getType().toString(), identifier.getValue());
                    }
                    try {
                        spectrum.setIdentifiersJson(new ObjectMapper().writeValueAsString(jsonMap));
                    } catch (Exception e) {
                        spectrum.setIdentifiersJson(null);
                    }
                }
                insertSql.append(String.format("(%s)",
                        Arrays.stream(spectrumFields)
                                .map(field -> String.format(field.format, field.function.apply(spectrum)))
                                .collect(Collectors.joining(", "))));
            }
        }

        return insertSql.toString();
    }

    public List<Spectrum> getSpectrumList() {
        return spectrumList;
    }

    private static String quote(String x) {
        if (x == null) return null;
        return String.format("\"%s\"", x.replace("\"", "\"\""));
    }


    private static class SqlField {

        private final String name;
        private final String format;
        private final Function<Spectrum, Object> function;

        public SqlField(String name, String format, Function<Spectrum, Object> function) {
            this.name = name;
            this.format = format;
            this.function = function;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public Function<Spectrum, Object> getFunction() {
            return function;
        }
    }
}
