package org.dulab.site.services;

import org.dulab.site.repositories.DefaultUserPrincipalRepository;
import org.dulab.site.repositories.UserPrincipalRepository;
import org.dulab.models.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultUserPrincipalManager implements UserPrincipalManager {

    private UserPrincipalRepository userPrincipalRepository;

    public DefaultUserPrincipalManager() {
        userPrincipalRepository = new DefaultUserPrincipalRepository();
    }

    @Override
    @Transactional("jpaTransactionManager")
    public List<UserPrincipal> getUsers() {
        return toList(userPrincipalRepository.getAll());
    }

    @Override
    @Transactional
    public void saveUser(UserPrincipal user) {
        if (user.getId() < 1)
            userPrincipalRepository.add(user);
        else
            userPrincipalRepository.update(user);
    }

    private <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }


}
