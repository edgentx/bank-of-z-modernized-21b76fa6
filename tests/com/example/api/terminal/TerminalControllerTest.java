package com.example.api.terminal;

import com.example.application.account.AccountAppService;
import com.example.domain.account.model.AccountAggregate;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TerminalController.class)
class TerminalControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private AccountAppService accounts;
  @MockBean private CustomerMongoDataRepository customers;
  @MockBean private TransactionMongoDataRepository transactions;

  private AccountAggregate opened() {
    AccountAggregate aggregate = new AccountAggregate("20123456");
    aggregate.execute(new OpenAccountCmd("20123456", "CUS-1001", "Current", 248350L, "12-34-56"));
    aggregate.execute(new UpdateAccountStatusCmd("20123456", "ACTIVE"));
    return aggregate;
  }

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

  @Test
  void submitMainMenuOptionNavigatesToSelectedScreen() throws Exception {
    AccountAggregate account = opened();
    when(accounts.list(isNull(), isNull(), isNull(), any()))
        .thenReturn(new PageImpl<>(List.of(account), PageRequest.of(0, 6), 1));
    when(customers.findById("CUS-1001"))
        .thenReturn(Optional.of(new CustomerDocument(
            "CUS-1001", "Pat Morgan", "pat.morgan@example.com", "12-34-56", true, 1)));

    var body = Map.of(
        "screenId", "MAINMENU",
        "values", Map.of("option", "1"));

    mockMvc.perform(post("/api/terminal/screens/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.screenId").value("ACCTLIST"))
        .andExpect(jsonPath("$.fields[2].label").value(containsString("Pat Morgan")));
  }

  @Test
  void submitAccountDetailRendersAccountData() throws Exception {
    when(accounts.findById("20123456")).thenReturn(opened());
    when(customers.findById("CUS-1001"))
        .thenReturn(Optional.of(new CustomerDocument(
            "CUS-1001", "Pat Morgan", "pat.morgan@example.com", "12-34-56", true, 1)));

    var body = Map.of(
        "screenId", "ACCTDET",
        "values", Map.of("accountNumber", "20123456"));

    mockMvc.perform(post("/api/terminal/screens/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.screenId").value("ACCTDET"))
        .andExpect(jsonPath("$.fields[4].label").value(containsString("Pat Morgan")))
        .andExpect(jsonPath("$.fields[8].label").value(containsString("GBP 2,483.50")));
  }

  @Test
  void submitTransactionListRendersRows() throws Exception {
    TransactionDocument transaction = new TransactionDocument();
    transaction.setId("TX-1001-001");
    transaction.setAccountId("20123456");
    transaction.setKind("deposit");
    transaction.setAmount(new BigDecimal("1250.00"));
    transaction.setCurrency("GBP");
    transaction.setPosted(true);

    when(accounts.findById("20123456")).thenReturn(opened());
    when(transactions.findByAccountId(eq("20123456"), any()))
        .thenReturn(new PageImpl<>(List.of(transaction), PageRequest.of(0, 8), 1));

    var body = Map.of(
        "screenId", "TXLIST",
        "values", Map.of("accountNumber", "20123456"));

    mockMvc.perform(post("/api/terminal/screens/submit")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.screenId").value("TXLIST"))
        .andExpect(jsonPath("$.fields[4].label").value(containsString("TX-1001-001")))
        .andExpect(jsonPath("$.fields[4].label").value(containsString("GBP 1,250.00")));
  }
}
