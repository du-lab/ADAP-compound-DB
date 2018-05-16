package org.dulab.adapcompounddb.site.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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