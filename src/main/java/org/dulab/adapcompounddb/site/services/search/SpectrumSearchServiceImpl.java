package org.dulab.adapcompounddb.site.services.search;

import org.dulab.adapcompounddb.models.entities.Adduct;
import org.dulab.adapcompounddb.models.ontology.OntologyLevel;
import org.dulab.adapcompounddb.models.ontology.OntologySupplier;
import org.dulab.adapcompounddb.site.repositories.AdductRepository;
import org.dulab.adapcompounddb.site.services.AdductService;
import org.dulab.adapcompounddb.site.services.admin.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.SearchResultDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpectrumSearchServiceImpl implements IndividualSearchService {

    private final SpectrumRepository spectrumRepository;
    private final SubmissionRepository submissionRepository;
    private final AdductService adductService;
    private final JavaSpectrumSimilarityService javaSpectrumSimilarityService;

    @Autowired
    public SpectrumSearchServiceImpl(SpectrumRepository spectrumRepository,
                                     SubmissionRepository submissionRepository,
                                     AdductService adductService,
                                     JavaSpectrumSimilarityService javaSpectrumSimilarityService) {

        this.spectrumRepository = spectrumRepository;
        this.submissionRepository = submissionRepository;
        this.adductService = adductService;
        this.javaSpectrumSimilarityService = javaSpectrumSimilarityService;
    }

    @Override
    @Transactional
    public List<SpectrumMatch> search(Spectrum querySpectrum, QueryParameters parameters) {
        return spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum, parameters);
    }

    @Override
    @Transactional
    public List<SearchResultDTO> searchConsensusSpectra(UserPrincipal user, Spectrum querySpectrum,
                                                        SearchParameters parameters, boolean withOntologyLevels) {

        Set<BigInteger> submissionIds = (parameters.getSubmissionIds() != null)
                ? parameters.getSubmissionIds()
                : new HashSet<>();

        if (submissionIds.isEmpty() || submissionIds.contains(BigInteger.ZERO)) {
            Iterable<BigInteger> publicSubmissionIds = submissionRepository.findSubmissionIdsBySubmissionTags(
                    parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            publicSubmissionIds.forEach(submissionIds::add);
            submissionIds.remove(BigInteger.ZERO);
        }

        // Check if there are ontology levels for a given chromatography type
        int[] priorities = OntologySupplier.findPrioritiesByChromatographyType(querySpectrum.getChromatographyType());
        if (priorities == null) {
            // There is no ontology levels. Perform simple search.
            return searchWithoutOntologyLevels(submissionIds, querySpectrum, parameters);
        } else {
            // There are ontology levels. Perform a search for each ontology level until the there any matches
            for (int priority : priorities) {
                List<SearchResultDTO> searchResults =
                        searchWithOntologyLevels(submissionIds, parameters, querySpectrum, priority);
                if (!searchResults.isEmpty())
                    return searchResults;
            }
        }

        return new ArrayList<>(0);
    }

    private List<SearchResultDTO> searchWithoutOntologyLevels(
            Set<BigInteger> submissionIds, Spectrum querySpectrum, SearchParameters parameters) {

        List<SearchResultDTO> searchResults = new ArrayList<>();
//        for (SpectrumClusterView view : spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
//                null, submissionIds, querySpectrum, parameters)) {
//            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);
//            searchResults.add(searchResult);
//        }
        for (SpectrumMatch match : javaSpectrumSimilarityService.searchConsensusAndReference(querySpectrum, parameters)) {
            SpectrumClusterView view = MappingUtils.mapSpectrumMatchToSpectrumClusterView(
                    match, parameters.getSpecies(), parameters.getSource(), parameters.getDisease());
            SearchResultDTO searchResult = new SearchResultDTO(querySpectrum, view);
            searchResults.add(searchResult);
        }

        return searchResults;
    }

    private List<SearchResultDTO> searchWithOntologyLevels(
            Set<BigInteger> submissionIds, SearchParameters parameters, Spectrum spectrum, int priority) {

        OntologyLevel[] ontologyLevels =
                OntologySupplier.findByChromatographyTypeAndPriority(spectrum.getChromatographyType(), priority);
        if (ontologyLevels == null)
            return new ArrayList<>(0);

        List<Adduct> adducts = adductService.findAdductsByChromatography(spectrum.getChromatographyType());

        List<SearchResultDTO> searchResults = new ArrayList<>();
        for (OntologyLevel ontologyLevel : ontologyLevels) {

            // Check the presence of necessary properties
            if (ontologyLevel.getMzTolerance() != null && ontologyLevel.getScoreThreshold() != null && spectrum.getPeaks() == null)
                continue;
            if (ontologyLevel.getPrecursorTolerance() != null && spectrum.getPrecursor() == null)
                continue;
            if (ontologyLevel.getRetTimeTolerance() != null && spectrum.getRetentionTime() == null)
                continue;
//            if (ontologyLevel.getMassTolerancePPM() != null && spectrum.getMolecularWeight() == null)
//                continue;
//            if (ontologyLevel.getMassTolerancePPM() != null && spectrum.getMolecularWeight() == null) {
//                List<Adduct> adducts = adductService.findAdductsByChromatography(spectrum.getChromatographyType());
//                if (adducts != null && spectrum.getPrecursor() != null) {
//                    for (Adduct adduct : adducts) {
//                        spectrum.setMolecularWeight(adduct.calculateNeutralMass(spectrum.getPrecursor()));
//                        searchResults.addAll(searchForOntologyLevel(submissionIds, parameters, spectrum, ontologyLevel));
//                    }
//                    spectrum.setMolecularWeight(null);
//                }
//                continue;
//            }

            // Modify search parameters
            SearchParameters modifiedParameters;
            try {
                modifiedParameters = parameters.clone();
            } catch (CloneNotSupportedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            modifiedParameters.setMzTolerance(ontologyLevel.getMzTolerance());
            modifiedParameters.setScoreThreshold(ontologyLevel.getScoreThreshold());
            modifiedParameters.setPrecursorTolerance(ontologyLevel.getPrecursorTolerance());
            modifiedParameters.setMassTolerance(null);
            modifiedParameters.setMassTolerancePPM(ontologyLevel.getMassTolerancePPM());
            modifiedParameters.setRetTimeTolerance(ontologyLevel.getRetTimeTolerance());
            if (spectrum.getMolecularWeight() == null && adducts != null && spectrum.getPrecursor() != null)
                modifiedParameters.setMasses(adducts.stream()
                        .mapToDouble(adduct -> adduct.calculateNeutralMass(spectrum.getPrecursor()))
                        .toArray());

            // Perform search
            List<SearchResultDTO> results = MappingUtils.toList(
                    spectrumRepository.matchAgainstConsensusAndReferenceSpectra(
                            null, submissionIds, spectrum, modifiedParameters))
                    .stream()
                    .map(x -> new SearchResultDTO(spectrum, x))
                    .collect(Collectors.toList());
            results.forEach(x -> x.setOntologyLevel(ontologyLevel));
//            return results;

            searchResults.addAll(results);
        }

        return searchResults;
    }
}
