package org.dulab.adapcompounddb.site.services;

import com.esotericsoftware.minlog.Log;
import org.dulab.adapcompounddb.models.dto.SearchParametersDTO;
import org.dulab.adapcompounddb.models.entities.SearchParameters;
import org.dulab.adapcompounddb.site.repositories.SearchParameterRepository;
import org.dulab.adapcompounddb.utils.SearchParameterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.MzToleranceType;
import org.dulab.adapcompounddb.site.services.search.SearchParameters.RetIndexMatchType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchParametersServiceImpl implements SearchParametersService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchParametersServiceImpl.class);

    @Autowired
    private SearchParameterRepository searchParameterRepository;

    @PersistenceContext()
    private EntityManager entityManager;

    @Transactional
    public SearchParametersDTO updateUserSearchParameters(final SearchParametersDTO searchParameters, final long userId) {
        List<SearchParameters> searchParametersList = searchParameterRepository.findAllByUserId(userId);
        Map<String, String> searchParametersMap = searchParametersList.stream().collect(
                Collectors.toMap(SearchParameters::getName,SearchParameters::getValue));
        SearchParametersDTO savedSearchParameters = buildSearchParametersDTO(searchParametersMap);
        if (!savedSearchParameters.getScoreThreshold().equals(searchParameters.getScoreThreshold())) {
            updateSearchParameter(SearchParameterConstants.scoreThreshold, searchParameters.getScoreThreshold().toString(), userId);
        }
        if (!savedSearchParameters.getLimit().equals(searchParameters.getLimit())) {
            updateSearchParameter(SearchParameterConstants.matchesPerSpectrum, searchParameters.getLimit().toString(), userId);
        }
        if (savedSearchParameters.getRetentionIndexMatch() != searchParameters.getRetentionIndexMatch()) {
            updateSearchParameter(SearchParameterConstants.retentionIndexMatch, searchParameters.getRetentionIndexMatch().toString(), userId);
        }
        if (savedSearchParameters.getMzToleranceType() != searchParameters.getMzToleranceType()) {
            updateSearchParameter(SearchParameterConstants.mzToleranceType, searchParameters.getMzToleranceType().toString(), userId);
        }
        if (!savedSearchParameters.getRetentionIndexTolerance().equals(searchParameters.getRetentionIndexTolerance())) {
            updateSearchParameter(SearchParameterConstants.retentionIndexTolerance, searchParameters.getRetentionIndexTolerance().toString(), userId);
        }
        if (!savedSearchParameters.getMzTolerance().equals(searchParameters.getMzTolerance())) {
            updateSearchParameter(SearchParameterConstants.mzTolerance, searchParameters.getMzTolerance().toString(), userId);
        }
        return searchParameters;
    }

    private void updateSearchParameter(final String parameterName, final String parameterValue, final long userId) {
        try {
            String searchParametersSQL = "UPDATE `searchparameters` SET `value` = '"+
                    parameterValue +"' WHERE (`name` = '"+parameterName+"' and `UserPrimaryId` = "+userId+");";
            entityManager.flush();
            entityManager.clear();
            final Query query = entityManager.createNativeQuery(searchParametersSQL);
            query.executeUpdate();
        } catch (Exception e) {
            Log.error("Error occurred while updating parameter "+parameterName+" for user : " + userId,e);
        }
    }

    public SearchParametersDTO getUserSearchParameters(final long userId) {

        List<SearchParameters> searchParametersList = searchParameterRepository.findAllByUserId(userId);
        Map<String, String> searchParametersMap = searchParametersList.stream().collect(
                Collectors.toMap(SearchParameters::getName,SearchParameters::getValue));
        SearchParametersDTO searchParametersDTO = buildSearchParametersDTO(searchParametersMap);
        System.out.println(searchParametersDTO.toString());
        return searchParametersDTO;
    }

    @Override
    @Transactional
    public void createDefaultSearchParameters(final long userId) {
        try {
            SearchParametersDTO searchParametersDTO = new SearchParametersDTO();
            String searchParametersSQL = "INSERT INTO `searchparameters` (`name`, `value`, `UserPrimaryId`) VALUES " +
                    "('" + SearchParameterConstants.scoreThreshold + "', '" + searchParametersDTO.getScoreThreshold().toString() + "', '" + userId + "')," +
                    "('" + SearchParameterConstants.matchesPerSpectrum + "', '" + searchParametersDTO.getLimit().toString() + "', '" + userId + "')," +
                    "('" + SearchParameterConstants.retentionIndexMatch + "', '" + searchParametersDTO.getRetentionIndexMatch().toString() + "', '" + userId + "')," +
                    "('" + SearchParameterConstants.mzToleranceType + "', '" + searchParametersDTO.getMzToleranceType().toString() + "', '" + userId + "')," +
                    "('" + SearchParameterConstants.retentionIndexTolerance + "', '" + searchParametersDTO.getRetentionIndexTolerance().toString() + "', '" + userId + "')," +
                    "('" + SearchParameterConstants.mzTolerance + "', '" + searchParametersDTO.getMzTolerance().toString() + "', '" + userId + "')";
            entityManager.flush();
            entityManager.clear();
            final Query query = entityManager.createNativeQuery(searchParametersSQL);
            query.executeUpdate();
        } catch (Exception e) {
            Log.error("Error occurred while inserting default search parameters for user : " + userId,e);
        }
    }

    private SearchParametersDTO buildSearchParametersDTO(final Map<String, String> searchParametersMap) {
        SearchParametersDTO searchParametersDTO = new SearchParametersDTO();
        if (searchParametersMap.get(SearchParameterConstants.scoreThreshold) != null) {
            searchParametersDTO.setScoreThreshold(Integer.parseInt(searchParametersMap.get(SearchParameterConstants.scoreThreshold)));
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
