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
import com.google.gson.Gson;


@Service
public class SearchParametersServiceImpl implements SearchParametersService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchParametersServiceImpl.class);

    Gson gson = new Gson();

    @Autowired
    private SearchParameterRepository searchParameterRepository;

    @PersistenceContext()
    private EntityManager entityManager;

    @Transactional
    public SearchParametersDTO updateUserSearchParameters(final SearchParametersDTO searchParameters, final long userId) {
        SearchParametersDTO fetchedSearchParameters = getUserSearchParameters(userId);
        if (fetchedSearchParameters == null) {
            createDefaultSearchParameters(userId);
        } else {
            updateSearchParameter(gson.toJson(searchParameters), userId);
        }
        return searchParameters;
    }

    private void updateSearchParameter(final String parameterValue, final long userId) {
        try {
            String searchParametersSQL = "UPDATE `searchparameters` SET `value` = '"+
                    parameterValue +"' WHERE (`UserPrimaryId` = "+userId+");";
            entityManager.flush();
            entityManager.clear();
            final Query query = entityManager.createNativeQuery(searchParametersSQL);
            query.executeUpdate();
        } catch (Exception e) {
            Log.error("Error occurred while updating parameter for user : " + userId,e);
        }
    }

    public SearchParametersDTO getUserSearchParameters(final long userId) {
        Gson gson = new Gson();
        return gson.fromJson(searchParameterRepository.findAllByUserId(userId)
                ,SearchParametersDTO.class);
    }

    @Override
    @Transactional
    public void createDefaultSearchParameters(final long userId) {
        try {
            String s = gson.toJson(new SearchParametersDTO());
            String searchParametersSQL = "INSERT INTO `searchparameters` (`name`, `value`, `UserPrimaryId`) VALUES " +
                    "('" + SearchParameterConstants.SEARCH_PARAMETERS + "', '" + s + "', '" + userId + "')";
            entityManager.flush();
            entityManager.clear();
            final Query query = entityManager.createNativeQuery(searchParametersSQL);
            query.executeUpdate();
        } catch (Exception e) {
            Log.error("Error occurred while inserting default search parameters for user : " + userId, e);
        }
    }
}
