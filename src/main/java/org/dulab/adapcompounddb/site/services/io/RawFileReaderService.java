package org.dulab.adapcompounddb.site.services.io;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapbig.base.RawDataFile;
import org.dulab.adapbig.base.Scan;
import org.dulab.adapbig.input.InputModule;
import org.dulab.adapcompounddb.models.MetaDataMapping;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RawFileReaderService implements FileReaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RawFileReaderService.class);

    @Override
    public List<Spectrum> read(InputStream inputStream, @Nullable MetaDataMapping mapping, String filename,
                               ChromatographyType chromatographyType)
            throws IOException {

        List<Spectrum> spectra = new ArrayList<>();

        File file = File.createTempFile("ADAP_KDB", filename.replace('/', '_'));

        try {
            FileUtils.copyInputStreamToFile(inputStream, file);

            RawDataFile rawDataFile = InputModule.readFile(file.toPath(), null);
            if (rawDataFile == null) return spectra;

            List<Scan> scans = rawDataFile.getScans();
            if (scans == null) return spectra;

            int minMsLevel = (chromatographyType == ChromatographyType.LC_MSMS_POS
                    || chromatographyType == ChromatographyType.LC_MSMS_NEG) ? 2 : 1;
            convertScansToSpectra(scans, spectra, minMsLevel, chromatographyType);

        } finally {
            boolean deleted = file.delete();
            if (!deleted)
                LOGGER.warn(String.format("File '%s' has not been deleted", file.getAbsolutePath()));
        }

        return spectra;
    }

    @Override
    public MetaDataMapping validateMetaDataMapping(MetaDataMapping mapping) {
        return mapping;
    }

    private void convertScansToSpectra(List<Scan> scans, List<Spectrum> spectra, int minMsLevel,
                                       ChromatographyType chromatographyType) {

        for (Scan scan : scans) {
            if (scan.getMsLevel() >= minMsLevel)
                spectra.add(convertScanToSpectrum(scan, chromatographyType));

            List<Scan> ms2Scans = scan.getMs2Scans();
            if (ms2Scans != null)
                convertScansToSpectra(ms2Scans, spectra, minMsLevel, chromatographyType);
        }
    }

    private ChromatographyType adjustPolarity(Scan scan, ChromatographyType chromatographyType) {

        char polarity = scan.getPolarity();

        ChromatographyType adjustedChromatographyType = chromatographyType;

        switch (chromatographyType) {
            case LC_MSMS_POS:
            case LC_MSMS_NEG:
                if (polarity == '+')
                    adjustedChromatographyType = ChromatographyType.LC_MSMS_POS;
                else if (polarity == '-')
                    adjustedChromatographyType = ChromatographyType.LC_MSMS_NEG;
                break;
            case LIQUID_POSITIVE:
            case LIQUID_NEGATIVE:
                if (polarity == '+')
                    adjustedChromatographyType = ChromatographyType.LIQUID_POSITIVE;
                else if (polarity == '-')
                    adjustedChromatographyType = ChromatographyType.LIQUID_NEGATIVE;
                break;
        }

        return adjustedChromatographyType;
    }

    private Spectrum convertScanToSpectrum(Scan scan, ChromatographyType chromatographyType) {
        double[] mzValues = scan.getMzValues();
        double[] intensities = scan.getIntensities();

        if (mzValues.length != intensities.length)
            throw new IllegalStateException("The numbers of m/z values and intensities do not match.");

        Spectrum spectrum = new Spectrum();
        spectrum.setName(scan.getName());
        spectrum.setRetentionTime(scan.getRetTime());
        spectrum.setPrecursor(scan.getPrecursorMz());
        spectrum.setChromatographyType(adjustPolarity(scan, chromatographyType));

        List<Peak> peaks = new ArrayList<>(mzValues.length);
        for (int i = 0; i < mzValues.length; ++i) {
            Peak peak = new Peak();
            peak.setMz(mzValues[i]);
            peak.setIntensity(intensities[i]);
            peak.setSpectrum(spectrum);
            peaks.add(peak);
        }
        spectrum.setPeaks(peaks, true);

        return spectrum;
    }
}
