package org.dulab.adapcompounddb.site.controllers;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.entities.Spectrum;
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
import java.util.List;

import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
public class SpectrumControllerTest extends TestCase {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private Submission submission;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private SpectrumService spectrumService;

    @Mock
    private Spectrum spectrum;

    @Mock
    private List<Spectrum> spectrumList;

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(new SpectrumController(spectrumService, submissionService), new IndexController())
                .setViewResolvers(
                        new ServletContextConfiguration(
                                new LocalValidatorFactoryBean()).viewResolver())
                .build();

        mockHttpSession = new MockHttpSession();

    }

    /*
       This method tests for Spectrum submissions
       GET method on "/spectrum/{spectrumId:\d+}/"
     */
    @Test
    public void spectrumTest1() throws Exception{
        when(spectrumService.find(1)).thenReturn(null); // spectrum not found
        mockMvc.perform(get("/spectrum/1/")) // checking the Spectrum
                .andExpect(status().is3xxRedirection()) // Redirected  - link
                .andExpect(redirectedUrlPattern("/notfound/*")) //404 NOT FOUND
                .andExpect(model().attributeExists("errorMessage"));

        when(spectrumService.find(1)).thenReturn(spectrum); // Returning a dummy spectrum
        mockMvc.perform(get("/spectrum/1/")) // checking the Spectrum
                .andExpect(status().isOk()) // OK status - link
                .andExpect(view().name("file/spectrum")) // checking the view
                .andExpect(model().attributeExists("spectrum"));
    }

    /*
       This method tests for submissions and its corresponding/existing spectrum
       GET method on "//submission/{submissionId:\\d+}/{spectrumListIndex:\\d+}/"
     */
    @Test
    public void spectrumTest2() throws Exception{

        when(submissionService.findSubmission(1)).thenReturn(null); // Returning an empty spectrum
        mockMvc.perform(get("/submission/1/1/1/")) // checking if there is a submission and its corresponding spectrum
                .andExpect(status().is3xxRedirection()) // REDIRECTION - link
                .andExpect(redirectedUrlPattern("/notfound/*")) //404 NOT FOUND
                .andExpect(model().attributeExists("errorMessage"));

        when(submissionService.findSubmission(1)).thenReturn(submission); // Returning a dummy spectrum
        when(submission.getFiles().get(1).getSpectra()).thenReturn(spectrumList); // Returning a dummy spectrum List

        spectrumList.get(0); //Getting the Spectrum List index

        mockMvc.perform(get("/submission/1/1/")) // checking if there is a submission and its corresponding spectrum
                .andExpect(status().isOk()) // OK status - link
                .andExpect(view().name("file/spectrum")) // checking the view
                .andExpect(model().attributeExists("spectrum"));
    }

    /*
       This method tests for spectrum
       GET method on "/file/{spectrumListIndex:\\d+}/"
     */
    @Test
    public void spectrumTest3() throws Exception{
        mockMvc.perform(get("/file/1/1/").session(mockHttpSession)) // checking without the session
                .andExpect(status().is3xxRedirection()) // redirection - link
                .andExpect(redirectedUrlPattern("/notfound/*")) //404 NOT FOUND
                .andExpect(model().attributeExists("errorMessage"));

        Submission.assign(mockHttpSession, submission);

        when(submission.getFiles().get(1).getSpectra()).thenReturn(spectrumList); // Returning a dummy Spectrum
        spectrumList.get(0);
        mockMvc.perform(get("/file/1/").session(mockHttpSession)) // checking if there is a submission
                .andExpect(status().isOk()) // OK status - link
                .andExpect(view().name("file/spectrum")) // checking the view
                .andExpect(model().attributeExists("spectrum"));
    }

}