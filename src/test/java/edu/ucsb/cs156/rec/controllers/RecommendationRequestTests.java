package edu.ucsb.cs156.rec.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestTests extends ControllerTestCase {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    @WithMockUser(roles = "PROFESSOR")
    public void test_professor_can_get_recommendation_request_by_type() throws Exception {
        // Arrange
        User mockProfessor = User.builder()
                .id(1L)
                .email("professor@ucsb.edu")
                .build();

        RecommendationRequest mockRequest1 = RecommendationRequest.builder()
                .id(101L)
                .professor(mockProfessor)
                .status("completed")
                .details("Details 1")
                .build();

        RecommendationRequest mockRequest2 = RecommendationRequest.builder()
                .id(102L)
                .professor(mockProfessor)
                .status("completed")
                .details("Details 2")
                .build();

        List<RecommendationRequest> mockRequests = List.of(mockRequest1, mockRequest2);

        when(userRepository.findByEmail("professor@ucsb.edu")).thenReturn(java.util.Optional.of(mockProfessor));
        when(recommendationRequestRepository.findAllByProfessorIdAndStatus(1L, "completed"))
                .thenReturn(mockRequests);

        // Act & Assert
        mockMvc.perform(get("/api/recommendationrequest/professor/filtered")
                .param("status", "completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].status").value("completed"))
                .andExpect(jsonPath("$[1].id").value(102))
                .andExpect(jsonPath("$[1].status").value("completed"));

        verify(recommendationRequestRepository, times(1)).findAllByProfessorIdAndStatus(1L, "completed");
    }

    @Test
    @WithMockUser(roles = { "PROFESSOR" })
    public void test_professor_get_recommendation_request_by_status_empty_list() throws Exception {
        // Arrange
        User mockProfessor = User.builder()
                .id(1L)
                .email("professor@ucsb.edu")
                .build();

        List<RecommendationRequest> mockRequests = List.of();

        when(userRepository.findByEmail("professor@ucsb.edu")).thenReturn(java.util.Optional.of(mockProfessor));
        when(recommendationRequestRepository.findAllByProfessorIdAndStatus(1L, "completed"))
                .thenReturn(mockRequests);

        // Act & Assert
        mockMvc.perform(get("/api/recommendationrequest/professor/filtered")
                .param("status", "completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(recommendationRequestRepository, times(1)).findAllByProfessorIdAndStatus(1L, "completed");
    }

    @Test
    @WithMockUser(roles = { "STUDENT" })
    public void test_non_professor_get_recommendation_request_by_status_access_denied() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/recommendationrequest/professor/filtered")
                .param("status", "completed"))
                .andExpect(status().isForbidden());

        verify(recommendationRequestRepository, times(0)).findAllByProfessorIdAndStatus(anyLong(), anyString());
    }
    
}
