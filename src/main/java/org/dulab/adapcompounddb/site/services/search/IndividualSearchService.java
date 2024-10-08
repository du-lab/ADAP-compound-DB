package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Adduct;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.ontology.OntologyLevel;
import org.dulab.adapcompounddb.models.ontology.OntologySupplier;
import org.dulab.adapcompounddb.models.ontology.Parameters;
import org.dulab.adapcompounddb.site.repositories.SpectrumMatchRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.AdductService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class IndividualSearchService {

    private final SpectrumRepository spectrumRepository;
    private final SubmissionService submissionService;
    private final AdductService adductService;
    private final JavaSpectrumSimilarityService javaSpectrumSimilarityService;
    private final SpectrumMatchRepository spectrumMatchRepository;

    @Autowired
    public IndividualSearchService(SpectrumRepository spectrumRepository, AdductService adductService,
                                   JavaSpectrumSimilarityService javaSpectrumSimilarityService,
                                   SubmissionService submissionService, SpectrumMatchRepository spectrumMatchRepository) {

        this.spectrumRepository = spectrumRepository;
        this.adductService = adductService;
        this.javaSpectrumSimilarityService = javaSpectrumSimilarityService;
        this.submissionService = submissionService;
        this.spectrumMatchRepository = spectrumMatchRepository;
    }

    @Transactional
    public List<SpectrumMatch> search(Spectrum querySpectrum, QueryParameters parameters) {
        return spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum,
                parameters);
    }

    @Transactional
    public List<SearchResultDTO> searchConsensusSpectra(UserPrincipal user, Spectrum querySpectrum,
                                                        SearchParameters parameters) {

        List<SearchResultDTO> searchResults = new ArrayList<>();
        int matchIndex = 0;

        List<SpectrumMatch> matches = javaSpectrumSimilarityService.searchConsensusAndReference(
                querySpectrum, parameters, user);

        for (SpectrumMatch match : matches) {

            SearchResultDTO searchResult = MappingUtils.mapSpectrumMatchToSpectrumClusterView(match,
                    matchIndex++, parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            searchResult.setChromatographyTypeLabel(
                    match.getMatchSpectrum().getChromatographyType().getLabel());
            searchResult.setLibraryPeakMzs(match.getLibraryPeakMzList());
            searchResult.setQueryPeakMzs(match.getQueryPeakMzList());
            searchResults.add(searchResult);

        }

        return searchResults;
    }

    @Transactional
    public List<SearchResultDTO> searchConsensusSpectra(UserPrincipal user, Spectrum querySpectrum,
                                                        SearchParameters parameters, boolean savedSubmission, List<SpectrumMatch> saveMatches,
                                                        Set<Long> deleteMatches) {

        List<SearchResultDTO> searchResults = new ArrayList<>();
        int matchIndex = 0;

        List<SpectrumMatch> matches = javaSpectrumSimilarityService.searchConsensusAndReference(
                querySpectrum, parameters, user);

        if (user != null && savedSubmission && (saveMatches != null && deleteMatches != null)) {
            if (!matches.isEmpty()) {
                matches.forEach(match -> match.setUserPrincipalId(user.getId()));
                saveMatches.addAll(matches);
                deleteMatches.addAll(
                        matches.stream().map(SpectrumMatch::getQuerySpectrum).map(Spectrum::getId)
                                .collect(Collectors.toList()));


            } else {

                deleteMatches.add(querySpectrum.getId());
                SpectrumMatch emptyMatch = new SpectrumMatch();
                emptyMatch.setQuerySpectrum(querySpectrum);
                emptyMatch.setUserPrincipalId(user.getId());
                saveMatches.add(emptyMatch);


            }
        }

        for (SpectrumMatch match : matches) {

            SearchResultDTO searchResult = MappingUtils.mapSpectrumMatchToSpectrumClusterView(match,
                    matchIndex++, parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            searchResult.setChromatographyTypeLabel(
                    match.getMatchSpectrum().getChromatographyType().getLabel());
            searchResult.setQueryPeakMzs(match.getQueryPeakMzList());
            searchResult.setLibraryPeakMzs(match.getLibraryPeakMzList());
            searchResults.add(searchResult);
        }

        return searchResults;
    }


    //    @Transactional
    public List<SearchResultDTO> searchWithOntologyLevels(UserPrincipal user, Spectrum spectrum,
                                                          SearchParameters parameters, boolean savedSubmission, List<SpectrumMatch> saveMatches,
                                                          Set<Long> deleteMatches) {

        // Check if there are ontology levels for a given chromatography type
        int[] priorities = OntologySupplier.findPrioritiesByChromatographyType(
                spectrum.getChromatographyType());

        if (priorities == null) {
            throw new IllegalStateException(
                    "No priority values found for " + spectrum.getChromatographyType());
        }

//        SearchParameters modifiedParameters;
//        try {
//            modifiedParameters = parameters.clone();
//        } catch (CloneNotSupportedException e) {
//            throw new IllegalStateException(e.getMessage(), e);
//        }

        parameters.setScoreThreshold(null);
        parameters.setMzTolerance(null, Parameters.MZ_TOLERANCE_PPM);
        parameters.setPrecursorTolerance(null, null);
        parameters.setRetTimeTolerance(null);
        parameters.setRetIndexTolerance(null);
        parameters.setRetIndexMatchType(SearchParameters.RetIndexMatchType.IGNORE_MATCH);
        parameters.setMassTolerance(null, Parameters.MASS_TOLERANCE_PPM);
        parameters.setPenalizeQueryImpurities(true);
        parameters.setPenalizeDominantPeak(false);

        SearchParameters modifiedParameters;
        try {
            modifiedParameters = parameters.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        List<Adduct> adducts = adductService.findAdductsByChromatography(
                spectrum.getChromatographyType());
        if (spectrum.getMass() == null && adducts != null && spectrum.getPrecursor() != null) {
            modifiedParameters.setAdducts(adducts);
        }
//                    adducts.stream()
//                    .collect(Collectors.toMap(a -> a.calculateNeutralMass(spectrum.getPrecursor()), Adduct::getName));
//                    .mapToDouble(adduct -> adduct.calculateNeutralMass(spectrum.getPrecursor()))
//                    .toArray());

//        Map<Long, String> privateSubmissionIdMap =
//                submissionService.findUserPrivateSubmissions(user, spectrum.getChromatographyType());
//        Set<Long> privateSubmissionIds = privateSubmissionIdMap.keySet();

        List<SpectrumMatch> matches = javaSpectrumSimilarityService.searchConsensusAndReference(
                spectrum, modifiedParameters, user);

        List<SearchResultDTO> results = new ArrayList<>(matches.size());
        for (int i = 0; i < matches.size(); ++i) {
            SearchResultDTO searchResult = (MappingUtils.mapSpectrumMatchToSpectrumClusterView(matches.get(i), i,
                    modifiedParameters.getSpecies(), modifiedParameters.getSource(),
                    modifiedParameters.getDisease()));
            searchResult.setQueryPeakMzs(matches.get(i).getQueryPeakMzList());
            searchResult.setLibraryPeakMzs(matches.get(i).getLibraryPeakMzList());
            results.add(searchResult);
        }
        int step = 0;
        List<SearchResultDTO> resultsWithOntology = new ArrayList<>();
        for (SearchResultDTO result : results) {

            OntologyLevel ontologyLevel = OntologySupplier.select(spectrum.getChromatographyType(),
                    result.getInHouse(), result.getScore(), result.getPrecursorErrorPPM(),
                    result.getMassErrorPPM(), result.getRetTimeError(), result.getIsotopicSimilarity());

            if (ontologyLevel != null) {
                if (ontologyLevel.getScoreThreshold() == null) {
                    result.setScore(0.0);
                }
                if (ontologyLevel.getRetTimeTolerance() == null) {
                    result.setRetTimeError(Double.MAX_VALUE);
                }
                if (ontologyLevel.getMassTolerancePPM() == null) {
                    result.setMassErrorPPM(Double.MAX_VALUE);
                }

                result.setOntologyLevel(ontologyLevel);

                //set ontology level to matches as well
                matches.get(step).setOntologyLevelObj(ontologyLevel);
            }

            resultsWithOntology.add(result);

            step++;
        }
        if (user != null && savedSubmission && (saveMatches != null && deleteMatches != null)) {
            if (!matches.isEmpty()) {
                matches.forEach(match -> match.setUserPrincipalId(user.getId()));
                saveMatches.addAll(matches);
                deleteMatches.addAll(
                        matches.stream().map(SpectrumMatch::getQuerySpectrum).map(Spectrum::getId)
                                .collect(Collectors.toList()));
            } else {
                //save query spectrum even when there's no match
                deleteMatches.add(spectrum.getId());
                SpectrumMatch emptyMatch = new SpectrumMatch();
                emptyMatch.setQuerySpectrum(spectrum);
                emptyMatch.setUserPrincipalId(user.getId());
                saveMatches.add(emptyMatch);

            }
        }
        IntStream.range(0, resultsWithOntology.size())
                .forEach(i -> resultsWithOntology.get(i).setMatchIndex(i));

        if (!resultsWithOntology.isEmpty()) {
            markBestSearchResults(resultsWithOntology);
        }

        return resultsWithOntology;
    }

//    private Set<BigInteger> getSubmissionIds(SearchParameters parameters) {
//        Set<BigInteger> submissionIds = (parameters.getSubmissionIds() != null)
//                ? parameters.getSubmissionIds()
//                : new HashSet<>();
//
//        if (submissionIds.isEmpty() || submissionIds.contains(BigInteger.ZERO)) {
//            Iterable<BigInteger> publicSubmissionIds = submissionRepository.findSubmissionIdsBySubmissionTags(
//                    parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
//            publicSubmissionIds.forEach(submissionIds::add);
//            submissionIds.remove(BigInteger.ZERO);
//        }
//        return submissionIds;
//    }

    private void markBestSearchResults(List<SearchResultDTO> searchResults) {
        List<SearchResultDTO> bestSearchResults = new ArrayList<>();
        for (SearchResultDTO searchResult : searchResults) {

            boolean higher = true;
            boolean equal = true;
            for (SearchResultDTO bestSearchResult : bestSearchResults) {
                int comparison = searchResult.compareTo(bestSearchResult);
                if (comparison <= 0) {
                    higher = false;
                }
                if (comparison != 0) {
                    equal = false;
                }
            }

            if (higher) {
                bestSearchResults.clear();
            }
            if (higher || equal) {
                bestSearchResults.add(searchResult);
            }
        }

//        if (bestSearchResults.size() < searchResults.size()) {
        bestSearchResults.forEach(r -> r.setMarked(true));
//        }
    }

//    private List<SearchResultDTO> searchWithoutOntologyLevels(
//            Set<BigInteger> submissionIds, Spectrum querySpectrum, SearchParameters parameters, UserPrincipal user) {
//
//        List<SearchResultDTO> searchResults = new ArrayList<>();
////        for (SpectrumClusterView view : spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
////                null, submissionIds, querySpectrum, parameters)) {
////            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);
////            searchResults.add(searchResult);
////        }
//        int matchIndex = 0;
//        for (SpectrumMatch match
//                : javaSpectrumSimilarityService.searchConsensusAndReference(querySpectrum, parameters, user)) {
//
//            SearchResultDTO searchResult = MappingUtils.mapSpectrumMatchToSpectrumClusterView(
//                    match, matchIndex++, parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
//            searchResults.add(searchResult);
//        }
//
//        return searchResults;
//    }
//
//    private List<SearchResultDTO> searchWithOntologyLevels(Set<BigInteger> submissionIds, SearchParameters parameters,
//                                                           Spectrum spectrum, UserPrincipal user) {
//
//        SearchParameters modifiedParameters;
//        try {
//            modifiedParameters = parameters.clone();
//        } catch (CloneNotSupportedException e) {
//            throw new IllegalStateException(e.getMessage(), e);
//        }
//
//        modifiedParameters.setScoreThreshold(null);
//        modifiedParameters.setPrecursorTolerance(null, null);
//        modifiedParameters.setRetTimeTolerance(null);
//        modifiedParameters.setMassTolerance(null, Parameters.MASS_TOLERANCE_PPM);
//
//        List<Adduct> adducts = adductService.findAdductsByChromatography(spectrum.getChromatographyType());
//        if (spectrum.getMass() == null && adducts != null && spectrum.getPrecursor() != null)
//            modifiedParameters.setMasses(adducts.stream()
//                    .mapToDouble(adduct -> adduct.calculateNeutralMass(spectrum.getPrecursor()))
//                    .toArray());
//
//        List<SpectrumMatch> matches =
//                javaSpectrumSimilarityService.searchConsensusAndReference(spectrum, modifiedParameters, user);
//
//        List<SearchResultDTO> results = IntStream.range(0, matches.size())
//                .mapToObj(i -> MappingUtils.mapSpectrumMatchToSpectrumClusterView(matches.get(i), i,
//                        modifiedParameters.getSpecies(),
//                        modifiedParameters.getSource(),
//                        modifiedParameters.getDisease()))
//                .collect(Collectors.toList());
//
//        List<SearchResultDTO> resultsWithOntology = new ArrayList<>();
//        for (SearchResultDTO result : results) {
//
//            OntologyLevel ontologyLevel = OntologySupplier.select(spectrum.getChromatographyType(),
//                    result.getScore(), result.getPrecursorErrorPPM(), result.getMassErrorPPM(), result.getRetTimeError());
//            if (ontologyLevel == null)
//                continue;
//
//            if (ontologyLevel.getScoreThreshold() == null)
//                result.setScore(null);
//            if (ontologyLevel.getRetTimeTolerance() == null)
//                result.setRetTimeError(null);
//            if (ontologyLevel.getMassTolerancePPM() == null)
//                result.setMassErrorPPM(null);
//
//            result.setOntologyLevel(ontologyLevel);
//            resultsWithOntology.add(result);
//        }
//
//        IntStream.range(0, resultsWithOntology.size())
//                .forEach(i -> resultsWithOntology.get(i).setMatchIndex(i));
//
////        for (SearchResultDTO result : massErrorResults) {
////            result.setOntologyLevel(OntologySupplier.select(spectrum.getChromatographyType(), result.getScore(),
////                    result.getPrecursorErrorPPM(), result.getMassErrorPPM(), result.getRetTimeError()));
////        }
//
//
////        OntologyLevel[] ontologyLevels =
////                OntologySupplier.findByChromatographyTypeAndPriority(spectrum.getChromatographyType(), priority);
////        if (ontologyLevels == null)
////            return new ArrayList<>(0);
////
////        List<Adduct> adducts = adductService.findAdductsByChromatography(spectrum.getChromatographyType());
////
////        List<SearchResultDTO> searchResults = new ArrayList<>();
////        for (OntologyLevel ontologyLevel : ontologyLevels) {
////
////            // Check the presence of necessary properties
////            if (ontologyLevel.getMzTolerancePPM() != null && ontologyLevel.getScoreThreshold() != null && spectrum.getPeaks() == null)
////                continue;
////            if (ontologyLevel.getPrecursorTolerancePPM() != null && spectrum.getPrecursor() == null)
////                continue;
////            if (ontologyLevel.getRetTimeTolerance() != null && spectrum.getRetentionTime() == null)
////                continue;
////
////            // Modify search parameters
////            SearchParameters modifiedParameters;
////            try {
////                modifiedParameters = parameters.clone();
////            } catch (CloneNotSupportedException e) {
////                throw new IllegalStateException(e.getMessage(), e);
////            }
////            modifiedParameters.setMzTolerance(null, ontologyLevel.getMzTolerancePPM());
////            modifiedParameters.setScoreThreshold(ontologyLevel.getScoreThreshold());
////            modifiedParameters.setPrecursorTolerance(null, ontologyLevel.getPrecursorTolerancePPM());
////            modifiedParameters.setMassTolerance(null, ontologyLevel.getMassTolerancePPM());
////            modifiedParameters.setRetTimeTolerance(ontologyLevel.getRetTimeTolerance());
////            if (spectrum.getMass() == null && adducts != null && spectrum.getPrecursor() != null)
////                modifiedParameters.setMasses(adducts.stream()
////                        .mapToDouble(adduct -> adduct.calculateNeutralMass(spectrum.getPrecursor()))
////                        .toArray());
////
////            if (ontologyLevel.getMassTolerancePPM() != null && spectrum.getMass() == null
////                    && modifiedParameters.getMasses() == null)
////                continue;
////
////            // Perform search
//////            List<SearchResultDTO> results = MappingUtils.toList(
//////                    spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
//////                            null, submissionIds, spectrum, modifiedParameters))
//////                    .stream()
//////                    .map(x -> new SearchResultDTO(spectrum, x))
//////                    .collect(Collectors.toList());
////
////            List<SearchResultDTO> results =
////                    javaSpectrumSimilarityService.searchConsensusAndReference(spectrum, modifiedParameters, user)
////                            .stream()
////                            .map(match -> MappingUtils.mapSpectrumMatchToSpectrumClusterView(match,
////                                    modifiedParameters.getSpecies(),
////                                    modifiedParameters.getSource(),
////                                    modifiedParameters.getDisease()))
////                            .collect(Collectors.toList());
////
////            results.forEach(r -> r.setOntologyLevel(ontologyLevel));
////
////            searchResults.addAll(results);
//
//        return resultsWithOntology;
//    }
}
