package org.dulab.adapcompounddb.site.controllers;

import javafx.beans.binding.When;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.SampleSourceType;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.services.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private AuthenticationService authenticationServiceMock;

    @Mock
    private UserPrincipal userPrincipal;

    @Mock
    private Submission submission;

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthenticationController(authenticationServiceMock), new IndexController())
                .setViewResolvers(
                        new ServletContextConfiguration(
                                new LocalValidatorFactoryBean()).viewResolver())
                .build();

        mockHttpSession = new MockHttpSession();
    }

    /*
       This method tests for Login
       GET method on "/login"
     */
    @Test
    public void loginGetTest() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())  // checks the status
                .andExpect(view().name("login"))  // checks the view name
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/login.jsp"));  // checks the view filename
    }

    /*
       This method tests for the Redirection on Login
       GET method on "/login"
       GET method for redirection on "/"
     */
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

    // This tests checks the POST-method for '/login/'
    @Test
    public void loginPostTest() throws Exception {

        // When login form is successfully submitted, and the credentials are verified the page is redirected to that Home page
        when(authenticationServiceMock.authenticate("username@domain.com","password")).thenReturn(userPrincipal);
        mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "username@domain.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/*"));

        // When there are validation errors, we stay at the same page and display those errors
        mockMvc.perform(
                post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/login.jsp"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(2));
    }

    @Test
    public void signupPostTest() throws Exception {

        // When signup form is successfully submitted, and the credentials are verified the page is redirected to that Home page

        userPrincipal.setUsername("archit");
        userPrincipal.setEmail("aa@bb.com");
        authenticationServiceMock.saveUser(userPrincipal,"Password&1");
        mockMvc.perform(
                post("/signup")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "archit")
                        .param("email","aa@bb.com")
                        .param("confirmedEmail","aa@bb.com")
                        .param("password", "Du&Lab5192")
                        .param("confirmedPassword", "Du&Lab5192"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/*"));

        // When there are validation errors, we stay at the same page and display those errors
        mockMvc.perform(
                post("/signup")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "")
                        .param("email","")
                        .param("confirmedEmail","")
                        .param("password", "password")
                        .param("confirmedPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/signup.jsp"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(5));
    }

    /*
       This method tests for signup
       GET method on "/signup"
     */
    @Test
    public void signupGetTest() throws Exception {

        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())  // checks the status
                .andExpect(view().name("signup"))  // checks the view name
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/signup.jsp"));  // checks the view filename
    }

    /*
       This method tests for the Redirection on Login
       GET method on "/signup"
       GET method for redirection on "/"
     */
    @Test
    public void signupRedirectTest() throws Exception {

        UserPrincipal.assign(mockHttpSession, userPrincipal);
        mockMvc.perform(get("/signup").session(mockHttpSession))
                .andExpect(status().isSeeOther())  // checks the status
                .andExpect(redirectedUrl("/"));  // check the redirect url

        // Check that the redirected url is handled
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());  // checks the status
    }

    /*
       This method tests for the Redirection on Logot
       GET method on "/logout"
       GET method for redirection on "/"
     */
    @Test
    public void getLogoutTest() throws Exception {
        UserPrincipal.assign(mockHttpSession, userPrincipal);
        mockMvc.perform(get("/logout").session(mockHttpSession))
                .andExpect(status().isSeeOther())  // checks the status
                .andExpect(redirectedUrl("/"));  // check the redirect url

        // Check that the redirected url is handled
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());  // checks the status

    }
}