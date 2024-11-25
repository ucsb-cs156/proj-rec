package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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

@WebMvcTest(controllers = RequestTypeController.class)
@Import(TestConfig.class)
public class RequestTypeControllerTests extends ControllerTestCase {

        @MockBean
        RequestTypeRepository requestTypeRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/requesttype/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/requesttype/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/requesttype/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/requesttype?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/requesttype/post

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttype/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttype/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // Authorization tests for /api/requesttype/put

        @Test
        public void logged_out_users_cannot_put() throws Exception {
                mockMvc.perform(put("/api/requesttype/put"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_put() throws Exception {
                mockMvc.perform(put("/api/requesttype/put"))
                                .andExpect(status().is(403)); // only admins can edit/put
        }

        // Authorization tests for /api/requesttype/delete
        @Test
        public void logged_out_users_cannot_delete() throws Exception {
                mockMvc.perform(delete("/api/requesttype/delete"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_delete() throws Exception {
                mockMvc.perform(delete("/api/requesttype/delete"))
                                .andExpect(status().is(403)); // only admins can delete
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                RequestType requestType = RequestType.builder()
                                .requestType("Internship")
                                .build();

                when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.of(requestType));

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttype?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(requestType);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttype?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RequestType with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_requesttypes() throws Exception {

                // arrange

                RequestType requestType1 = RequestType.builder()
                                .requestType("Internship")
                                .build();

                RequestType requestType2 = RequestType.builder()
                                .requestType("Research Lab")
                                .build();

                ArrayList<RequestType> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(requestType1, requestType2));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequests);

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttype/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRequests);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

}