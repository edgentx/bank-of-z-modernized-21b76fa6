package com.example.api.customer;

import com.example.api.GlobalExceptionHandler;
import com.example.application.AggregateNotFoundException;
import com.example.application.customer.CustomerAppService;
import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.infrastructure.mongo.account.AccountDocument;
import com.example.infrastructure.mongo.account.AccountMongoDataRepository;
import com.example.infrastructure.mongo.customer.CustomerDocument;
import com.example.infrastructure.mongo.customer.CustomerMongoDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private CustomerAppService service;
  @MockBean private CustomerMongoDataRepository customerData;
  @MockBean private AccountMongoDataRepository accountData;

  private CustomerAggregate enrolled(String id) {
    CustomerAggregate agg = new CustomerAggregate(id);
    agg.execute(new EnrollCustomerCmd(id, "Alice Doe", "alice@example.com", "GOV-123"));
    return agg;
  }

  @Test
  void enroll_returns201_andCustomerJson() throws Exception {
    when(service.enroll(any(EnrollCustomerCmd.class))).thenReturn(enrolled("c-1"));

    var body = Map.of(
        "customerId", "c-1",
        "fullName", "Alice Doe",
        "email", "alice@example.com",
        "governmentId", "GOV-123");

    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customerId").value("c-1"))
        .andExpect(jsonPath("$.email").value("alice@example.com"))
        .andExpect(jsonPath("$.enrolled").value(true));
  }

  @Test
  void enroll_returns400_whenEmailInvalid() throws Exception {
    var body = Map.of(
        "customerId", "c-1",
        "fullName", "Alice",
        "email", "not-an-email",
        "governmentId", "GOV-123");

    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
  }

  @Test
  void enroll_returns400_whenAggregateRejectsCommand() throws Exception {
    when(service.enroll(any(EnrollCustomerCmd.class)))
        .thenThrow(new IllegalStateException("Customer already enrolled: c-1"));

    var body = Map.of(
        "customerId", "c-1",
        "fullName", "Alice",
        "email", "alice@example.com",
        "governmentId", "GOV-123");

    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Customer already enrolled: c-1"));
  }

  @Test
  void updateDetails_returns200_andUpdatedJson() throws Exception {
    CustomerAggregate agg = enrolled("c-1");
    agg.execute(new UpdateCustomerDetailsCmd("c-1", "alice@new.com", "12-34-56"));
    when(service.updateDetails(any(UpdateCustomerDetailsCmd.class))).thenReturn(agg);

    var body = Map.of("emailAddress", "alice@new.com", "sortCode", "12-34-56");

    mockMvc.perform(put("/api/customers/c-1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sortCode").value("12-34-56"));
  }

  @Test
  void delete_returns204() throws Exception {
    doNothing().when(service).delete(any(DeleteCustomerCmd.class));

    mockMvc.perform(delete("/api/customers/c-1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void find_returns404_whenAbsent() throws Exception {
    when(service.findById("missing"))
        .thenThrow(new AggregateNotFoundException("Customer", "missing"));

    mockMvc.perform(get("/api/customers/missing"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  void find_returns200_whenPresent() throws Exception {
    when(service.findById("c-1")).thenReturn(enrolled("c-1"));

    mockMvc.perform(get("/api/customers/c-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value("c-1"));
  }

  @Test
  void searchByName_returnsPagedCustomerSummaries() throws Exception {
    CustomerDocument doc = new CustomerDocument(
        "CUS-1001",
        "Pat Morgan",
        "pat.morgan@example.com",
        "12-34-56",
        true,
        1);
    when(customerData.findByFullNameContainingIgnoreCase("Pat", PageRequest.of(0, 25)))
        .thenReturn(new PageImpl<>(List.of(doc), PageRequest.of(0, 25), 1));

    mockMvc.perform(get("/api/customers")
            .param("name", "Pat")
            .param("page", "0")
            .param("size", "25"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].customerId").value("CUS-1001"))
        .andExpect(jsonPath("$.content[0].fullName").value("Pat Morgan"))
        .andExpect(jsonPath("$.content[0].status").value("ENROLLED"));
  }

  @Test
  void searchByAccountNumber_resolvesCustomerThroughAccount() throws Exception {
    AccountDocument account = new AccountDocument();
    account.setId("20123456");
    account.setCustomerId("CUS-1001");
    CustomerDocument customer = new CustomerDocument(
        "CUS-1001",
        "Pat Morgan",
        "pat.morgan@example.com",
        "12-34-56",
        true,
        1);

    when(accountData.findById("20123456")).thenReturn(Optional.of(account));
    when(customerData.findById("CUS-1001")).thenReturn(Optional.of(customer));

    mockMvc.perform(get("/api/customers")
            .param("accountNumber", "20123456")
            .param("page", "0")
            .param("size", "25"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].customerId").value("CUS-1001"))
        .andExpect(jsonPath("$.content[0].fullName").value("Pat Morgan"));
  }
}
