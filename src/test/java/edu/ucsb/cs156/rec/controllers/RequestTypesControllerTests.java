package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.catalina.connector.Request;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RequestTypesController.class)
@Import(TestConfig.class)
public class RequestTypesControllerTests extends ControllerTestCase {

        @MockBean
        RequestTypeRepository requestTypeRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/phones/admin/all


        // Authorization tests for /api/phones/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttypes/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttypes/post"))
                                .andExpect(status().is(403)); // only admins can post
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_request_type() throws Exception {
                // arrange

                RequestType requestType1 = RequestType.builder()
                                .requestType("Advice")
                                .build();

                when(requestTypeRepository.save(eq(requestType1))).thenReturn(requestType1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/requesttypes/post?requestType=Advice")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).save(requestType1);
                String expectedJson = mapper.writeValueAsString(requestType1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        
}
