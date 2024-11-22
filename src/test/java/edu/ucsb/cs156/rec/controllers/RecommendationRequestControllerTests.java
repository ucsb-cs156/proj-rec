package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.entities.User;

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

import java.time.LocalDate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase{

    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;
    
    //authorization tests for /api/recommendationrequest/admin/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest?id=123"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    // Authorization tests for /api/recommendationrequest/post
    // (Perhaps should also have these for put and delete)

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendationrequest/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendationrequest/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    // // Tests with mocks for database actions

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

            // arrange
            LocalDate ld = LocalDate.parse("2022-01-03");

            RecommendationRequest recReq = RecommendationRequest.builder()
                            .requesterName("student")
                            .professorEmail("email")
                            .professorName("prof")
                            .recommendationTypes("type")
                            .details("")
                            .submissionDate(ld)
                            .completionDate(ld)
                            .status("pending")
                            .build();

            when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(recReq));

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=1"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findById(eq(1L));
            String expectedJson = mapper.writeValueAsString(recReq);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(recommendationRequestRepository.findById(eq(123L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=123"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findById(eq(123L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("RecommendationRequest with id 123 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void logged_in_user_can_get_all_recommendation_requests() throws Exception {

            // arrange
            LocalDate ld1 = LocalDate.parse("2022-01-03");

            RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(ld1)
                        .status("pending")
                        .build();

            LocalDate ld2 = LocalDate.parse("2022-03-11");

            RecommendationRequest recReq2 = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(ld2)
                        .status("pending")
                        .build();

            ArrayList<RecommendationRequest> expectedRecReqs = new ArrayList<>();
            expectedRecReqs.addAll(Arrays.asList(recReq1, recReq2));

            when(recommendationRequestRepository.findAll()).thenReturn(expectedRecReqs);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedRecReqs);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    @Test
    public void an_admin_user_can_post_a_new_recommendation_request() throws Exception {
        // arrange

        LocalDate ld1 = LocalDate.parse("2022-01-03");

        RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .requesterName("student")
                .professorEmail("email")
                .professorName("prof")
                .recommendationTypes("type")
                .details("")
                .submissionDate(ld1)
                .completionDate(ld1)
                .status("pending")
                .build();

        User mockUser = User.builder().fullName("prof").build();
        when(userRepository.findByFullName("prof")).thenReturn(Optional.of(mockUser));
        when(recommendationRequestRepository.save(eq(recReq1))).thenReturn(recReq1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/recommendationrequest/post?requesterName=student&professorEmail=email&professorName=prof&recommendationTypes=type&details=&submissionDate=2022-01-03&completionDate=2022-01-03")
                                        .with(csrf()))
                        .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).save(recReq1);
        String expectedJson = mapper.writeValueAsString(recReq1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    @Test
    public void an_admin_user_cant_post_a_new_recommendation_request_with_non_existent_professor() throws Exception {
        // arrange

        LocalDate ld1 = LocalDate.parse("2022-01-03");

        RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .requesterName("student")
                .professorEmail("email")
                .professorName("prof")
                .recommendationTypes("type")
                .details("")
                .submissionDate(ld1)
                .completionDate(ld1)
                .status("pending")
                .build();

        when(userRepository.findByFullName("prof")).thenReturn(Optional.empty());
        when(recommendationRequestRepository.save(eq(recReq1))).thenReturn(recReq1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/recommendationrequest/post?requesterName=student&professorEmail=email&professorName=prof&recommendationTypes=type&details=&submissionDate=2022-01-03&completionDate=2022-01-03")
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(userRepository, times(1)).findByFullName("prof");
        verify(recommendationRequestRepository, times(0)).save(recReq1);

        String responseString = response.getResponse().getContentAsString();
        assertTrue(responseString.contains("EntityNotFoundException"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_recommendationrequest() throws Exception {
            // arrange

            LocalDate ld1 = LocalDate.parse("2022-01-03");
            LocalDate ld2 = LocalDate.parse("2023-01-04");

            RecommendationRequest recReqOrig = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(ld1)
                        .status("pending")
                        .build();

            RecommendationRequest recReqEdited = RecommendationRequest.builder()
                        .requesterName("edit")
                        .professorEmail("edit")
                        .professorName("edit")
                        .recommendationTypes("edit")
                        .details("edit")
                        .submissionDate(ld2)
                        .completionDate(ld2)
                        .status("completed")
                        .build();
            String requestBody = mapper.writeValueAsString(recReqEdited);

            when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(recReqOrig));

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/recommendationrequest?id=1")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(1L);
            verify(recommendationRequestRepository, times(1)).save(recReqEdited); // should be saved with correct user
            String responseString = response.getResponse().getContentAsString();
            assertEquals(requestBody, responseString);
    }
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_recommendationrequest_that_does_not_exist() throws Exception {
            // arrange

            LocalDate ld1 = LocalDate.parse("2022-01-03");

            RecommendationRequest recReqEdited = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(ld1)
                        .status("pending")
                        .build();

            String requestBody = mapper.writeValueAsString(recReqEdited);

            when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/recommendationrequest?id=67")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(67L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 67 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_recommendation_request() throws Exception {
            // arrange

            LocalDate ld1 = LocalDate.parse("2022-01-03");

            RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(ld1)
                        .status("pending")
                        .build();

            when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(recommendationRequest));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/recommendationrequest?id=1")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(eq(1L));
            verify(recommendationRequestRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 1 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_recommendation_request_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/recommendationrequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(15L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
    }
    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void logged_in_user_can_get_all_recommendation_requests_by_professor_name() throws Exception {

            // arrange
            LocalDate ld1 = LocalDate.parse("2022-01-03");

            RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof1")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(ld1)
                        .status("pending")
                        .build();

            LocalDate ld2 = LocalDate.parse("2022-03-11");

            RecommendationRequest recReq2 = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof1")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(ld2)
                        .status("pending")
                        .build();

            RecommendationRequest recReq3 = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof3")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(ld2)
                        .status("pending")
                        .build();

            Iterable<RecommendationRequest> expectedRecReqs = Arrays.asList(recReq1, recReq2);

            when(recommendationRequestRepository.findAllByProfessorName("prof1")).thenReturn(expectedRecReqs);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/professor/prof1"))
                            .andExpect(status().isOk()).andReturn();

            // assert

        //     verify(recommendationRequestRepository, times(1)).findAll();
            verify(recommendationRequestRepository, times(1)).findAllByProfessorName("prof1");
            String expectedJson = mapper.writeValueAsString(expectedRecReqs);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void logged_in_user_can_get_all_recommendation_requests_by_student_name() throws Exception {

            // arrange
            LocalDate ld1 = LocalDate.parse("2022-01-03");

            RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(ld1)
                        .status("pending")
                        .build();

            LocalDate ld2 = LocalDate.parse("2022-03-11");

            RecommendationRequest recReq2 = RecommendationRequest.builder()
                        .requesterName("student")
                        .professorEmail("email")
                        .professorName("prof")
                        .recommendationTypes("type")
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(ld2)
                        .status("pending")
                        .build();

            Iterable<RecommendationRequest> expectedRecReqs = Arrays.asList(recReq1, recReq2);

            when(recommendationRequestRepository.findAllByRequesterName("student")).thenReturn(expectedRecReqs);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/requester/student"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findAllByRequesterName("student");
            String expectedJson = mapper.writeValueAsString(expectedRecReqs);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
}
