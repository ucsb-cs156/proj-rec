package edu.ucsb.cs156.rec.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = DummyController.class)
@Import(TestConfig.class)
public class ApiControllerTests extends ControllerTestCase {

  @MockBean UserRepository userRepository;

  @Test
  public void generic_message_test() {
    ApiController apiController = new DummyController();
    Object result = apiController.genericMessage("Hello World");
    Object expected = Map.of("message", "Hello World");
    assertEquals(expected, result);
  }

  @Test
  public void test_that_dummy_controller_returns_String1_when_1_is_passed() throws Exception {

    // act
    MvcResult response =
        mockMvc.perform(get("/dummycontroller?id=1")).andExpect(status().isOk()).andReturn();

    // assert

    assertEquals("String1", response.getResponse().getContentAsString());
  }

  @Test
  public void test_that_dummy_controller_returns_Exception_when_1_is_not_passed() throws Exception {

    // act
    MvcResult response =
        mockMvc.perform(get("/dummycontroller?id=7")).andExpect(status().isNotFound()).andReturn();

    // assert

    Map<String, Object> json = responseToJson(response);
    assertEquals("EntityNotFoundException", json.get("type"));
    assertEquals("String with id 7 not found", json.get("message"));
  }
}
