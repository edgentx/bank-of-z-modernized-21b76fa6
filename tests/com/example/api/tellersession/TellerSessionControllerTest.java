package com.example.api.tellersession;

import com.example.api.GlobalExceptionHandler;
import com.example.application.AggregateNotFoundException;
import com.example.application.tellersession.TellerSessionAppService;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TellerSessionController.class)
@Import(GlobalExceptionHandler.class)
class TellerSessionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private TellerSessionAppService service;

  private TellerSessionAggregate started(String id) {
    TellerSessionAggregate agg = new TellerSessionAggregate(id);
    agg.execute(new StartSessionCmd("teller-1", "term-1"));
    return agg;
  }

  @Test
  void start_returns201_andStatusActive() throws Exception {
    when(service.startSession(any(StartSessionCmd.class))).thenReturn(started("s-1"));

    var body = Map.of("tellerId", "teller-1", "terminalId", "term-1");

    mockMvc.perform(post("/api/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.sessionId").value("s-1"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void start_returns400_whenTellerIdBlank() throws Exception {
    var body = Map.of("tellerId", "", "terminalId", "term-1");

    mockMvc.perform(post("/api/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void navigate_returns200() throws Exception {
    TellerSessionAggregate agg = started("s-1");
    agg.execute(new NavigateMenuCmd("s-1", "main", "open"));
    when(service.navigateMenu(eq("s-1"), any(NavigateMenuCmd.class))).thenReturn(agg);

    var body = Map.of("menuId", "main", "action", "open");

    mockMvc.perform(post("/api/sessions/s-1/navigate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sessionId").value("s-1"));
  }

  @Test
  void end_returns204() throws Exception {
    TellerSessionAggregate agg = started("s-1");
    agg.execute(new EndSessionCmd("s-1"));
    when(service.endSession(eq("s-1"), any(EndSessionCmd.class))).thenReturn(agg);

    mockMvc.perform(delete("/api/sessions/s-1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void find_returns404_whenAbsent() throws Exception {
    when(service.findById("missing"))
        .thenThrow(new AggregateNotFoundException("TellerSession", "missing"));

    mockMvc.perform(get("/api/sessions/missing"))
        .andExpect(status().isNotFound());
  }
}
