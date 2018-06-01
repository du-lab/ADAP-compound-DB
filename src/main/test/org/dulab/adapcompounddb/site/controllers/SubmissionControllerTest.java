package org.dulab.adapcompounddb.site.controllers;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.SampleSourceType;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.mock;
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
    public void setUp() {
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

        mockMvc.perform(get("/file/")) // checking if there is a submission
                .andExpect(status().is3xxRedirection()) // if not redirect to the same upload page
                .andExpect(redirectedUrlPattern("/file/upload/*")); // checking the redirection here

        Submission.assign(mockHttpSession, submission); //

        SampleSourceType sampleSourceType;
        sampleSourceType = SampleSourceType.PLASMA;

        when(submission.getName()).thenReturn("file");
        when(submission.getDescription()).thenReturn("file Description");
        when(submission.getSampleSourceType()).thenReturn(sampleSourceType);
        when(submission.getCategory()).thenReturn(null);
        mockMvc.perform(get("/file/").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("file/view"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/view.jsp"));

        when(submissionService.findSubmission(1L)).thenReturn(null);
        mockMvc.perform(get("/submission/1/").session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/notfound/*"));

        when(submissionService.findSubmission(1L)).thenReturn(submission);
        mockMvc.perform(get("/submission/1/").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("file/view"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/view.jsp"));

//        mockMvc.perform(get("/WEB-INF/jsp/view/file/view.jsp").session(mockHttpSession))
//                .andExpect(status().isOk());  // checks the status



    }

    @Test
    public void fileRawViewTest() throws Exception {

        mockMvc.perform(get("/file/fileview/")) // checking if there is a submission
                .andExpect(status().is3xxRedirection()) // if not redirect to the same upload page
                .andExpect(redirectedUrlPattern("/file/upload/*")); // if not redirect to the same upload page

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

        mockMvc.perform(get("/file/filedownload/")) // checking if there is a submission
                .andExpect(status().is3xxRedirection()) // if not redirect to the same upload page
                .andExpect(redirectedUrlPattern("/file/upload/*")); // if not redirect to the same upload page

        Submission.assign(mockHttpSession, submission);

        when(submission.getFile()).thenReturn(new byte[0]);
        when(submission.getFilename()).thenReturn("filename");
        mockMvc.perform(get("/file/filedownload/").session(mockHttpSession))
                .andExpect(status().isOk());

        when(submissionService.findSubmission(1L)).thenReturn(null);
        mockMvc.perform(get("/submission/1/filedownload/").session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/notfound/*"));

        when(submissionService.findSubmission(1L)).thenReturn(submission);
        mockMvc.perform(get("/submission/1/filedownload/").session(mockHttpSession))
                .andExpect(status().isOk());

    }

    // This tests checks the POST-method for '/file/'
    @Test
    public void fileViewPostTest() throws Exception {

        when(submission.getId()).thenReturn(1L);
        Submission.assign(mockHttpSession, submission);

        // When a submission is successfully submitted, the page is redirected to that submission's page
        mockMvc.perform(
                post("/file/")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "submission name")
                        .param("description", "submission description")
                        .param("sampleSourceType", SampleSourceType.STD.name())
                        .param("submissionCategoryId", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/submission/1/*"));

        // When there are validation errors, we stay at the same page and display those errors
        mockMvc.perform(
                post("/file/")
                        .session(mockHttpSession)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .param("description", "")
                        .param("sampleSourceType", "") // SampleSourceType.STD.name()
                        .param("submissionCategoryId", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("file/view"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/view.jsp"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(3));
    }

}
