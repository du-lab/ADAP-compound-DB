package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.site.repositories.DistributionRepository;
import org.dulab.adapcompounddb.site.services.DistributionService;
import org.dulab.adapcompounddb.site.services.SpectrumClusterer;
import org.dulab.adapcompounddb.site.services.admin.SpectrumMatchCalculator;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private final SubmissionService submissionService;

    public AdminRestController(final SpectrumMatchCalculator spectrumMatchCalculator,
                               final SpectrumClusterer spectrumClusterer,
                               final DistributionRepository distributionRepository,
                               final DistributionService distributionService,
                               SubmissionService submissionService) {

        this.spectrumMatchCalculator = spectrumMatchCalculator;
        this.spectrumClusterer = spectrumClusterer;
        this.distributionService = distributionService;
        this.executor = Executors.newCachedThreadPool();
        this.submissionService = submissionService;
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
        spectrumMatchCalculator.setProgress(0F);
        executor.submit(spectrumMatchCalculator::run);
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

    @RequestMapping(value = "/get/submissions", produces = "application/json")
    public String getSubmissions(@RequestParam("start") Integer start,
                                 @RequestParam("length") Integer length,
                                 @RequestParam("column") Integer column,
                                 @RequestParam("sortDirection") String sortDirection,
                                 @RequestParam("search") String searchStr) throws JsonProcessingException {

        String sortColumn = ColumnInformation.getColumnNameFromPosition(column);
        if (sortColumn == null) {
            sortColumn = ColumnInformation.getDefaultSortColumn();
            sortDirection = "DESC";
        }

        Pageable pageable;
        if (sortColumn != null) {
            Sort sort = new Sort(Sort.Direction.fromString(sortDirection), sortColumn);
            pageable = PageRequest.of(start / length, length, sort);
        } else {
            pageable = PageRequest.of(start / length, length);
        }

        DataTableResponse response = submissionService.findAllSubmissions(searchStr, pageable);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper.writeValueAsString(response);
    }

    private enum ColumnInformation {
        ID(0, "id"), DATE(1, "dateTime"),
        NAME(2, "name"), EXTERNALID(3, "externalId"),
        USER(4, "user.username");

        private int position;
        private String sortColumnName;

        private ColumnInformation(final int position, final String sortColumnName) {
            this.position = position;
            this.sortColumnName = sortColumnName;
        }

        public int getPosition() {
            return position;
        }

        public String getSortColumnName() {
            return sortColumnName;
        }

        public static String getColumnNameFromPosition(final int position) {
            String columnName = null;
            for (final ColumnInformation columnInformation : ColumnInformation.values()) {
                if (position == columnInformation.getPosition()) {
                    columnName = columnInformation.getSortColumnName();
                }
            }
            return columnName;
        }

        public static String getDefaultSortColumn() {
            return ColumnInformation.DATE.getSortColumnName();
        }
    }
}
