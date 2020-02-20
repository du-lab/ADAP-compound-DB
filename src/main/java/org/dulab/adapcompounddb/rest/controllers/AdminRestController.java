package org.dulab.adapcompounddb.rest.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.site.repositories.DistributionRepository;
import org.dulab.adapcompounddb.site.services.DistributionService;
import org.dulab.adapcompounddb.site.services.SpectrumClusterer;
import org.dulab.adapcompounddb.site.services.SpectrumMatchCalculator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private static final Logger LOGGER = LogManager.getLogger(AdminRestController.class);

    private final SpectrumMatchCalculator spectrumMatchCalculator;
    private final SpectrumClusterer spectrumClusterer;
    private final ExecutorService executor;

    private final DistributionService distributionService;

    public AdminRestController(final SpectrumMatchCalculator spectrumMatchCalculator,
                               final SpectrumClusterer spectrumClusterer, final DistributionRepository distributionRepository, final DistributionService distributionService) {
        this.spectrumMatchCalculator = spectrumMatchCalculator;
        this.spectrumClusterer = spectrumClusterer;
        this.distributionService = distributionService;
        executor = Executors.newCachedThreadPool();
    }

    @RequestMapping(value = "/calculatescores/progress", produces = "application/json")
    public int calculateScoresProgress() {
        return Math.round(100 * spectrumMatchCalculator.getProgress());
    }

    @RequestMapping(value = "/cluster/progress", produces = "application/json")
    public int calculateClustersProgress() {
        return Math.round(100 * spectrumClusterer.getProgress());
    }

    @RequestMapping(value = "/admin/calculatescores", method = RequestMethod.GET)
    public String calculateScores() {
        //        spectrumMatchService.fillSpectrumMatchTable(0.01F, 0.75F);
        final Runnable r = () -> spectrumMatchCalculator.run();
        spectrumMatchCalculator.setProgress(0F);
        executor.submit(r);
        return "OK";
    }

    @RequestMapping(value = "/admin/cluster", method = RequestMethod.GET)
    public String cluster() {
        try {
            final Runnable r = () -> {
                try {
                    distributionService.removeAll();
                    distributionService.saveAllDbDistributions();
                    spectrumClusterer.removeAll();
                    spectrumClusterer.cluster();

                } catch (Exception e) {
                    LOGGER.error("Error during clustering: ", e);
                    throw new IllegalStateException("Error during clustering: " + e.getMessage(), e);
                }
            };
            spectrumClusterer.setProgress(0F);
            executor.submit(r);
        } catch (final Exception e) {
            LOGGER.error("Error during clustering: ", e);
            throw new IllegalStateException("Error during clustering: " + e.getMessage(), e);
        }
        return "OK";
    }
}
