package org.dulab.adapcompounddb.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.FileIndexAndSpectrumIndexBestMatchPair;
import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SpectrumIndexAndBestMatchPair;
import org.dulab.adapcompounddb.models.dto.DataTableResponse;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.SearchController;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
import org.dulab.adapcompounddb.site.services.SpectrumSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class GroupSearchController {

    private final Map<ChromatographyType, SpectrumSearchService> spectrumSearchServiceMap;
    private final SpectrumMatchService spectrumMatchService;

    @Autowired
    public GroupSearchController(final SpectrumMatchService spectrumMatchService,
                                 @Qualifier("spectrumSearchServiceGCImpl") final SpectrumSearchService gcSpectrumSearchService,
                                 @Qualifier("spectrumSearchServiceLCImpl") final SpectrumSearchService lcSpectrumSearchService) {

        this.spectrumMatchService = spectrumMatchService;
        this.spectrumSearchServiceMap = new HashMap<>();
        this.spectrumSearchServiceMap.put(ChromatographyType.GAS, gcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_POSITIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LIQUID_NEGATIVE, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_POS, lcSpectrumSearchService);
        this.spectrumSearchServiceMap.put(ChromatographyType.LC_MSMS_NEG, lcSpectrumSearchService);
    }

    @RequestMapping(value = "/file/group_search_results/data", produces = "application/json")
    public String fileGroupSearchResults(
            @RequestParam("start") final Integer start,
            @RequestParam("length") final Integer length,
            @RequestParam("column") final Integer column,
            @RequestParam("sortDirection") final String sortDirection,
            @RequestParam("search") final String searchStr,
            final HttpSession session, final SearchController.SearchForm form) throws JsonProcessingException {

        // Assume scoreThreshold = 0.75
        // mzTolerance = 0.01

        /*
        1. Match all spectra from the session to the library and get a list of SpectrumMatch
        2. Sort the List<SpectrumMatch> based on the `column` and `sortDirection` parameters.
        3. Based on "start", "length", return only the required results
        4. Return json-string of DataTableResponse with the matching results
         */

        // 1.
        final Submission submission = Submission.from(session);
        if (submission == null) {
            return "redirect:/file/upload/";
        }

        final DataTableResponse response = spectrumMatchService.groupSearchSort(searchStr, start,
                length, column, sortDirection, groupSearchPost(submission, form));

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        final String jsonString = mapper.writeValueAsString(response);
        System.out.println(jsonString);
        return jsonString;
    }


    private List<FileIndexAndSpectrumIndexBestMatchPair> groupSearchPost(final HttpSession session,
            final Submission submission, final SearchController.SearchForm form) {
        final List<File> spectrumFiles = submission.getFiles();
        final Map<Integer, List<Spectrum>> spectrumListAndIndexMap = new HashMap<>();
        final List<FileIndexAndSpectrumIndexBestMatchPair> fileIndexAndSpectrumIndexBestMatchPairList = new ArrayList<>();
        QueryParameters parameters = new QueryParameters();
        parameters.setScoreThreshold(form.isScoreThresholdCheck() ? form.getFloatScoreThreshold() : null);
        parameters.setMzTolerance(form.isScoreThresholdCheck() ? form.getMzTolerance() : null);
        parameters.setPrecursorTolerance(form.isMassToleranceCheck() ? form.getMassTolerance() : null);
        parameters.setRetTimeTolerance(form.isRetTimeToleranceCheck() ? form.getRetTimeTolerance() : null);


        //TODO: Instead of using for-loop and service.search(), we can use session.getAttribute("group_search_results") to get List<SpectrumMatch>


        for (int i = 0; i < spectrumFiles.size(); i++) {
            spectrumListAndIndexMap.put(i, spectrumFiles.get(i).getSpectra());
        }

        for (Map.Entry<Integer, List<Spectrum>> entry : spectrumListAndIndexMap.entrySet()) {

            int fileIndex = entry.getKey();
            List<Spectrum> querySpectrumList = entry.getValue();

            for (int n = 0; n < querySpectrumList.size(); n++) {
                int spectrumIndex = n;
                SpectrumSearchService service =
                        spectrumSearchServiceMap.get(querySpectrumList.get(n).getChromatographyType());

                List<SpectrumMatch> matches = service.search(querySpectrumList.get(n), parameters);


                FileIndexAndSpectrumIndexBestMatchPair fileIndexAndSpectrumIndexBestMatchPair = new FileIndexAndSpectrumIndexBestMatchPair();
                SpectrumIndexAndBestMatchPair spectrumIndexAndBestMatchPair = new SpectrumIndexAndBestMatchPair();

/*            final String tags = form.getTags();
            parameters.setTags(
                    tags != null && tags.length() > 0
                            ? new HashSet<>(Arrays.asList(tags.split(",")))
                            : null);*/

                // get the best match if the match is not null
                if (matches.size() > 0) {
                    spectrumIndexAndBestMatchPair.setSpectrumIndex(spectrumIndex);
                    spectrumIndexAndBestMatchPair.setBestMatch(matches.get(0));
                    fileIndexAndSpectrumIndexBestMatchPair.setFileIndex(fileIndex);
                    fileIndexAndSpectrumIndexBestMatchPair.setSpectrumIndexAndBestMatchPair(spectrumIndexAndBestMatchPair);
                    fileIndexAndSpectrumIndexBestMatchPairList.add(fileIndexAndSpectrumIndexBestMatchPair);

                } else {
                    SpectrumMatch noneMatch = new SpectrumMatch();

                    noneMatch.setQuerySpectrum(querySpectrumList.get(n));
                    spectrumIndexAndBestMatchPair.setSpectrumIndex(spectrumIndex);
                    spectrumIndexAndBestMatchPair.setBestMatch(noneMatch);
                    fileIndexAndSpectrumIndexBestMatchPair.setFileIndex(fileIndex);
                    fileIndexAndSpectrumIndexBestMatchPair.setSpectrumIndexAndBestMatchPair(spectrumIndexAndBestMatchPair);
                    fileIndexAndSpectrumIndexBestMatchPairList.add(fileIndexAndSpectrumIndexBestMatchPair);

                }
            }

        }
        return fileIndexAndSpectrumIndexBestMatchPairList;
    }
}