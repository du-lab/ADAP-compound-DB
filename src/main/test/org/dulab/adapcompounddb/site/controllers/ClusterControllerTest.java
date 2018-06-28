package org.dulab.adapcompounddb.site.controllers;

import junit.framework.TestCase;
import org.dulab.adapcompounddb.config.ServletContextConfiguration;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.SpectrumMatchService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
public class ClusterControllerTest extends TestCase {

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @Mock
    private SpectrumMatchService spectrumMatchService;

    @Mock
    private SubmissionService submissionService;

    @Mock
    private SpectrumCluster cluster;

    @Mock
    private Submission submission;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ClusterController(spectrumMatchService, submissionService), new IndexController())
                .setViewResolvers(
                        new ServletContextConfiguration(
                                new LocalValidatorFactoryBean()).viewResolver())
                .build();

        mockHttpSession = new MockHttpSession();

    }

    /*
       This method tests for cluster
       GET method on "/cluster/{id:\\d+}/"
     */
    @Test
    public void clusterTest() throws Exception {

        when(spectrumMatchService.getCluster(1)).thenReturn(cluster);
        mockMvc.perform(get("/cluster/1/")) // checking if there is a cluster
                .andExpect(status().isOk()) // OK status - link
                .andExpect(view().name("cluster/view")) // checking the view
                .andExpect(model().attributeExists("cluster"));  // checks for the attributes
    }


}