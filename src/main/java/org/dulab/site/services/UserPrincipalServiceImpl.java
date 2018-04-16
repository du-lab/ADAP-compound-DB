package org.dulab.site.services;

import org.dulab.models.UserPrincipal;
import org.dulab.site.repositories.UserPrincipalRepository;
import org.dulab.site.repositories.UserPrincipalRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPrincipalServiceImpl implements UserPrincipalService {

    private UserPrincipalRepository repository;

    public UserPrincipalServiceImpl() {
        repository = new UserPrincipalRepositoryImpl();
    }

    @Override
    @Transactional
    public void saveUserPrincipal(UserPrincipal userPrincipal) {
        if (userPrincipal.getId() < 1)
            repository.add(userPrincipal);
        else
            repository.update(userPrincipal);
    }
}
