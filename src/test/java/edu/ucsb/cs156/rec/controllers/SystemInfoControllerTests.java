package edu.ucsb.cs156.rec.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.models.SystemInfo;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.services.SystemInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = SystemInfoController.class)
public class SystemInfoControllerTests extends ControllerTestCase {

  @MockBean UserRepository userRepository;

  @MockBean SystemInfoService mockSystemInfoService;

  @Test
  public void systemInfo__admin_logged_in() throws Exception {

    // arrange

    SystemInfo systemInfo =
        SystemInfo.builder()
            .showSwaggerUILink(true)
            .springH2ConsoleEnabled(true)
            .oauthLogin("/oauth2/authorization/google")
            .build();
    when(mockSystemInfoService.getSystemInfo()).thenReturn(systemInfo);
    String expectedJson = mapper.writeValueAsString(systemInfo);

    // act
    MvcResult response =
        mockMvc.perform(get("/api/systemInfo")).andExpect(status().isOk()).andReturn();

    // assert
    String responseString = response.getResponse().getContentAsString();
    assertEquals(expectedJson, responseString);
  }
}
