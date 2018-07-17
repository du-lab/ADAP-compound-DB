package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ADAPSpringUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserPrincipalService userPrincipalService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserPrincipal user = userPrincipalService.getUerByUsername(username);
		String[] roles1 = {"ADMIN"};
		String[] roles2 = {"STUDENT"};

	    UserBuilder builder = null;
	    if (user != null) {
	    	builder = org.springframework.security.core.userdetails.User.withUsername(username);
			builder.password(user.getHashedPassword());
			if(username.equals("user2")) {
				builder.roles(roles2);
			} else {
				builder.roles(roles1);
			}
	    } else {
	    	throw new UsernameNotFoundException("User not found.");
	    }

	    return builder.build();
	}

	
}
