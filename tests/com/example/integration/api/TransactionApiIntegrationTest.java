package com.example.integration.api;

import com.example.integration.BaseApiIntegrationTest;
import com.example.integration.fixtures.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * BANK S-44 — API-level integration test for {@code /api/transactions}.
 */
class TransactionApiIntegrationTest extends BaseApiIntegrationTest {

  @Test
  void postDeposit_returns201_andPersistsTransaction() {
    String accountId = TestFixtures.newId("acct");
    String txId = TestFixtures.newId("tx");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/transactions/deposits"),
        jsonRequest(TestFixtures.postDepositBody(txId, accountId)),
        Map.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody());
    assertEquals(txId, created.getBody().get("transactionId"));
    assertEquals("deposit", created.getBody().get("kind"));

    ResponseEntity<Map> fetched = restTemplate.getForEntity(
        url("/api/transactions/" + txId),
        Map.class);
    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertNotNull(fetched.getBody());
    assertEquals(txId, fetched.getBody().get("transactionId"));
  }

  @Test
  void postWithdrawal_returns201() {
    String accountId = TestFixtures.newId("acct");
    String txId = TestFixtures.newId("tx");

    ResponseEntity<Map> response = restTemplate.postForEntity(
        url("/api/transactions/withdrawals"),
        jsonRequest(TestFixtures.postWithdrawalBody(txId, accountId)),
        Map.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("withdrawal", response.getBody().get("kind"));
  }

  @Test
  void postDeposit_returns400_whenCurrencyInvalid() {
    Map<String, Object> body = Map.of(
        "transactionId", TestFixtures.newId("tx"),
        "accountId", TestFixtures.newId("acct"),
        "amount", "100",
        "currency", "USDX"); // 4 chars — violates @Size(min=3,max=3)

    ResponseEntity<Map> response = restTemplate.postForEntity(
        url("/api/transactions/deposits"),
        jsonRequest(body),
        Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void postDeposit_returns400_whenAmountNegative() {
    Map<String, Object> body = Map.of(
        "transactionId", TestFixtures.newId("tx"),
        "accountId", TestFixtures.newId("acct"),
        "amount", "-50.00",
        "currency", "USD");

    ResponseEntity<Map> response = restTemplate.postForEntity(
        url("/api/transactions/deposits"),
        jsonRequest(body),
        Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void reverseTransaction_returns200() {
    String accountId = TestFixtures.newId("acct");
    String txId = TestFixtures.newId("tx");

    restTemplate.postForEntity(
        url("/api/transactions/deposits"),
        jsonRequest(TestFixtures.postDepositBody(txId, accountId)),
        Map.class);

    ResponseEntity<Map> reversed = restTemplate.postForEntity(
        url("/api/transactions/" + txId + "/reversal"),
        jsonRequest(TestFixtures.reverseTransactionBody()),
        Map.class);

    assertEquals(HttpStatus.OK, reversed.getStatusCode());
    assertNotNull(reversed.getBody());
    assertEquals(Boolean.TRUE, reversed.getBody().get("reversed"));
  }

  @Test
  void fetchUnknownTransaction_returns404() {
    ResponseEntity<Map> response = restTemplate.getForEntity(
        url("/api/transactions/missing-" + System.nanoTime()),
        Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private static HttpEntity<Map<String, Object>> jsonRequest(Map<String, Object> body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }
}
