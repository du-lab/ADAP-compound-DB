package org.dulab.adapcompounddb.site.services;

import java.util.List;
import java.util.stream.Collectors;

import org.dulab.adapcompounddb.models.UserRole;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SpringUserDetailsServiceImpl implements UserDetailsService {

    private final UserPrincipalService userPrincipalService;

    @Autowired
    public SpringUserDetailsServiceImpl(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPrincipal user = userPrincipalService.findUserByUsername(username);

        UserBuilder builder = null;
        if (user != null) {
            List<String> roleNames = user.getRoles()
                    .stream()
                    .map(UserRole::name).collect(Collectors.toList());
            builder = org.springframework.security.core.userdetails.User.withUsername(username);
            builder.password(user.getHashedPassword());
            builder.roles(roleNames.toArray(new String[roleNames.size()]));
        } else {
            throw new UsernameNotFoundException("User not found.");
        }

        return builder.build();
    }

}
