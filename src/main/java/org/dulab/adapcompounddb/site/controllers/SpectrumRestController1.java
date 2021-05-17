package org.dulab.adapcompounddb.site.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class SpectrumRestController1 {

    private static final Logger LOGGER = LogManager.getLogger(SpectrumRestController1.class);

    private final SpectrumService spectrumService;


    @Autowired
    public SpectrumRestController1(SpectrumService spectrumService) {
        this.spectrumService = spectrumService;
    }

    @RequestMapping(value = {
            "/spectrum/{spectrumId:\\d+}/search/{sign}/peaks",
            "/submission/*/spectrum/{spectrumId:\\d+}/search/{sign}/peaks"},
            produces = "application/json")
    public String spectrumSearchPeaks(@PathVariable("spectrumId") long spectrumId,
                                      @PathVariable("sign") String sign) {
        Spectrum spectrum = null;
        try {
            spectrum = spectrumService.find(spectrumId);
        } catch (EmptySearchResultException e) {
            LOGGER.warn("Cannot find spectrum with ID = " + spectrumId);
        }
        return spectrumToJsonPeaks(spectrum, sign);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/{sign}/peaks", produces = "application/json")
    public String spectrumSearchPeaks(@PathVariable("fileIndex") int fileIndex,
                                      @PathVariable("spectrumIndex") int spectrumIndex,
                                      @PathVariable("sign") String sign, HttpSession session) {
        Submission submission = Submission.from(session);
        Spectrum spectrum;
        try {
            spectrum = submission.getFiles().get(fileIndex).getSpectra().get(spectrumIndex);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warn(e.getMessage(), e);
            return "";
        }

        return spectrumToJsonPeaks(spectrum, sign);
    }

    private String spectrumToJsonPeaks(@Nullable Spectrum spectrum, String sign) {

        if (spectrum == null)
            return "";

        int intensityFactor = sign.equals("negative") ? -1 : 1;

        JSONObject root = new JSONObject();
        root.put("name", spectrum.getName());

        JSONArray peaks = new JSONArray();
        if (spectrum.getPeaks() != null) {
            for (Peak peak : spectrum.getPeaks()) {
                JSONObject p = new JSONObject();
                p.put("mz", peak.getMz());
                p.put("intensity", intensityFactor * peak.getIntensity());
                peaks.put(p);
            }
        }
        root.put("peaks", peaks);

        return root.toString();
    }

    @RequestMapping(value = {
            "/spectrum/{spectrumId:\\d+}/search/info",
            "/submission/*/spectrum/{spectrumId:\\d+}/search/info"},
            produces = "application/json")
    public String spectrumSearchInfo(@PathVariable("spectrumId") long spectrumId) {
        Spectrum spectrum = null;
        try {
            spectrum = spectrumService.find(spectrumId);
        } catch (EmptySearchResultException e) {
            LOGGER.warn("Cannot find spectrum with ID = " + spectrumId);
        }
        return spectrumToJsonInfo(spectrum, null, null);
    }

    @RequestMapping(value = "/file/{fileIndex:\\d+}/{spectrumIndex:\\d+}/search/info", produces = "application/json")
    public String spectrumSearchInfo(@PathVariable("fileIndex") int fileIndex,
                                     @PathVariable("spectrumIndex") int spectrumIndex, HttpSession session) {

        Submission submission = Submission.from(session);
        Spectrum spectrum;
        try {
            spectrum = submission.getFiles().get(fileIndex).getSpectra().get(spectrumIndex);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warn(e.getMessage(), e);
            return "";
        }

        return spectrumToJsonInfo(spectrum, fileIndex, spectrumIndex);
    }

    private String spectrumToJsonInfo(@Nullable Spectrum spectrum, Integer fileIndex, Integer spectrumIndex) {

        if (spectrum == null)
            return "";

        JSONObject root = new JSONObject();
        root.put("name", spectrum.getName());
        root.put("id", spectrum.getId());
        root.put("fileIndex", fileIndex);
        root.put("spectrumIndex", spectrumIndex);
        root.put("chromatographyType", spectrum.getChromatographyType().getLabel());

        JSONArray standardProperties = new JSONArray();
        standardProperties.put(propertyToJsonObject("Precursor m/z", spectrum.getPrecursor()));
        standardProperties.put(propertyToJsonObject("Precursor type", spectrum.getPrecursorType()));
        standardProperties.put(propertyToJsonObject("Neutral mass", spectrum.getMass()));
        standardProperties.put(propertyToJsonObject("Retention time", spectrum.getRetentionTime()));
        root.put("standardProperties", standardProperties);

        JSONArray otherProperties = new JSONArray();
        for (SpectrumProperty property : spectrum.getProperties())
            otherProperties.put(propertyToJsonObject(property));
        root.put("otherProperties", otherProperties);

        return root.toString();
    }

    private <E> JSONObject propertyToJsonObject(String name, E value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("value", (value != null) ? value.toString() : null);
        return jsonObject;
    }

    private JSONObject propertyToJsonObject(SpectrumProperty spectrumProperty) {
        return propertyToJsonObject(spectrumProperty.getName(), spectrumProperty.getValue());
    }
}
