package org.dulab.adapcompounddb.site.services.io;

import org.dulab.adapcompounddb.models.entities.*;
import org.springframework.stereotype.Service;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MspFileWriterService {

    public void writeMspFile(List<Spectrum> spectra, OutputStream outputStream) throws IOException {
        try (PrintWriter writer = new PrintWriter(outputStream)) {
            for (Spectrum spectrum : spectra) {
                writeSpectrumToMspFile(spectrum, writer);
            }
        }
    }

    private void writeSpectrumToMspFile(Spectrum spectrum, PrintWriter writer) throws IOException {

        SpectrumCluster cluster = Objects.requireNonNull(spectrum.getCluster());

        writer.println("Name: " + spectrum.getShortName());
        writer.println("DB#: " + cluster.getId());
        writer.println("ADAP-KDB ID: " + cluster.getId());
        writer.println(String.format("URL: https://adap.cloud/cluster/%d/", cluster.getId()));

        if (spectrum.getPrecursor() != null)
            writer.println(String.format("PrecursorMZ: %.4f", spectrum.getPrecursor()));
        if (spectrum.getPrecursorType() != null)
            writer.println("Precursor_type: " + spectrum.getPrecursorType());
        if (spectrum.getFormula() != null)
            writer.println("Formula: " + spectrum.getFormula());
        if (spectrum.getMass() != null)
            writer.println(String.format("Neutral mass: %.4f", spectrum.getMass()));

        Set<Submission> submissions = cluster.getSpectra()
                .stream().filter(s -> s != spectrum)
                .map(Spectrum::getFile).filter(Objects::nonNull)
                .map(File::getSubmission).filter(Objects::nonNull)
                .collect(Collectors.toSet());

        writer.println("Studies: " + submissions.stream()
                .map(Submission::getExternalId).filter(Objects::nonNull)
                .collect(Collectors.joining(", ")));

        writer.println("Species: " + submissions.stream()
                .map(s -> s.getTagValue("species (common)")).filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", ")));

        writer.println("Sample sources: " + submissions.stream()
                .map(s -> s.getTagValue("sample source")).filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", ")));

        writer.println("Diseases: " + submissions.stream()
                .map(s -> s.getTagValue("disease")).filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", ")));

        List<Peak> peaks = spectrum.getPeaks();
        double maxIntensity = peaks.stream()
                .mapToDouble(Peak::getIntensity)
                .max()
                .orElseThrow(() -> new IIOException("Cannot find the maximum peak intensity"));

        double intensityFactor = 100.0 / maxIntensity;

        writer.println("Num Peaks: " + peaks.size());
        for (Peak peak : peaks) {
            writer.println(String.format("%.4f %.4f", peak.getMz(), intensityFactor * peak.getIntensity()));
        }

        writer.println();
    }
}
