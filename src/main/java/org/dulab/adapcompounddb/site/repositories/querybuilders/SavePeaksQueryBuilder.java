package org.dulab.adapcompounddb.site.repositories.querybuilders;

import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;

import java.util.ArrayList;
import java.util.List;

public class SavePeaksQueryBuilder {

    private static final String PEAK_INSERT_SQL_STRING = "INSERT INTO `Peak`(`Mz`, `Intensity`, `SpectrumId`) VALUES ";

    private final List<Spectrum> spectrumList;
    private final List<Long> spectrumIds;
    private final int maxPeaksInQuery;


    public SavePeaksQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds) {
        this(spectrumList, spectrumIds, 1000000);
    }

    public SavePeaksQueryBuilder(List<Spectrum> spectrumList, List<Long> spectrumIds, int maxPeaksInQuery) {
        this.spectrumList = spectrumList;
        this.spectrumIds = spectrumIds;
        this.maxPeaksInQuery = maxPeaksInQuery;
    }

    public String[] build() {

        List<List<String>> peakTriplesList = new ArrayList<>();
        List<String> peakTriples = new ArrayList<>();
        int peakCount = 0;
        for (int i = 0; i < spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();

            if (peaks == null)
                continue;

            for (Peak peak : peaks) {
                peakTriples.add(String.format("(%f, %f, %d)", peak.getMz(), peak.getIntensity(), spectrumIds.get(i)));
            }

            peakCount += peaks.size();

            if (peakCount > maxPeaksInQuery) {
                peakTriplesList.add(peakTriples);
                peakTriples = new ArrayList<>();
                peakCount = 0;
            }
        }

        if (peakCount > 0)
            peakTriplesList.add(peakTriples);

        return peakTriplesList.stream()
                .map(triples -> String.join(",", triples))
                .filter(s -> !s.isEmpty())
                .map(s -> PEAK_INSERT_SQL_STRING + s)
                .toArray(String[]::new);
    }
}
