package edu.ucsb.cs156.rec.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.services.CurrentUserService;
import edu.ucsb.cs156.rec.services.GrantedAuthoritiesService;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@Import(TestConfig.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestTypeControllerIT extends ControllerTestCase{
        @Autowired
        public CurrentUserService currentUserService;

        @Autowired
        public GrantedAuthoritiesService grantedAuthoritiesService;

        @Autowired
        RequestTypeRepository requestTypeRepository;

        @Autowired
        public MockMvc mockMvc;

        @Autowired
        public ObjectMapper mapper;

        @MockBean
        UserRepository userRepository;

        @WithMockUser(roles = { "USER" })
        @Test
        public void values_in_repository_after_startup() throws Exception {
                // arrange
                RequestType requestType1 = RequestType.builder()
                                .id(1)
                                .requestType("CS Department BS/MS program")
                                .build();
                RequestType requestType2 = RequestType.builder()
                                .id(2)
                                .requestType("Scholarship or Fellowship")
                                .build();
                RequestType requestType3 = RequestType.builder()
                                .id(3)
                                .requestType("MS program (other than CS Dept BS/MS)")
                                .build();
                RequestType requestType4 = RequestType.builder()
                                .id(4)
                                .requestType("PhD program")
                                .build();
                RequestType requestType5 = RequestType.builder()
                                .id(5)
                                .requestType("Other")
                                .build();

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestType2, requestType3, requestType4, requestType5));

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert
                String expectedJson = mapper.writeValueAsString(expectedRequests);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
}
