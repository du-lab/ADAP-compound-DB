package org.dulab.adapcompounddb.site.controllers;

import junit.framework.TestCase;

import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.entities.Submission;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadControllerTest extends TestCase {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private Submission submission;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private SpectrumService spectrumService;

    @Before
    public void SetUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FileUploadController(),
                        new SubmissionController(submissionService,spectrumService))
                .setViewResolvers(
                        new ServletContextConfiguration(
                                new LocalValidatorFactoryBean()).viewResolver())
                .build();

        mockHttpSession = new MockHttpSession();
    }

    @Test
    public void fileUploadGetTest() throws Exception {

        mockMvc.perform(get("/file/upload/"))
                .andExpect(status().isOk())  // checks the status
                .andExpect(view().name("file/upload")) // checks the view name
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/upload.jsp"));

    }

    @Test
    public void fileUploadRedirectTest() throws Exception{
        Submission.assign(mockHttpSession, submission);

        mockMvc.perform(get("/file/upload/").session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // checks the status
                .andExpect(redirectedUrlPattern("/file/*"));  // check the redirect url

        //Check that the redirected url is handled
        mockMvc.perform(get("/file/").session(mockHttpSession))
                .andExpect(status().isOk());  // checks the status

    }
}