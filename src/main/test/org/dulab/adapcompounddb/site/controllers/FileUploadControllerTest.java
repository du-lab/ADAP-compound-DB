package site.controllers;

import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.FileType;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.controllers.AuthenticationController;
import org.dulab.adapcompounddb.site.controllers.FileUploadController;
import org.dulab.adapcompounddb.site.controllers.IndexController;
import org.dulab.adapcompounddb.site.services.FileReaderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadControllerTest extends TestCase {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private Submission submission;

    @Before
    public void SetUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new FileUploadController())
                .setViewResolvers(
                        new ServletContextConfiguration(
                                new LocalValidatorFactoryBean()).viewResolver())
                .build();

        mockHttpSession = new MockHttpSession();
    }

    @Test
    public void FileUploadGetTest() throws Exception {
        System.out.println("jUnit tests");
        mockMvc.perform(get("/file/upload/"))
                .andExpect(status().isOk())  // checks the status
                .andExpect(view().name("file/upload")) // checks the view name
                .andExpect(forwardedUrl("/WEB-INF/jsp/view/file/upload.jsp"));

    }

    @Test
    public void FileUploadRedirectTest() throws Exception{
        Submission.assign(mockHttpSession, submission);
        mockMvc.perform(get("/file/upload/").session(mockHttpSession))
                .andExpect(status().is3xxRedirection()) // checks the status
                .andExpect(redirectedUrl("/file/?chromatographyTypeList=GAS&chromatographyTypeList=LIQUID_POSITIVE&chromatographyTypeList=LIQUID_NEGATIVE&fileTypeList=MSP"));  // check the redirect url

        //Check that the redirected url is handled
        //mockMvc.perform(get("/file/?chromatographyTypeList=GAS&chromatographyTypeList=LIQUID_POSITIVE&chromatographyTypeList=LIQUID_NEGATIVE&fileTypeList=MSP"))
          //      .andExpect(status().isOk());  // checks the status

    }
}