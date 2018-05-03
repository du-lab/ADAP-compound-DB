package org.dulab.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.entities.UserParameter;
import org.dulab.models.UserParameterType;
import org.dulab.models.entities.UserPrincipal;
import org.dulab.site.repositories.UserParameterRepository;
import org.dulab.site.repositories.UserPrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPrincipalServiceImpl implements UserPrincipalService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final UserPrincipalRepository userPrincipalRepository;
    private final UserParameterRepository userParameterRepository;

    @Autowired
    public UserPrincipalServiceImpl(UserPrincipalRepository userPrincipalRepository,
                                    UserParameterRepository userParameterRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        this.userParameterRepository = userParameterRepository;
    }

    @Override
    @Transactional
    public void saveUserPrincipal(UserPrincipal userPrincipal) {
        userPrincipalRepository.save(userPrincipal);
    }

    @Override
    @Transactional
    public UserParameter findParameter(UserPrincipal userPrincipal, String parameter) {
        return userParameterRepository
                .findByUserPrincipalIdAndIdentifier(userPrincipal.getId(), parameter)
                .orElse(userParameterRepository
                        .findByUserPrincipalIdIsNullAndIdentifier(parameter)
                        .orElseThrow(EmptySearchResultException::new));
    }

    @Override
    @Transactional
    public UserParameter findDefaultParameter(String parameter) {
        return userParameterRepository
                .findByUserPrincipalIdIsNullAndIdentifier(parameter)
                .orElseThrow(EmptySearchResultException::new);
    }

    @Override
    @Transactional
    public void saveDefaultParameter(String parameter, UserParameterType type, Object object) {

        if (object == null) {
            LOGGER.warn("Attempt to save null-parameter with identifier " + parameter);
            return;
        }

        UserParameter userParameter = userParameterRepository
                .findByUserPrincipalIdIsNullAndIdentifier(parameter)
                .orElse(new UserParameter());

        if (object.equals(userParameter.getObject())) return;

        userParameter.setUserPrincipalId(null);
        userParameter.setIdentifier(parameter);
        userParameter.setType(type);
        userParameter.setValue(type.toString(object));

        userParameterRepository.save(userParameter);
    }

    @Override
    @Transactional
    public void saveParameter(UserPrincipal userPrincipal, String parameter, UserParameterType type, Object object) {

        if (object == null) {
            LOGGER.warn("Attempt to save null-parameter with identifier " + parameter);
            return;
        }

        UserParameter userParameter = userParameterRepository
                .findByUserPrincipalIdAndIdentifier(userPrincipal.getId(), parameter)
                .orElse(new UserParameter());

        if (object.equals(userParameter.getObject())) return;

        userParameter.setUserPrincipalId(userPrincipal.getId());
        userParameter.setIdentifier(parameter);
        userParameter.setType(type);
        userParameter.setValue(type.toString(object));

        userParameterRepository.save(userParameter);
    }
}
