package org.dulab.site.services;

import org.dulab.models.UserPrincipal;
import org.dulab.site.repositories.UserPrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPrincipalServiceImpl implements UserPrincipalService {

    private final UserPrincipalRepository repository;

    @Autowired
    public UserPrincipalServiceImpl(UserPrincipalRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void saveUserPrincipal(UserPrincipal userPrincipal) {
        repository.save(userPrincipal);
    }
}
