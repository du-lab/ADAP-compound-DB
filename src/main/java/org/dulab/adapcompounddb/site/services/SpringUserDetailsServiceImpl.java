package org.dulab.adapcompounddb.site.services;

import java.util.List;
import java.util.stream.Collectors;

import org.dulab.adapcompounddb.models.entities.Role;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.models.entities.UserRole;
import org.dulab.adapcompounddb.site.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SpringUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserPrincipalService userPrincipalService;
	@Autowired
	UserRoleRepository userRoleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserPrincipal user = userPrincipalService.getUerByUsername(username);
		List<String> roleNames = userRoleRepository.findUserRoleByUserPrincipal(user)
							.stream()
							.map(UserRole::getRole).collect(Collectors.toList())
							.stream()
							.map(Role::getRoleName).collect(Collectors.toList());

	    UserBuilder builder = null;
	    if (user != null) {
	    	builder = org.springframework.security.core.userdetails.User.withUsername(username);
			builder.password(user.getHashedPassword());
			builder.roles(roleNames.toArray(new String[roleNames.size()]));
	    } else {
	    	throw new UsernameNotFoundException("User not found.");
	    }

	    return builder.build();
	}

}
