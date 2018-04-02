package org.dulab.site.services;

import org.dulab.site.data.DefaultUserRepository;
import org.dulab.site.data.UserRepository;
import org.dulab.site.models.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultUserManagerService implements UserManagerService {

    private UserRepository userRepository;

    public DefaultUserManagerService() {
        userRepository = new DefaultUserRepository();
    }

    @Override
    @Transactional("jpaTransactionManager")
    public List<User> getUsers() {
        return toList(userRepository.getAll());
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        if (user.getId() < 1)
            userRepository.add(user);
        else
            userRepository.update(user);
    }

    private <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
