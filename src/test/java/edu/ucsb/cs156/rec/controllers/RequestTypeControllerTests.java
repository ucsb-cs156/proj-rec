package edu.ucsb.cs156.rec.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

@WebMvcTest(controllers = RequestTypeController.class)
@Import(TestConfig.class)
public class RequestTypeControllerTests extends ControllerTestCase  {
    @MockBean
    RequestTypeRepository requestTypeRepository;

    @Test
    public void logged_out_users_cannot_get_all_request_types() throws Exception {
            mockMvc.perform(get("/api/requesttype/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @Test
    @WithMockUser(roles = { "USER" })
    public void logged_in_user_can_get_all() throws Exception {
            RequestType r = RequestType.builder().id(1L).requestType("Type A").build();
            Iterable<RequestType> rs = Arrays.asList(r);
            when(requestTypeRepository.findAll()).thenReturn(rs);
            MvcResult response = mockMvc.perform(get("/api/requesttype/all"))
                            .andExpect(status().isOk()).andReturn();
            String expectedJson = mapper.writeValueAsString(rs);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
}
