package com.example.api.dashboard;

import com.example.infrastructure.mongo.account.AccountMongoDataRepository;
import com.example.infrastructure.mongo.customer.CustomerMongoDataRepository;
import com.example.infrastructure.mongo.transaction.TransactionDocument;
import com.example.infrastructure.mongo.transaction.TransactionMongoDataRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private AccountMongoDataRepository accountRepository;
  @MockBean private CustomerMongoDataRepository customerRepository;
  @MockBean private TransactionMongoDataRepository transactionRepository;

  @Test
  void summaryReturnsCountersAndMinorUnitTotals() throws Exception {
    when(accountRepository.countByStatus("ACTIVE")).thenReturn(7L);
    when(accountRepository.countByStatus("CLOSED")).thenReturn(2L);
    when(customerRepository.countByEnrolledTrue()).thenReturn(5L);
    when(transactionRepository.countByPostedFalseAndReversedFalse()).thenReturn(1L);
    when(transactionRepository.countByPostedTrueAndReversedFalse()).thenReturn(4L);
    when(transactionRepository.findByKindAndPostedTrueAndReversedFalse("deposit"))
        .thenReturn(List.of(transaction("10.50", "GBP"), transaction("5.25", "GBP")));
    when(transactionRepository.findByKindAndPostedTrueAndReversedFalse("withdrawal"))
        .thenReturn(List.of(transaction("3.25", "GBP")));

    mockMvc.perform(get("/api/dashboard/summary")
            .header("X-User-Id", "TELLER-9")
            .header("X-Branch-Id", "BR-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.generatedAt").exists())
        .andExpect(jsonPath("$.tellerId").value("TELLER-9"))
        .andExpect(jsonPath("$.branch").value("BR-1"))
        .andExpect(jsonPath("$.openAccountCount").value(7))
        .andExpect(jsonPath("$.closedTodayCount").value(2))
        .andExpect(jsonPath("$.activeCustomerCount").value(5))
        .andExpect(jsonPath("$.pendingTransactionCount").value(1))
        .andExpect(jsonPath("$.postedTransactionCount").value(4))
        .andExpect(jsonPath("$.totalDepositsToday").value(1575))
        .andExpect(jsonPath("$.totalWithdrawalsToday").value(325))
        .andExpect(jsonPath("$.currency").value("GBP"));
  }

  private static TransactionDocument transaction(String amount, String currency) {
    TransactionDocument document = new TransactionDocument();
    document.setAmount(new BigDecimal(amount));
    document.setCurrency(currency);
    document.setPosted(true);
    return document;
  }
}
