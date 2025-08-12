package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import joptsimple.internal.OptionNameMap;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTest extends ControllerTestCase {
        @MockBean
        RecommendationRequestRepository recommendationRequestRepository;

        @MockBean
        UserRepository userRepository;

        // User can delete their own recommendation request
        @WithMockUser(roles = { "USER" })
        @Test
        public void user_can_delete_their_recommendation_request() throws Exception {

                User user = currentUserService.getCurrentUser().getUser();
                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                // arrange
                RecommendationRequest recReq = RecommendationRequest.builder()
                                .id(15L)
                                .requester(user)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                when(recommendationRequestRepository.findByIdAndRequester(eq(15L), eq(user)))
                                .thenReturn(Optional.of(recReq));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndRequester(15L, user);
                verify(recommendationRequestRepository, times(1)).delete(recReq);

                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 15 deleted", json.get("message"));
        }

        // user attempts to delete a recommendation request that dne
        @WithMockUser(roles = { "USER" })
        @Test
        public void user_tries_to_delete_non_existant_recommendation_request_and_gets_right_error_message()
                        throws Exception {
                // arrange
                User user1 = currentUserService.getCurrentUser().getUser();
                User user2 = User.builder().id(44).build();
                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec1 = RecommendationRequest.builder()
                                .id(15L)
                                .requester(user1)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                when(recommendationRequestRepository.findByIdAndRequester(eq(15L), eq(user2)))
                                .thenReturn(Optional.of(rec1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndRequester(15L, user1);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
        }

        // User can't delete another user's recommendation request
        @WithMockUser(roles = { "USER" })
        @Test
        public void user_can_not_delete_belonging_to_another_user()
                        throws Exception {

                User user1 = currentUserService.getCurrentUser().getUser();
                User user2 = User.builder().id(44).build();
                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec1 = RecommendationRequest.builder()
                                .id(67L)
                                .requester(user2)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(rec1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest?id=67")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndRequester(67L, user1);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 67 not found", json.get("message"));
        }

        // Admin can delete a recommendation request
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_recommendation_request() throws Exception {
                // arrange

                User user2 = User.builder().id(44).build();
                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec1 = RecommendationRequest.builder()
                                .id(67L)
                                .requester(user2)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(rec1));
                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest/admin?id=67")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(67L);
                verify(recommendationRequestRepository, times(1)).delete(any());
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 67 deleted", json.get("message"));

        }

        // Admin can't delete a recommendation request that dne
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_not_delete_recommendation_request_that_does_not_exist() throws Exception {
                // arrange

                when(recommendationRequestRepository.findById(eq(19L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest/admin?id=19")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(19L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 19 not found", json.get("message"));
        }

        // Professor can delete incoming recommendation request
        @WithMockUser(roles = { "PROFESSOR" })
        @Test
        public void professor_can_delete_their_recommendation_request() throws Exception {

                User user = currentUserService.getCurrentUser().getUser();
                User requester = User.builder()
                                .id(22L)
                                .email("studentA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Student A")
                                .givenName("Student")
                                .familyName("A")
                                .emailVerified(true)
                                .build();

                // arrange
                RecommendationRequest recReq = RecommendationRequest.builder()
                                .id(15L)
                                .requester(requester)
                                .professor(user)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                when(recommendationRequestRepository.findByIdAndProfessor(eq(15L), eq(user)))
                                .thenReturn(Optional.of(recReq));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest/professor?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndProfessor(15L, user);
                verify(recommendationRequestRepository, times(1)).delete(recReq);

                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 15 deleted", json.get("message"));
        }

        // Professor attempts to delete a recommendation request that dne
        @WithMockUser(roles = { "PROFESSOR" })
        @Test
        public void professor_tries_to_delete_non_existant_recommendation_request_and_gets_right_error_message()
                        throws Exception {
                // arrange
                User user1 = currentUserService.getCurrentUser().getUser();
                User user2 = User.builder().id(44).build();
                User requester = User.builder()
                                .id(22L)
                                .email("studentA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Student A")
                                .givenName("Student")
                                .familyName("A")
                                .emailVerified(true)
                                .build();

                RecommendationRequest rec1 = RecommendationRequest.builder()
                                .id(15L)
                                .requester(requester)
                                .professor(user1)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                when(recommendationRequestRepository.findByIdAndProfessor(eq(15L), eq(user2)))
                                .thenReturn(Optional.of(rec1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/recommendationrequest/professor?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndProfessor(15L, user1);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
        }

        // User can update their recommendation request
        @WithMockUser(roles = { "USER" })
        @Test
        public void user_logged_in_put_recommendation_request() throws Exception {
                User user1 = currentUserService.getCurrentUser().getUser();

                User prof1 = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec = RecommendationRequest.builder()
                                .id(63L)
                                .requester(user1)
                                .professor(prof1)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                RecommendationRequest rec_updated = RecommendationRequest.builder()
                                .id(63L)
                                .requester(user1)
                                .professor(prof1)
                                .recommendationType("PhDprogram")
                                .details("more details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                RecommendationRequest rec_corrected = RecommendationRequest.builder()
                                .id(63L)
                                .requester(user1)
                                .professor(prof1)
                                .recommendationType("PhDprogram")
                                .details("more details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                String requestBody = mapper.writeValueAsString(rec_updated);
                String expectedReturn = mapper.writeValueAsString(rec_corrected);

                when(recommendationRequestRepository.findByIdAndRequester(eq(63L), eq(user1)))
                                .thenReturn(Optional.of(rec));

                // act
                MvcResult response = mockMvc
                                .perform(put("/api/recommendationrequest?id=63")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndRequester(63L, user1);
                verify(recommendationRequestRepository, times(1))
                                .save(rec_corrected);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedReturn, responseString);
        }

        // User can't edit a recommendation request that dne
        @WithMockUser(roles = { "USER" })
        @Test
        public void user_can_not_put_recommendation_request_that_does_not_exist() throws Exception {

                User user1 = currentUserService.getCurrentUser().getUser();

                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec = RecommendationRequest.builder()
                                .id(67L)
                                .requester(user1)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                String requestBody = mapper.writeValueAsString(rec);
                when(recommendationRequestRepository.findByIdAndRequester(eq(67L), eq(user1)))
                                .thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc
                                .perform(put("/api/recommendationrequest?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound())
                                .andReturn();
                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndRequester(67L, user1);
                Map<String, Object> output = responseToJson(response);
                assertEquals("RecommendationRequest with id 67 not found", output.get("message"));
        }

        // User can't edit a recommendation request for another user
        @WithMockUser(roles = { "USER" })
        @Test
        public void user_can_not_put_recommendation_request_for_another_user() throws Exception {
                User user1 = currentUserService.getCurrentUser().getUser();
                User user2 = User.builder().id(44).build();

                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec = RecommendationRequest.builder()
                                .id(67L)
                                .requester(user2)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();
                RecommendationRequest rec_updated = RecommendationRequest.builder()
                                .id(67L)
                                .requester(user1)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("more details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();
                when(recommendationRequestRepository.findByIdAndRequester(eq(31L), eq(user2)))
                                .thenReturn(Optional.of(rec));

                String requestBody = mapper.writeValueAsString(rec_updated);

                // act
                MvcResult response = mockMvc
                                .perform(put("/api/recommendationrequest?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound())
                                .andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findByIdAndRequester(67, user1);
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RecommendationRequest with id 67 not found", json.get("message"));
        }

        // Prof can edit a Recommendation Request
        @WithMockUser(roles = { "PROFESSOR" })
        @Test
        public void prof_can_put_complete_recommendation_request() throws Exception {
                // arrange
                LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                User student = User.builder().id(99).build();
                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec = RecommendationRequest.builder()
                                .id(67L)
                                .requester(student)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                RecommendationRequest rec_updated = RecommendationRequest.builder()
                                .id(67L)
                                .requester(student)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("COMPLETED")
                                .completionDate(now)
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();
                RecommendationRequest rec_corrected = RecommendationRequest.builder()
                                .id(67L)
                                .requester(student)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("COMPLETED")
                                .completionDate(now)
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();


                String requestBody = mapper.writeValueAsString(rec_updated);
                String expectedJson = mapper.writeValueAsString(rec_corrected);

                when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(rec));

                // act
                MvcResult response = mockMvc
                                .perform(put("/api/recommendationrequest/professor?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                // assert

                verify(recommendationRequestRepository, times(1)).findById(67L);
                verify(recommendationRequestRepository, times(1)).save(rec_corrected);

                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Prof can edit a Recommendation Request
        @WithMockUser(roles = { "PROFESSOR" })
        @Test
        public void prof_can_put_incomplete_recommendation_request() throws Exception {
                // arrange
                User student = User.builder().id(99).build();
                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec = RecommendationRequest.builder()
                                .id(67L)
                                .requester(student)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                RecommendationRequest rec_updated = RecommendationRequest.builder()
                                .id(67L)
                                .requester(student)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("STILL_PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();
                RecommendationRequest rec_corrected = RecommendationRequest.builder()
                                .id(67L)
                                .requester(student)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("STILL_PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                String requestBody = mapper.writeValueAsString(rec_updated);
                String expectedJson = mapper.writeValueAsString(rec_corrected);

                when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(rec));

                // act
                MvcResult response = mockMvc
                                .perform(put("/api/recommendationrequest/professor?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(67L);
                verify(recommendationRequestRepository, times(1)).save(rec_corrected);

                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // prof can not edit a Recommendation Request that dne
        @WithMockUser(roles = { "PROFESSOR" })
        @Test
        public void prof_can_not_put_recommendation_request_that_does_not_exist() throws Exception {
                // arrange
                User user2 = User.builder().id(200).build();

                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec_updated = RecommendationRequest.builder()
                                .id(67L)
                                .requester(user2)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("COMPLETED")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();

                String requestBody = mapper.writeValueAsString(rec_updated);

                when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc
                                .perform(put("/api/recommendationrequest/professor?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound())
                                .andReturn();

                // assert
                verify(recommendationRequestRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RecommendationRequest with id 67 not found", json.get("message"));
        }
        //non admins cannot get all requests
        @WithMockUser(roles = { "USER" })
        @Test
        public void non_admins_cannot_get_all_requests() throws Exception {
                mockMvc.perform(get("/api/recommendationrequest/admin"))
                                .andExpect(status().is(403)); // non-admins can't get all
        }
        //admins can get all requests
        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void admins_can_get_all_requests() throws Exception {
                //arrange
                User student = User.builder().id(99).build();
                User prof = User.builder()
                                .id(22L)
                                .email("profA@ucsb.edu")
                                .googleSub("googleSub")
                                .fullName("Prof A")
                                .givenName("Prof")
                                .familyName("A")
                                .emailVerified(true)
                                .professor(true)
                                .build();

                RecommendationRequest rec = RecommendationRequest.builder()
                                .id(67L)
                                .requester(student)
                                .professor(prof)
                                .recommendationType("PhDprogram")
                                .details("details")
                                .status("PENDING")
                                .completionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .dueDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .submissionDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .lastModifiedDate(LocalDateTime.parse("2022-01-03T00:00:00"))
                                .build();
                List<RecommendationRequest> recs = new ArrayList<>();
                recs.add(rec);
                when(recommendationRequestRepository.findAll()).thenReturn(recs);
                //act
                MvcResult response = mockMvc.perform(get("/api/recommendationrequest/admin"))
                                .andExpect(status().isOk()).andReturn();
                //assert
                verify(recommendationRequestRepository, times(1)).findAll();
                String responseString = response.getResponse().getContentAsString();
                String expectedJson = mapper.writeValueAsString(recs);
                assertEquals(expectedJson, responseString);
        }

}
