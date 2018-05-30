package org.dulab.adapcompounddb.site.controllers;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.entities.Submission;
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
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SubmissionControllerTest extends TestCase {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private Submission submission;

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
        Submission.assign(mockHttpSession, submission);

        mockMvc.perform(get("/file/").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("file/view"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/view.jsp"));



    }

    @Test
    public void fileRawViewTest() throws Exception {

        mockMvc.perform(get("/file/fileview/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/file/upload/*"));

        Submission.assign(mockHttpSession, submission);

        when(submission.getFile()).thenReturn(new byte[0]);
        when(submission.getFilename()).thenReturn("filename");
        mockMvc.perform(get("/file/fileview/").session(mockHttpSession))
                 .andExpect(status().isOk());

        when(submissionService.findSubmission(1L)).thenReturn(null);
        mockMvc.perform(get("/submission/1/fileview/").session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/notfound/*"));

        when(submissionService.findSubmission(1L)).thenReturn(submission);
        mockMvc.perform(get("/submission/1/fileview/").session(mockHttpSession))
                .andExpect(status().isOk());

    }

    @Test
    public void fileRawDownloadTest() throws Exception {
        Submission.assign(mockHttpSession, submission);

        mockMvc.perform(get("/file/filedownload/"))
                .andExpect(status().isFound());


    }



}
