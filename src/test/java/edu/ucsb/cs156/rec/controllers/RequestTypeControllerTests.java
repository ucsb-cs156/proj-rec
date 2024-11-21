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

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/request-types/all"))
                .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/request-types/all"))
                .andExpect(status().is(200));
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/request-types?id=7"))
                .andExpect(status().is(403)); // logged out users can't get by id
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
        // Arrange
        RequestType requestType = RequestType.builder()
                .requestType("Type A")
                .build();

        when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.of(requestType));

        // Act
        MvcResult response = mockMvc.perform(get("/api/request-types?id=7"))
                .andExpect(status().isOk()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(requestType);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_cannot_get_by_id_when_the_id_does_not_exist() throws Exception {
        // Arrange
        when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // Act
        MvcResult response = mockMvc.perform(get("/api/request-types?id=7"))
                .andExpect(status().isNotFound()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("RequestType with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_request_types() throws Exception {
        // Arrange
        RequestType rt1 = RequestType.builder()
                .requestType("Type A")
                .build();

        RequestType rt2 = RequestType.builder()
                .requestType("Type B")
                .build();

        ArrayList<RequestType> expectedRequestTypes = new ArrayList<>(Arrays.asList(rt1, rt2));

        when(requestTypeRepository.findAll()).thenReturn(expectedRequestTypes);

        // Act
        MvcResult response = mockMvc.perform(get("/api/request-types/all"))
                .andExpect(status().isOk()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedRequestTypes);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_user_can_post_a_new_request_type() throws Exception {
        // Arrange
        RequestType requestType = RequestType.builder()
                .requestType("Type A")
                .build();

        when(requestTypeRepository.save(any(RequestType.class))).thenReturn(requestType);

        // Act
        MvcResult response = mockMvc.perform(
                        post("/api/request-types/post?description=Type A")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).save(any(RequestType.class));
        String expectedJson = mapper.writeValueAsString(requestType);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_can_delete_a_request_type() throws Exception {
        // Arrange
        RequestType requestType = RequestType.builder()
                .requestType("Type A")
                .build();

        when(requestTypeRepository.findById(eq(15L))).thenReturn(Optional.of(requestType));

        // Act
        MvcResult response = mockMvc.perform(
                        delete("/api/request-types?id=15")
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).findById(15L);
        verify(requestTypeRepository, times(1)).delete(any());

        Map<String, Object> json = responseToJson(response);
        assertEquals("Request Type with id 15 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_cannot_delete_non_existent_request_type() throws Exception {
        // Arrange
        when(requestTypeRepository.findById(eq(15L))).thenReturn(Optional.empty());

        // Act
        MvcResult response = mockMvc.perform(
                        delete("/api/request-types?id=15")
                                .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).findById(15L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("RequestType with id 15 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_can_update_existing_request_type() throws Exception {
        // Arrange
        RequestType originalRequestType = RequestType.builder()
                .id(67L)
                .requestType("Type A")
                .build();

        String updatedDescription = "Updated Type";

        RequestType updatedRequestType = RequestType.builder()
                .id(67L)
                .requestType(updatedDescription)
                .build();

        when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(originalRequestType));
        when(requestTypeRepository.save(any(RequestType.class))).thenAnswer(invocation -> {
            RequestType rt = invocation.getArgument(0);
            rt.setId(67L); // Preserve ID
            return rt;
        });

        // Act
        MvcResult response = mockMvc.perform(
                        put("/api/request-types")
                                .param("id", "67")
                                .param("description", updatedDescription)
                                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).findById(67L);
        verify(requestTypeRepository, times(1)).save(any(RequestType.class));

        String responseString = response.getResponse().getContentAsString();
        String expectedJson = mapper.writeValueAsString(updatedRequestType);
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_cannot_post_duplicate_request_type() throws Exception {
        // Arrange
        String description = "Type A";

        RequestType existingRequestType = RequestType.builder()
                .id(1L)
                .requestType(description)
                .build();

        when(requestTypeRepository.findByRequestType(description)).thenReturn(Optional.of(existingRequestType));

        // Act
        MvcResult response = mockMvc.perform(
                        post("/api/request-types/post?description=Type A")
                                .with(csrf()))
                .andExpect(status().isBadRequest()) // Expecting 400 Bad Request
                .andReturn();

        // Assert
        Map<String, Object> json = responseToJson(response);
        assertEquals("IllegalArgumentException", json.get("type"));
        assertEquals("Request Type with this description already exists", json.get("message"));

        verify(requestTypeRepository, times(1)).findByRequestType(description);
        verify(requestTypeRepository, times(0)).save(any(RequestType.class));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_cannot_update_request_type_to_duplicate_description() throws Exception {
        // Arrange
        String originalDescription = "Type A";
        String duplicateDescription = "Type B";

        RequestType originalRequestType = RequestType.builder()
                .id(1L)
                .requestType(originalDescription)
                .build();

        RequestType duplicateRequestType = RequestType.builder()
                .id(2L)
                .requestType(duplicateDescription)
                .build();

        when(requestTypeRepository.findById(eq(1L))).thenReturn(Optional.of(originalRequestType));
        when(requestTypeRepository.findByRequestType(duplicateDescription)).thenReturn(Optional.of(duplicateRequestType));

        // Act
        MvcResult response = mockMvc.perform(
                        put("/api/request-types")
                                .param("id", "1")
                                .param("description", duplicateDescription)
                                .with(csrf()))
                .andExpect(status().isBadRequest()) // Expecting 400 Bad Request
                .andReturn();

        // Assert
        Map<String, Object> json = responseToJson(response);
        assertEquals("IllegalArgumentException", json.get("type"));
        assertEquals("Request Type with this description already exists", json.get("message"));

        verify(requestTypeRepository, times(1)).findById(1L);
        verify(requestTypeRepository, times(1)).findByRequestType(duplicateDescription);
        verify(requestTypeRepository, times(0)).save(any(RequestType.class));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void admin_cannot_update_nonexistent_request_type() throws Exception {
        // Arrange
        when(requestTypeRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act
        MvcResult response = mockMvc.perform(
                        put("/api/request-types")
                                .param("id", "1")
                                .param("description", "New Description")
                                .with(csrf()))
                .andExpect(status().isNotFound()).andReturn();

        // Assert
        verify(requestTypeRepository, times(1)).findById(1L);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("RequestType with id 1 not found", json.get("message"));
    }
}