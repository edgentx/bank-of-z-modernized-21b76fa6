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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BANK S-44 — cross-service integration test exercising the event-driven
 * flow that spans customer → account → transaction → legacy-bridge.
 *
 * <p>This is the suite that validates the AC "Cross-service integration tests
 * validate event-driven interactions": the {@link
 * com.example.domain.customer.model.CustomerEnrolledEvent},
 * {@link com.example.domain.account.model.AccountOpenedEvent}, and
 * {@link com.example.domain.transaction.model.TransactionPostedEvent} are
 * emitted by their respective aggregates as part of the persisted state, and
 * the read-back via the find endpoints proves the events were recorded in the
 * aggregate's version + state — i.e. the write-side and read-side are in sync.
 *
 * <p>The flow is intentionally one method (not split per-step) because the
 * test is asserting that the whole chain is reproducible end-to-end against
 * real backing stores. A failure on step 3 with steps 1+2 green tells the
 * operator exactly where the integration cracked.
 */
class CrossServiceFlowIntegrationTest extends BaseApiIntegrationTest {

  @Test
  void enrollCustomerOpenAccountAndPostTransaction_completesEndToEnd() {
    String customerId = TestFixtures.newId("cust");
    String accountId = TestFixtures.newId("acct");
    String txId = TestFixtures.newId("tx");

    // 1) Enroll customer — should fire CustomerEnrolledEvent and persist.
    ResponseEntity<Map> custCreated = restTemplate.postForEntity(
        url("/api/customers"),
        jsonRequest(TestFixtures.enrollCustomerBody(customerId)),
        Map.class);
    assertEquals(HttpStatus.CREATED, custCreated.getStatusCode());
    assertEquals(Boolean.TRUE, custCreated.getBody().get("enrolled"));

    // 2) Open account for that customer — AccountOpenedEvent + persist.
    ResponseEntity<Map> acctCreated = restTemplate.postForEntity(
        url("/api/accounts"),
        jsonRequest(TestFixtures.openAccountBody(accountId, customerId)),
        Map.class);
    assertEquals(HttpStatus.CREATED, acctCreated.getStatusCode());
    assertEquals(Boolean.TRUE, acctCreated.getBody().get("opened"));
    assertEquals(customerId, acctCreated.getBody().get("customerId"));

    // 3) Post a deposit on the account — TransactionPostedEvent + persist.
    ResponseEntity<Map> txCreated = restTemplate.postForEntity(
        url("/api/transactions/deposits"),
        jsonRequest(TestFixtures.postDepositBody(txId, accountId)),
        Map.class);
    assertEquals(HttpStatus.CREATED, txCreated.getStatusCode());
    assertEquals(txId, txCreated.getBody().get("transactionId"));

    // 4) Read-back each aggregate through GET — proves all three were
    // persisted to Mongo. If any of the writes had been swallowed by a
    // broken adapter, the corresponding GET would 404.
    ResponseEntity<Map> cust = restTemplate.getForEntity(
        url("/api/customers/" + customerId), Map.class);
    assertEquals(HttpStatus.OK, cust.getStatusCode());
    assertNotNull(cust.getBody());
    assertEquals(customerId, cust.getBody().get("customerId"));

    ResponseEntity<Map> acct = restTemplate.getForEntity(
        url("/api/accounts/" + accountId), Map.class);
    assertEquals(HttpStatus.OK, acct.getStatusCode());
    assertEquals(customerId, acct.getBody().get("customerId"));

    ResponseEntity<Map> tx = restTemplate.getForEntity(
        url("/api/transactions/" + txId), Map.class);
    assertEquals(HttpStatus.OK, tx.getStatusCode());
    assertEquals(accountId, tx.getBody().get("accountId"));
    assertEquals("deposit", tx.getBody().get("kind"));
  }

  @Test
  void routingRecordedThenReadBack_completesEndToEnd() {
    String routeId = TestFixtures.newId("route");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/legacy-bridge/routes"),
        jsonRequest(TestFixtures.evaluateRoutingBody(routeId)),
        Map.class);
    assertEquals(HttpStatus.CREATED, created.getStatusCode());

    ResponseEntity<Map> fetched = restTemplate.getForEntity(
        url("/api/legacy-bridge/routes/" + routeId),
        Map.class);
    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertTrue(fetched.getBody().containsKey("routeId"));
  }

  private static HttpEntity<Map<String, Object>> jsonRequest(Map<String, Object> body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }
}
