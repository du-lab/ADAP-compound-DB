package org.dulab.adapcompounddb.rest.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.enums.ChromatographyType;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.io.MspFileWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
public class DownloadRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadRestController.class);

    private final MspFileWriterService mspFileWriterService;
    private final SpectrumService spectrumService;

    @Autowired
    public DownloadRestController(MspFileWriterService mspFileWriterService, SpectrumService spectrumService) {
        this.mspFileWriterService = mspFileWriterService;
        this.spectrumService = spectrumService;
    }

    @RequestMapping(value = "ajax/download/lr_gcms", method = RequestMethod.GET)
    public void downloadConsensusLowResGCMSSpectra(HttpServletResponse response) {
        downloadSpectra(response, ChromatographyType.GAS, MassSpectrometryType.LOW_RESOLUTION);
    }

    @RequestMapping(value = "ajax/download/hr_gcms", method = RequestMethod.GET)
    public void downloadConsensusHighResGCMSSpectra(HttpServletResponse response) {
        downloadSpectra(response, ChromatographyType.GAS, MassSpectrometryType.HIGH_RESOLUTION);
    }

    @RequestMapping(value = "ajax/download/lcmsms_pos", method = RequestMethod.GET)
    public void downloadConsensusPositiveLCMSMSSpectra(HttpServletResponse response) {
        downloadSpectra(response, ChromatographyType.LC_MSMS_POS, MassSpectrometryType.HIGH_RESOLUTION);
    }

    @RequestMapping(value = "ajax/download/lcmsms_neg", method = RequestMethod.GET)
    public void downloadConsensusNegativeLCMSMSSpectra(HttpServletResponse response) {
        downloadSpectra(response, ChromatographyType.LC_MSMS_NEG, MassSpectrometryType.HIGH_RESOLUTION);
    }

    private void downloadSpectra(HttpServletResponse response, ChromatographyType chromatographyType,
                                 MassSpectrometryType massSpectrometryType) {

        String filename = String.format(
                "adap-kdb_consensus_spectra.%s_%s.msp", chromatographyType.name(), massSpectrometryType.name());

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));

        List<Spectrum> spectra = spectrumService
                .findConsensusSpectraByChromatographyType(chromatographyType, massSpectrometryType);
        try {
            mspFileWriterService.writeMspFile(spectra, response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException("Cannot create a file of consensus spectra: " + e.getMessage(), e);
        }
    }
}
