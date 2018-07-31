package org.dulab.adapcompounddb.site.controllers;

import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private AuthenticationService authenticationServiceMock;

    @Mock
    private SpectrumService spectrumServiceMock;

    @Mock
    private UserPrincipal userPrincipal;

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new AuthenticationController(authenticationServiceMock),
                        new IndexController(spectrumServiceMock))
                .setViewResolvers(
                        new ServletContextConfiguration(
                                new LocalValidatorFactoryBean()).viewResolver())
                .build();

        mockHttpSession = new MockHttpSession();
    }

    @Test
    public void loginGetTest() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())  // checks the status
                .andExpect(view().name("login"))  // checks the view name
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/login.jsp"));  // checks the view filename
    }

    @Test
    public void loginRedirectTest() throws Exception {

        UserPrincipal.assign(mockHttpSession, userPrincipal);
        mockMvc.perform(get("/login").session(mockHttpSession))
                .andExpect(status().isSeeOther())  // checks the status
                .andExpect(redirectedUrl("/"));  // check the redirect url

        // Check that the redirected url is handled
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());  // checks the status
    }
}