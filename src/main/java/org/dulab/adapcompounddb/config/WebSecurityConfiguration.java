package org.dulab.adapcompounddb.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String ACCESS_DENIED_MESSAGE = "Sorry you do not have access to this page";
    private static final String SESSION_ATTRIBUTE_KEY = "currentUser";

    @Autowired
    DataSource dataSource;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authProvider;
    }

    @Override
    public void configure(final WebSecurity security) {
        // Stop Spring Security from evaluating access to static resources to make it as
        // fast as possible.
        security.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http.csrf().disable();

        // The pages does not require login
        http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll();

        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin/").access("hasRole('ROLE_ADMIN')");
        http.authorizeRequests().antMatchers("/account/").access("isAuthenticated()");
        http.authorizeRequests().antMatchers(HttpMethod.POST, "/file/submit").access("isAuthenticated()");

        // When the user has logged in as XX.
        // But access a page that requires role YY,
        // AccessDeniedException will throw.
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/error?errorMsg=" + ACCESS_DENIED_MESSAGE);

        // Config for Login Form
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/login/")//
                .successHandler((request, response, authentication) -> {
                    if (authentication.isAuthenticated()) {
                        request.getSession().setAttribute(SESSION_ATTRIBUTE_KEY, authentication.getPrincipal());
                        response.sendRedirect(request.getServletContext().getContextPath() + "/");
                    } else {
                        request.getRequestDispatcher("/login?loginFailed=true").forward(request, response);
                    }
                }).failureUrl("/login?loginFailed=true")//
                .usernameParameter("username")//
                .passwordParameter("password")
                // Config for Logout Page
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID").invalidateHttpSession(true).permitAll();
    }

}
