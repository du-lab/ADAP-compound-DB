package org.dulab.adapcompounddb.site.controllers;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.FileType;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

    @Mock
    private MultipartFile file;

    @Before
    public void SetUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FileUploadController(),
                        new SubmissionController(submissionService, spectrumService))
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
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/upload.jsp"))
                .andExpect(model().attributeExists("fileUploadForm"));

    }

    // This tests checks the POST-method for '/login/'
    @Test
    public void loginPostTest() throws Exception {

        byte[] mspBytes = IOUtils.toByteArray(getClass().getResourceAsStream("msp.txt"));

        // Correct file upload with a msp-file
        mockMvc.perform(
                multipart("/file/upload/")
                        .file(new MockMultipartFile(
                                "file",
                                "filename.msp",
                                "text/plain",
                                mspBytes))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("chromatographyType", ChromatographyType.GAS.name())
                        .param("fileType", FileType.MSP.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/file/*"));


        // Incorrect file upload with non-msp file
        mockMvc.perform(
                multipart("/file/upload/")
                        .file(new MockMultipartFile(
                                "file",
                                "filename.msp",
                                "text/plain",
                                "Non-msp".getBytes()))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("chromatographyType", ChromatographyType.GAS.name())
                        .param("fileType", FileType.MSP.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("file/upload"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/upload.jsp"))
                .andExpect(model().attributeExists("message"));

        // When there are validation errors, we stay at the same page and display those errors
        mockMvc.perform(
                multipart("/file/upload/")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("chromatographyType", "")
                        .param("fileType", "")
                        .param("file", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("file/upload"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/upload.jsp"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(3));
    }

    @Test
    public void fileUploadRedirectTest() throws Exception {
        Submission.assign(mockHttpSession, submission);

        mockMvc.perform(get("/file/upload/").session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // checks the status
                .andExpect(redirectedUrlPattern("/file/*"));  // check the redirect url

        //Check that the redirected url is handled
        mockMvc.perform(get("/file/").session(mockHttpSession))
                .andExpect(status().isOk());  // checks the status

    }
}