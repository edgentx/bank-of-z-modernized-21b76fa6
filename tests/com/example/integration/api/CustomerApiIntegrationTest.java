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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BANK S-44 — API-level integration test for {@code /api/customers}.
 *
 * <p>Boots the full Spring context against the shared Testcontainers (Mongo /
 * Redis / MinIO) singletons and exercises every REST endpoint via HTTP. The
 * goal is to verify:
 * <ul>
 *   <li>success scenarios return the expected status codes + JSON shapes,</li>
 *   <li>validation failures produce 400 + an {@code ErrorResponse} body,</li>
 *   <li>missing aggregates produce 404 + an {@code ErrorResponse} body,</li>
 *   <li>state survives a round trip through the real Mongo adapter
 *       (write then read-back via the persisted-aggregate read path).</li>
 * </ul>
 */
class CustomerApiIntegrationTest extends BaseApiIntegrationTest {

  @Test
  void enrollAndFetchCustomer_persistsThroughMongo() {
    String customerId = TestFixtures.newId("cust");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/customers"),
        jsonRequest(TestFixtures.enrollCustomerBody(customerId)),
        Map.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody());
    assertEquals(customerId, created.getBody().get("customerId"));
    assertEquals(Boolean.TRUE, created.getBody().get("enrolled"));

    // Read-back via GET — exercises the Mongo find path, not just the
    // in-memory aggregate from the create call. This is the real proof that
    // the customer was persisted, not just the response of a write call.
    ResponseEntity<Map> fetched = restTemplate.getForEntity(
        url("/api/customers/" + customerId),
        Map.class);

    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertNotNull(fetched.getBody());
    assertEquals(customerId, fetched.getBody().get("customerId"));
  }

  @Test
  void enrollCustomer_returns400_whenEmailMalformed() {
    Map<String, Object> body = Map.of(
        "customerId", TestFixtures.newId("cust"),
        "fullName", "Alice Tester",
        "email", "not-an-email",
        "governmentId", "GOV-1");

    ResponseEntity<Map> response = restTemplate.postForEntity(
        url("/api/customers"),
        jsonRequest(body),
        Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().get("status"));
  }

  @Test
  void fetchUnknownCustomer_returns404WithErrorBody() {
    ResponseEntity<Map> response = restTemplate.getForEntity(
        url("/api/customers/does-not-exist-" + System.nanoTime()),
        Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().get("status"));
  }

  @Test
  void updateCustomerDetails_returns200_andReflectsNewState() {
    String customerId = TestFixtures.newId("cust");
    restTemplate.postForEntity(
        url("/api/customers"),
        jsonRequest(TestFixtures.enrollCustomerBody(customerId)),
        Map.class);

    ResponseEntity<Map> updated = restTemplate.exchange(
        url("/api/customers/" + customerId),
        HttpMethod.PUT,
        jsonRequest(TestFixtures.updateCustomerDetailsBody()),
        Map.class);

    assertEquals(HttpStatus.OK, updated.getStatusCode());
    assertNotNull(updated.getBody());
    assertEquals("12-34-56", updated.getBody().get("sortCode"));
  }

  @Test
  void deleteCustomer_returns204() {
    String customerId = TestFixtures.newId("cust");
    restTemplate.postForEntity(
        url("/api/customers"),
        jsonRequest(TestFixtures.enrollCustomerBody(customerId)),
        Map.class);

    ResponseEntity<Void> response = restTemplate.exchange(
        url("/api/customers/" + customerId + "?hasActiveAccounts=false"),
        HttpMethod.DELETE,
        null,
        Void.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  private static HttpEntity<Map<String, Object>> jsonRequest(Map<String, Object> body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }
}
