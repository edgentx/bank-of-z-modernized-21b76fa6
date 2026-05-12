package com.example.integration.api;

import com.example.integration.BaseApiIntegrationTest;
import com.example.integration.fixtures.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * BANK S-44 — API-level integration test for {@code /api/accounts}.
 *
 * Exercises the AccountController end-to-end through MockMvc-less HTTP so
 * the full filter chain (validation, exception handler, JSON serialization,
 * persistence adapter) participates in every assertion.
 */
class AccountApiIntegrationTest extends BaseApiIntegrationTest {

  @Test
  void openAndFetchAccount_persistsThroughMongo() {
    String accountId = TestFixtures.newId("acct");
    String customerId = TestFixtures.newId("cust");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/accounts"),
        jsonRequest(TestFixtures.openAccountBody(accountId, customerId)),
        Map.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody());
    assertEquals(accountId, created.getBody().get("accountId"));
    assertEquals(Boolean.TRUE, created.getBody().get("opened"));

    ResponseEntity<Map> fetched = restTemplate.getForEntity(
        url("/api/accounts/" + accountId),
        Map.class);

    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertNotNull(fetched.getBody());
    assertEquals(customerId, fetched.getBody().get("customerId"));
  }

  @Test
  void openAccount_returns400_whenInitialDepositNegative() {
    Map<String, Object> body = Map.of(
        "accountId", TestFixtures.newId("acct"),
        "customerId", TestFixtures.newId("cust"),
        "accountType", "CHECKING",
        "initialDeposit", -1L,
        "sortCode", "12-34-56");

    ResponseEntity<Map> response = restTemplate.postForEntity(
        url("/api/accounts"),
        jsonRequest(body),
        Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().get("status"));
  }

  @Test
  void updateAccountStatus_returns200_andReflectsNewStatus() {
    String accountId = TestFixtures.newId("acct");
    String customerId = TestFixtures.newId("cust");
    restTemplate.postForEntity(
        url("/api/accounts"),
        jsonRequest(TestFixtures.openAccountBody(accountId, customerId)),
        Map.class);

    ResponseEntity<Map> updated = restTemplate.exchange(
        url("/api/accounts/" + accountId + "/status"),
        HttpMethod.PATCH,
        jsonRequest(TestFixtures.updateAccountStatusBody("FROZEN")),
        Map.class);

    assertEquals(HttpStatus.OK, updated.getStatusCode());
    assertNotNull(updated.getBody());
    assertEquals("FROZEN", updated.getBody().get("status"));
  }

  @Test
  void closeAccount_returns204() {
    String accountId = TestFixtures.newId("acct");
    String customerId = TestFixtures.newId("cust");
    restTemplate.postForEntity(
        url("/api/accounts"),
        jsonRequest(TestFixtures.openAccountBody(accountId, customerId)),
        Map.class);

    ResponseEntity<Void> response = restTemplate.exchange(
        url("/api/accounts/" + accountId),
        HttpMethod.DELETE,
        null,
        Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void fetchUnknownAccount_returns404() {
    ResponseEntity<Map> response = restTemplate.getForEntity(
        url("/api/accounts/missing-" + System.nanoTime()),
        Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private static HttpEntity<Map<String, Object>> jsonRequest(Map<String, Object> body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }
}
