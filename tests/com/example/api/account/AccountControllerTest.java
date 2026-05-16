package com.example.api.account;

import com.example.api.GlobalExceptionHandler;
import com.example.application.AggregateNotFoundException;
import com.example.application.account.AccountAppService;
import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatus;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.infrastructure.mongo.customer.CustomerDocument;
import com.example.infrastructure.mongo.customer.CustomerMongoDataRepository;
import com.example.infrastructure.mongo.transaction.TransactionDocument;
import com.example.infrastructure.mongo.transaction.TransactionMongoDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
  @MockBean private CustomerMongoDataRepository customers;
  @MockBean private TransactionMongoDataRepository transactions;

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
  void list_returns200WithEmptyPage() throws Exception {
    when(service.list(isNull(), isNull(), isNull(), any()))
        .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 25), 0));

    mockMvc.perform(get("/api/accounts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content").isEmpty())
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.totalPages").value(0));
  }

  @Test
  void list_honorsFiltersAndPagination() throws Exception {
    AccountAggregate agg = opened();
    agg.execute(new UpdateAccountStatusCmd("a-1", "ACTIVE"));
    when(service.list(eq("a-"), eq("c-1"), eq(AccountStatus.ACTIVE), any()))
        .thenReturn(new PageImpl<>(List.of(agg), PageRequest.of(0, 10), 1));
    when(customers.findById("c-1"))
        .thenReturn(Optional.of(new CustomerDocument("c-1", "Alice Doe", "alice@example.com", "12-34-56", true, 1)));

    mockMvc.perform(get("/api/accounts")
            .param("accountNumber", "a-")
            .param("customerId", "c-1")
            .param("status", "ACTIVE")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].accountId").value("a-1"))
        .andExpect(jsonPath("$.content[0].accountNumber").value("a-1"))
        .andExpect(jsonPath("$.content[0].customerId").value("c-1"))
        .andExpect(jsonPath("$.content[0].customerName").value("Alice Doe"))
        .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
        .andExpect(jsonPath("$.content[0].balance").value(1000))
        .andExpect(jsonPath("$.content[0].currency").value("GBP"))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void find_returns404_whenAbsent() throws Exception {
    when(service.findById("missing"))
        .thenThrow(new AggregateNotFoundException("Account", "missing"));

    mockMvc.perform(get("/api/accounts/missing"))
        .andExpect(status().isNotFound());
  }

  @Test
  void find_returnsFrontendDetailShape() throws Exception {
    AccountAggregate agg = opened();
    agg.execute(new UpdateAccountStatusCmd("a-1", "ACTIVE"));
    when(service.findById("a-1")).thenReturn(agg);
    when(customers.findById("c-1"))
        .thenReturn(Optional.of(new CustomerDocument("c-1", "Alice Doe", "alice@example.com", "12-34-56", true, 1)));

    mockMvc.perform(get("/api/accounts/a-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountId").value("a-1"))
        .andExpect(jsonPath("$.accountNumber").value("a-1"))
        .andExpect(jsonPath("$.customerId").value("c-1"))
        .andExpect(jsonPath("$.customerName").value("Alice Doe"))
        .andExpect(jsonPath("$.balance").value(1000))
        .andExpect(jsonPath("$.availableBalance").value(1000))
        .andExpect(jsonPath("$.overdraftLimit").value(0))
        .andExpect(jsonPath("$.currency").value("GBP"))
        .andExpect(jsonPath("$.branch").value("12-34-56"))
        .andExpect(jsonPath("$.openedAt").isString())
        .andExpect(jsonPath("$.updatedAt").isString());
  }

  @Test
  void transactions_returnsFrontendLedgerShape() throws Exception {
    AccountAggregate agg = opened();
    agg.execute(new UpdateAccountStatusCmd("a-1", "ACTIVE"));
    TransactionDocument transaction = new TransactionDocument();
    transaction.setId("tx-1");
    transaction.setAccountId("a-1");
    transaction.setKind("deposit");
    transaction.setAmount(new BigDecimal("42.50"));
    transaction.setCurrency("gbp");
    transaction.setPosted(true);

    when(service.findById("a-1")).thenReturn(agg);
    when(transactions.findByAccountId(eq("a-1"), any()))
        .thenReturn(new PageImpl<>(List.of(transaction), PageRequest.of(0, 25), 1));

    mockMvc.perform(get("/api/accounts/a-1/transactions")
            .param("page", "0")
            .param("size", "25"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].transactionId").value("tx-1"))
        .andExpect(jsonPath("$.content[0].accountId").value("a-1"))
        .andExpect(jsonPath("$.content[0].postedAt").isString())
        .andExpect(jsonPath("$.content[0].description").value("Deposit"))
        .andExpect(jsonPath("$.content[0].type").value("DEPOSIT"))
        .andExpect(jsonPath("$.content[0].amount").value(4250))
        .andExpect(jsonPath("$.content[0].currency").value("GBP"))
        .andExpect(jsonPath("$.content[0].runningBalance").value(1000));
  }
}
