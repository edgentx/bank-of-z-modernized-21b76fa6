package com.example.api.legacybridge;

import com.example.api.GlobalExceptionHandler;
import com.example.application.AggregateNotFoundException;
import com.example.application.legacybridge.LegacyBridgeAppService;
import com.example.domain.legacybridge.model.DataSyncCheckpoint;
import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RecordSyncCheckpointCmd;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.VerifyDataParityCmd;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LegacyBridgeController.class)
@Import(GlobalExceptionHandler.class)
class LegacyBridgeControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private LegacyBridgeAppService service;

  @Test
  void evaluateRouting_returns201() throws Exception {
    LegacyTransactionRoute route = new LegacyTransactionRoute("r-1");
    Map<String, Object> payload = new HashMap<>();
    payload.put("forceModern", true);
    route.execute(new EvaluateRoutingCmd("r-1", "deposit", payload, 1));
    when(service.evaluateRouting(any(EvaluateRoutingCmd.class))).thenReturn(route);

    var body = Map.of(
        "routeId", "r-1",
        "transactionType", "deposit",
        "payload", payload,
        "rulesVersion", 1);

    mockMvc.perform(post("/api/legacy-bridge/routes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.routeId").value("r-1"))
        .andExpect(jsonPath("$.evaluated").value(true));
  }

  @Test
  void evaluateRouting_returns400_whenRulesVersionZero() throws Exception {
    var body = Map.of(
        "routeId", "r-1",
        "transactionType", "deposit",
        "payload", Map.of(),
        "rulesVersion", 0);

    mockMvc.perform(post("/api/legacy-bridge/routes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateRoutingRule_returns200() throws Exception {
    LegacyTransactionRoute route = new LegacyTransactionRoute("r-1");
    when(service.updateRoutingRule(eq("r-1"), any(UpdateRoutingRuleCmd.class))).thenReturn(route);

    var body = Map.of(
        "ruleId", "rule-1",
        "newTarget", "MODERN",
        "effectiveDate", Instant.now().toString(),
        "rulesVersion", 2);

    mockMvc.perform(put("/api/legacy-bridge/routes/r-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk());
  }

  @Test
  void recordCheckpoint_returns201() throws Exception {
    DataSyncCheckpoint checkpoint = new DataSyncCheckpoint("cp-1");
    checkpoint.execute(new RecordSyncCheckpointCmd("cp-1", 10L, "hash-1"));
    when(service.recordCheckpoint(any(RecordSyncCheckpointCmd.class))).thenReturn(checkpoint);

    var body = Map.of(
        "checkpointId", "cp-1",
        "syncOffset", 10L,
        "validationHash", "hash-1");

    mockMvc.perform(post("/api/legacy-bridge/checkpoints")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.checkpointId").value("cp-1"))
        .andExpect(jsonPath("$.currentOffset").value(10));
  }

  @Test
  void verifyParity_returns200() throws Exception {
    DataSyncCheckpoint checkpoint = new DataSyncCheckpoint("cp-1");
    checkpoint.execute(new VerifyDataParityCmd("cp-1", "transaction", 5L, "2024-01", "hash-1"));
    when(service.verifyDataParity(any(VerifyDataParityCmd.class))).thenReturn(checkpoint);

    var body = Map.of(
        "checkpointId", "cp-1",
        "entityType", "transaction",
        "syncOffset", 5L,
        "dateRange", "2024-01",
        "validationHash", "hash-1");

    mockMvc.perform(post("/api/legacy-bridge/checkpoints/parity")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.checkpointId").value("cp-1"));
  }

  @Test
  void findCheckpoint_returns404_whenAbsent() throws Exception {
    when(service.findCheckpoint("missing"))
        .thenThrow(new AggregateNotFoundException("DataSyncCheckpoint", "missing"));

    mockMvc.perform(get("/api/legacy-bridge/checkpoints/missing"))
        .andExpect(status().isNotFound());
  }
}
