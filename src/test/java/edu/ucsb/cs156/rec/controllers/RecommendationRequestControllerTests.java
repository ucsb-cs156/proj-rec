package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.checkerframework.checker.units.qual.m;
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
import java.util.List;

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

    @MockBean
    RequestTypeRepository requestTypeRepository;
    
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
            LocalDate ld = LocalDate.now();

            User mockUser = User.builder().fullName("test").professor(true).build();
            User mockRequester = User.builder().fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
            
            RecommendationRequest recReq = RecommendationRequest.builder()
                            .professor(mockUser)
                            .requester(mockRequester)
                            .recommendationType(requestType)
                            .details("")
                            .submissionDate(ld)
                            .completionDate(null)
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
            LocalDate ld1 = LocalDate.now();
            User mockUser = User.builder().fullName("test").professor(true).build();
            User mockRequester = User.builder().fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
            RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(null)
                        .status("pending")
                        .build();

            LocalDate ld2 = LocalDate.now();

            RecommendationRequest recReq2 = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(null)
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

    @WithMockUser(roles = { "STUDENT" })
    @Test
    public void a_student_user_can_post_a_new_recommendation_request() throws Exception {
        // arrange

        LocalDate ld = LocalDate.now();

        User mockUser = User.builder().email("email.com").fullName("prof").professor(true).build();
        RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
        RecommendationRequest recReq1 = RecommendationRequest.builder()
                .professor(mockUser)
                .requester(currentUserService.getCurrentUser().getUser())
                .recommendationType(requestType)
                .details("")
                .submissionDate(ld)
                .completionDate(null)
                .status("pending")
                .build();

        when(userRepository.findByEmail("email.com")).thenReturn(Optional.of(mockUser));
        when(requestTypeRepository.findByRequestType("type")).thenReturn(Optional.of(requestType));
        when(recommendationRequestRepository.save(eq(recReq1))).thenReturn(recReq1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/recommendationrequest/post?professorEmail=email.com&recommendationType=type&details=")
                                        .with(csrf()))
                        .andExpect(status().isOk()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(1)).save(recReq1);
        String expectedJson = mapper.writeValueAsString(recReq1);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
    @WithMockUser(roles = { "STUDENT" })
    @Test
    public void a_student_user_cant_post_a_new_recommendation_request_with_bad_request_type() throws Exception {
        // arrange

        LocalDate ld = LocalDate.now();

        User mockUser = User.builder().email("email.com").fullName("prof").professor(true).build();
        RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
        RecommendationRequest recReq1 = RecommendationRequest.builder()
                .professor(mockUser)
                .requester(currentUserService.getCurrentUser().getUser())
                .recommendationType(requestType)
                .details("")
                .submissionDate(ld)
                .completionDate(null)
                .status("pending")
                .build();

        when(userRepository.findByEmail("email.com")).thenReturn(Optional.of(mockUser));
        when(requestTypeRepository.findByRequestType("type")).thenReturn(Optional.empty());
        when(recommendationRequestRepository.save(eq(recReq1))).thenReturn(recReq1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/recommendationrequest/post?professorEmail=email.com&recommendationType=type&details=")
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(recommendationRequestRepository, times(0)).save(recReq1);

        String responseString = response.getResponse().getContentAsString();
        assertTrue(responseString.contains("EntityNotFoundException"));
    }

    @WithMockUser(roles = { "STUDENT" })
    @Test
    public void a_student_user_cant_post_a_new_recommendation_request_with_non_existent_professor_user() throws Exception {
        // arrange

        LocalDate ld = LocalDate.now();
        User mockUser = User.builder().email("email.com").fullName("test").professor(false).build();
        RequestType requestType = RequestType.builder().id(1L).requestType("type").build();

        RecommendationRequest recReq1 = RecommendationRequest.builder()
                .professor(mockUser)
                .requester(currentUserService.getCurrentUser().getUser())
                .recommendationType(requestType)
                .details("")
                .submissionDate(ld)
                .completionDate(null)
                .status("pending")
                .build();

        when(userRepository.findByEmail("email.com")).thenReturn(Optional.empty());
        when(recommendationRequestRepository.save(eq(recReq1))).thenReturn(recReq1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/recommendationrequest/post?professorEmail=email.com&recommendationType=type&details=")
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(userRepository, times(1)).findByEmail("email.com");
        verify(recommendationRequestRepository, times(0)).save(recReq1);

        String responseString = response.getResponse().getContentAsString();
        assertTrue(responseString.contains("EntityNotFoundException"));
    }

    @WithMockUser(roles = { "STUDENT" })
    @Test
    public void a_student_user_cant_post_a_new_recommendation_request_with_user_whos_not_a_professor() throws Exception {
        // arrange

        LocalDate ld = LocalDate.now();
        User mockUser1 = User.builder().email("email.com").fullName("test").professor(false).build();
        RequestType requestType = RequestType.builder().id(1L).requestType("type").build();

        RecommendationRequest recReq1 = RecommendationRequest.builder()
                .professor(mockUser1)
                .requester(currentUserService.getCurrentUser().getUser())
                .recommendationType(requestType)
                .details("")
                .submissionDate(ld)
                .completionDate(null)
                .status("pending")
                .build();

        User mockUser = User.builder().fullName("prof").professor(false).build();
        when(userRepository.findByEmail("email.com")).thenReturn(Optional.of(mockUser));
        when(recommendationRequestRepository.save(eq(recReq1))).thenReturn(recReq1);

        // act
        MvcResult response = mockMvc.perform(
                        post("/api/recommendationrequest/post?professorEmail=email.com&recommendationType=type&details=")
                                        .with(csrf()))
                        .andExpect(status().isNotFound()).andReturn();

        // assert
        verify(userRepository, times(1)).findByEmail("email.com");
        verify(recommendationRequestRepository, times(0)).save(recReq1);

        String responseString = response.getResponse().getContentAsString();
        assertTrue(responseString.contains("EntityNotFoundException"));
    }

    @WithMockUser(roles = { "ADMIN", "USER", "PROFESSOR" })
    @Test
    public void admin_can_edit_an_existing_recommendationrequest() throws Exception {
            // arrange

            LocalDate ld1 = LocalDate.parse("2022-01-03");
            LocalDate ld2 = LocalDate.now();
            User mockUser = User.builder().fullName("test").professor(true).build();
            User mockRequester = User.builder().fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
            RequestType requestType2 = RequestType.builder().id(2L).requestType("type2").build();

            RecommendationRequest recReqOrig = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(null)
                        .status("pending")
                        .build();

            RecommendationRequest recReqEdited = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType2)
                        .details("edit")
                        .submissionDate(ld1)
                        .completionDate(ld2)
                        .status("accepted")
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
            User mockUser = User.builder().fullName("test").professor(true).build();
            User mockRequester = User.builder().fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();

            RecommendationRequest recReqEdited = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(null)
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

    @WithMockUser(roles = { "USER" })
    @Test
    public void user_can_delete_a_recommendation_request() throws Exception {
            // arrange

            LocalDate ld1 = LocalDate.parse("2022-01-03");
            User mockUser = User.builder().fullName("test").professor(true).build();
            // User mockRequester = User.builder().fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
            RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                        .professor(mockUser)                        
                        .requester(currentUserService.getCurrentUser().getUser())
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(null)
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
    @WithMockUser(roles = { "USER" })
    @Test
    public void user_cant_delete_a_recommendation_request_they_didnt_author() throws Exception {
            // arrange

            LocalDate ld1 = LocalDate.parse("2022-01-03");
            User mockUser = User.builder().fullName("test").professor(true).build();
            User mockRequester = User.builder().fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
            RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                        .professor(mockUser)                        
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(null)
                        .status("pending")
                        .build();

            when(recommendationRequestRepository.findById(eq(1L))).thenReturn(Optional.of(recommendationRequest));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/recommendationrequest?id=1")
                                            .with(csrf()))
                            .andExpect(status().isForbidden()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(eq(1L));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void user_tries_to_delete_non_existant_recommendation_request_and_gets_right_error_message()
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
    @WithMockUser(roles = { "ADMIN", "PROFESSOR" })
    @Test
    public void logged_in_user_can_get_all_recommendation_requests_by_professor_id() throws Exception {

            // arrange
            User mockUser = User.builder().id(15L).fullName("prof").professor(true).build();
            User mockRequester = User.builder().fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
            LocalDate ld1 = LocalDate.parse("2022-01-03");
            RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(null)
                        .status("pending")
                        .build();

            LocalDate ld2 = LocalDate.parse("2022-03-11");
            User mockRequester2 = User.builder().fullName("student2").student(true).build();

            RecommendationRequest recReq2 = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester2)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(null)
                        .status("pending")
                        .build();

            User mockUser2 = User.builder().id(16L).fullName("prof").professor(true).build();
            RecommendationRequest recReq3 = RecommendationRequest.builder()
                        .professor(mockUser2)
                        .requester(mockRequester2)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(null)
                        .status("pending")
                        .build();

            List<RecommendationRequest> expectedRecReqs = Arrays.asList(recReq1, recReq2);

            when(recommendationRequestRepository.findAllByProfessorId(15L)).thenReturn(expectedRecReqs);
            when(userRepository.existsById(15L)).thenReturn(true);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/professor/15"))
                            .andExpect(status().isOk()).andReturn();

            // assert

        //     verify(recommendationRequestRepository, times(1)).findAll();
            verify(recommendationRequestRepository, times(1)).findAllByProfessorId(15L);
            String expectedJson = mapper.writeValueAsString(expectedRecReqs);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
    @WithMockUser(roles = { "ADMIN", "PROFESSOR" })
    @Test
    public void logged_in_user_cant_get_all_recommendation_requests_by_non_existent_professor_id() throws Exception {

            // arrange
            when(userRepository.existsById(15L)).thenReturn(false);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/professor/15"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

        //     verify(recommendationRequestRepository, times(1)).findAll();
            verify(userRepository, times(1)).existsById(15L);
            String responseString = response.getResponse().getContentAsString();
            assertTrue(responseString.contains("EntityNotFoundException"));
    }
    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    @Test
    public void logged_in_user_can_get_all_recommendation_requests_by_requester_id() throws Exception {

            // arrange
            User mockUser = User.builder().fullName("prof").professor(true).build();
            User mockRequester = User.builder().id(15L).fullName("student").student(true).build();
            RequestType requestType = RequestType.builder().id(1L).requestType("type").build();
            LocalDate ld1 = LocalDate.parse("2022-01-03");
            RecommendationRequest recReq1 = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld1)
                        .completionDate(null)
                        .status("pending")
                        .build();

            LocalDate ld2 = LocalDate.parse("2022-03-11");

            RecommendationRequest recReq2 = RecommendationRequest.builder()
                        .professor(mockUser)
                        .requester(mockRequester)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(null)
                        .status("pending")
                        .build();

            User mockRequester2 = User.builder().id(16L).fullName("student2").student(true).build();
            User mockUser2 = User.builder().fullName("prof").professor(true).build();
            RecommendationRequest recReq3 = RecommendationRequest.builder()
                        .professor(mockUser2)
                        .requester(mockRequester2)
                        .recommendationType(requestType)
                        .details("")
                        .submissionDate(ld2)
                        .completionDate(null)
                        .status("pending")
                        .build();

            List<RecommendationRequest> expectedRecReqs = Arrays.asList(recReq1, recReq2);

            when(recommendationRequestRepository.findAllByRequesterId(15L)).thenReturn(expectedRecReqs);
            when(userRepository.existsById(15L)).thenReturn(true);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/requester/15"))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findAllByRequesterId(15L);
            String expectedJson = mapper.writeValueAsString(expectedRecReqs);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
    @WithMockUser(roles = { "ADMIN", "STUDENT" })
    @Test
    public void logged_in_user_cant_get_all_recommendation_requests_by_non_existent_requester_id() throws Exception {

            // arrange
            when(userRepository.existsById(15L)).thenReturn(false);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/requester/15"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(userRepository, times(1)).existsById(15L);
            String responseString = response.getResponse().getContentAsString();
            assertTrue(responseString.contains("EntityNotFoundException"));
    }

}
