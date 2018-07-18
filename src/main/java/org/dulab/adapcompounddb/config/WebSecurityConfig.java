package org.dulab.adapcompounddb.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
@EnableWebSecurity
@ComponentScan(
        basePackages = "org.dulab.adapcompounddb.site",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter({Controller.class, ControllerAdvice.class}))
@Transactional
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String ACCESS_DENIED_MESSAGE = "Sorry you do not have access to this page";
    private static final String SESSION_ATTRIBUTE_KEY = "currentUsername";

	@Autowired
	DataSource dataSource;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication().withUser("user2").password("12345").roles("STUDENT");
//        auth.inMemoryAuthentication().withUser("admin1").password("12345").roles("STUDENT, ADMIN");
		auth.authenticationProvider(authProvider());
	}

	@Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authProvider;
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();

        // The pages does not require login
        http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll();

        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin/").access("hasRole('ROLE_ADMIN')");
 
        // When the user has logged in as XX.
        // But access a page that requires role YY,
        // AccessDeniedException will throw.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/error?errorMsg=" + ACCESS_DENIED_MESSAGE);

        // Config for Login Form
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/login/")//
                .successForwardUrl("/admin/")
                .defaultSuccessUrl("/")
//                .successHandler(new AuthenticationSuccessHandler() {
//					
//					@Override
//					public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//							Authentication authentication) throws IOException, ServletException {
//						if(authentication.isAuthenticated()) {
//					        request.getSession().setAttribute(SESSION_ATTRIBUTE_KEY, request.getParameter("username"));
//					        request.getRequestDispatcher("/admin/").forward(request, response);
//						} else {
//					        request.getRequestDispatcher("/login?loginFailed=true").forward(request, response);
//						}
//					}
//				})
                .failureUrl("/login?loginFailed=true")//
                .usernameParameter("username")//
                .passwordParameter("password")
                // Config for Logout Page
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/logoutSuccessful");
	}
	
}
