package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;
import org.dulab.adapcompounddb.models.entities.SearchParameters;
import org.dulab.adapcompounddb.site.repositories.SearchParameterRepository;
import org.dulab.adapcompounddb.utils.SearchParameterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.MzToleranceType;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.RetIndexMatchType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchParametersServiceImpl implements SearchParametersService {

    @Autowired
    private SearchParameterRepository searchParameterRepository;

    public void updateUserSearchParameters(final SearchParametersDTO searchParameters, final long userId) {

        List<SearchParameters> searchParametersList = searchParameterRepository.findAllByUserId(userId);
        Map<String, String> searchParametersMap = searchParametersList.stream().collect(
                        Collectors.toMap(SearchParameters::getName,SearchParameters::getValue));
        System.out.println(searchParameters.toString());
    }

    public SearchParametersDTO getUserSearchParameters(final long userId) {

        List<SearchParameters> searchParametersList = searchParameterRepository.findAllByUserId(userId);
        Map<String, String> searchParametersMap = searchParametersList.stream().collect(
                Collectors.toMap(SearchParameters::getName,SearchParameters::getValue));
        SearchParametersDTO searchParametersDTO = buildSearchParametersDTO(searchParametersMap);
        System.out.println(searchParametersDTO.toString());
        return searchParametersDTO;
    }

    private SearchParametersDTO buildSearchParametersDTO(final Map<String, String> searchParametersMap) {
        SearchParametersDTO searchParametersDTO = new SearchParametersDTO();
        if (searchParametersMap.get(SearchParameterConstants.scoreThreshold) != null) {
            searchParametersDTO.setScoreThreshold(Double.parseDouble(searchParametersMap.get(SearchParameterConstants.scoreThreshold)));
        }
        if (searchParametersMap.get(SearchParameterConstants.mzToleranceType) != null) {
            searchParametersDTO.setMzToleranceType(MzToleranceType.valueOf(searchParametersMap.get(SearchParameterConstants.mzToleranceType)));
        }
        if (searchParametersMap.get(SearchParameterConstants.mzTolerance) != null) {
            searchParametersDTO.setMzTolerance(Double.parseDouble(searchParametersMap.get(SearchParameterConstants.mzTolerance)));
        }
        if (searchParametersMap.get(SearchParameterConstants.retentionIndexTolerance) != null) {
            searchParametersDTO.setRetentionIndexTolerance(Double.parseDouble(searchParametersMap.get(SearchParameterConstants.retentionIndexTolerance)));
        }
        if (searchParametersMap.get(SearchParameterConstants.matchesPerSpectrum) != null) {
            searchParametersDTO.setLimit(Integer.parseInt(searchParametersMap.get(SearchParameterConstants.matchesPerSpectrum)));
        }
        if (searchParametersMap.get(SearchParameterConstants.retentionIndexMatch) != null) {
            searchParametersDTO.setRetentionIndexMatch(RetIndexMatchType.valueOf(searchParametersMap.get(SearchParameterConstants.retentionIndexMatch)));
        }
        return searchParametersDTO;
    }
}
