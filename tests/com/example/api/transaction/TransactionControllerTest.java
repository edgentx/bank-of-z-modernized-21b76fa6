package com.example.api.transaction;

import com.example.api.GlobalExceptionHandler;
import com.example.application.AggregateNotFoundException;
import com.example.application.transaction.TransactionAppService;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private TransactionAppService service;

  private TransactionAggregate deposited() {
    TransactionAggregate agg = new TransactionAggregate("tx-1");
    agg.execute(new PostDepositCmd("tx-1", "a-1", new BigDecimal("100.00"), "USD"));
    return agg;
  }

  @Test
  void postDeposit_returns201() throws Exception {
    when(service.postDeposit(any(PostDepositCmd.class))).thenReturn(deposited());

    var body = Map.of(
        "transactionId", "tx-1",
        "accountId", "a-1",
        "amount", "100.00",
        "currency", "USD");

    mockMvc.perform(post("/api/transactions/deposits")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.transactionId").value("tx-1"))
        .andExpect(jsonPath("$.kind").value("deposit"));
  }

  @Test
  void postDeposit_returns400_whenCurrencyInvalid() throws Exception {
    var body = Map.of(
        "transactionId", "tx-1",
        "accountId", "a-1",
        "amount", "100",
        "currency", "USDX");

    mockMvc.perform(post("/api/transactions/deposits")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void postWithdrawal_returns201() throws Exception {
    TransactionAggregate agg = new TransactionAggregate("tx-2");
    agg.execute(new PostWithdrawalCmd("tx-2", "a-1", new BigDecimal("50.00"), "USD"));
    when(service.postWithdrawal(any(PostWithdrawalCmd.class))).thenReturn(agg);

    var body = Map.of(
        "transactionId", "tx-2",
        "accountId", "a-1",
        "amount", "50.00",
        "currency", "USD");

    mockMvc.perform(post("/api/transactions/withdrawals")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.kind").value("withdrawal"));
  }

  @Test
  void reverse_returns200() throws Exception {
    TransactionAggregate agg = deposited();
    agg.execute(new ReverseTransactionCmd("tx-1", "customer dispute"));
    when(service.reverse(eq("tx-1"), any(ReverseTransactionCmd.class))).thenReturn(agg);

    var body = Map.of("reason", "customer dispute");

    mockMvc.perform(post("/api/transactions/tx-1/reversal")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reversed").value(true));
  }

  @Test
  void find_returns404_whenAbsent() throws Exception {
    when(service.findById("missing"))
        .thenThrow(new AggregateNotFoundException("Transaction", "missing"));

    mockMvc.perform(get("/api/transactions/missing"))
        .andExpect(status().isNotFound());
  }
}
