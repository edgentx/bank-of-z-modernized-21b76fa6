package com.example.api.account;

import com.example.api.GlobalExceptionHandler;
import com.example.application.AggregateNotFoundException;
import com.example.application.account.AccountAppService;
import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.model.UpdateAccountStatusCmd;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private AccountAppService service;

  private AccountAggregate opened() {
    AccountAggregate agg = new AccountAggregate("a-1");
    agg.execute(new OpenAccountCmd("a-1", "c-1", "SAVINGS", 1000L, "12-34-56"));
    return agg;
  }

  @Test
  void open_returns201() throws Exception {
    when(service.open(any(OpenAccountCmd.class))).thenReturn(opened());

    var body = Map.of(
        "accountId", "a-1",
        "customerId", "c-1",
        "accountType", "SAVINGS",
        "initialDeposit", 1000L,
        "sortCode", "12-34-56");

    mockMvc.perform(post("/api/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accountId").value("a-1"))
        .andExpect(jsonPath("$.opened").value(true));
  }

  @Test
  void open_returns400_whenInitialDepositNegative() throws Exception {
    var body = Map.of(
        "accountId", "a-1",
        "customerId", "c-1",
        "accountType", "SAVINGS",
        "initialDeposit", -1L,
        "sortCode", "12-34-56");

    mockMvc.perform(post("/api/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateStatus_returns200() throws Exception {
    AccountAggregate agg = opened();
    agg.execute(new UpdateAccountStatusCmd("a-1", "FROZEN"));
    when(service.updateStatus(eq("a-1"), any(UpdateAccountStatusCmd.class))).thenReturn(agg);

    var body = Map.of("newStatus", "FROZEN");

    mockMvc.perform(patch("/api/accounts/a-1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("FROZEN"));
  }

  @Test
  void close_returns204() throws Exception {
    AccountAggregate agg = opened();
    agg.execute(new CloseAccountCmd("a-1"));
    when(service.close(eq("a-1"), any(CloseAccountCmd.class))).thenReturn(agg);

    mockMvc.perform(delete("/api/accounts/a-1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void find_returns404_whenAbsent() throws Exception {
    when(service.findById("missing"))
        .thenThrow(new AggregateNotFoundException("Account", "missing"));

    mockMvc.perform(get("/api/accounts/missing"))
        .andExpect(status().isNotFound());
  }
}
