package org.dulab.adapcompounddb.site.controllers;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.AuthenticationController;
import org.dulab.adapcompounddb.site.controllers.IndexController;
import org.dulab.adapcompounddb.site.controllers.SubmissionController;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class SubmissionControllerTest extends TestCase {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private SpectrumService spectrumService;

    @Mock
    private UserPrincipal userPrincipal;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SubmissionController(submissionService, spectrumService), new IndexController())
                .setViewResolvers(
                        new ServletContextConfiguration(
                                new LocalValidatorFactoryBean()).viewResolver())
                .build();

        mockHttpSession = new MockHttpSession();

    }

    @Test
    public void fileViewTest() throws Exception {

        UserPrincipal.assign(mockHttpSession, userPrincipal);
        mockMvc.perform(post("/file/").session(mockHttpSession))
                .andExpect(status().isOk()); // checks the status
                //.andExpect(view().name("file/view"));// checks the view name
                //.andExpect(forwardedUrl("/WEB-INF/jsp/view/login.jsp"));  // checks the view filename

    }

    @Test
    public void viewSubmissionTest() throws Exception{

        //mockMvc.perform(post("/submission/{submissionId:\\d+}").session(mockHttpSession))
        //        .andExpect(status().isOk());
    }
}
