package org.dulab.site.services;

import org.dulab.config.ApplicationContextConfiguration;
import org.dulab.config.ServletContextConfiguration;
import org.dulab.models.UserPrincipal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {
//        ApplicationContextConfiguration.class,
//        ServletContextConfiguration.class
//})
//@ActiveProfiles(profiles = "test")
//@WebAppConfiguration
//@Transactional
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationService authenticationService;

    @Test
    public void saveUser() {

        assertNotNull(authenticationService);
    }
}