package com.example.api.terminal;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TerminalController.class)
class TerminalControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void getScreenReturnsMainMenuScreenMap() throws Exception {
    mockMvc.perform(get("/api/terminal/screens/MAINMENU"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.screenId").value("MAINMENU"))
        .andExpect(jsonPath("$.rows").value(24))
        .andExpect(jsonPath("$.cols").value(80))
        .andExpect(jsonPath("$.fields[0].protected").value(true));
  }

  @Test
  void getScreenReturns404ForUnknownScreen() throws Exception {
    mockMvc.perform(get("/api/terminal/screens/UNKNOWN"))
        .andExpect(status().isNotFound());
  }

  @Test
  void submitSignonReturnsMainMenu() throws Exception {
    var body = Map.of(
        "screenId", "SIGNON",
        "values", Map.of("userId", "TELLER001", "branch", "NYC-1"));

    mockMvc.perform(post("/api/terminal/screens/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.screenId").value("MAINMENU"));
  }
}
