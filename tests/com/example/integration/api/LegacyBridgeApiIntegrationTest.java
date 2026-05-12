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
 * BANK S-44 — API-level integration test for {@code /api/legacy-bridge}.
 *
 * <p>The legacy-bridge service is in-memory (see {@code LegacyBridgeAppService})
 * so persistence is per-JVM. These tests still ride a full Spring context so
 * the @Valid/@ExceptionHandler chain is exercised exactly as it would be on
 * a real REST request.
 */
class LegacyBridgeApiIntegrationTest extends BaseApiIntegrationTest {

  @Test
  void evaluateRouting_returns201_andRouteIsFetchable() {
    String routeId = TestFixtures.newId("route");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/legacy-bridge/routes"),
        jsonRequest(TestFixtures.evaluateRoutingBody(routeId)),
        Map.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody());
    assertEquals(routeId, created.getBody().get("routeId"));

    ResponseEntity<Map> fetched = restTemplate.getForEntity(
        url("/api/legacy-bridge/routes/" + routeId),
        Map.class);
    assertEquals(HttpStatus.OK, fetched.getStatusCode());
  }

  @Test
  void evaluateRouting_returns400_whenRouteIdBlank() {
    Map<String, Object> body = Map.of(
        "routeId", "",
        "transactionType", "DEPOSIT",
        "payload", Map.of("source", "BRANCH-NY-01"),
        "rulesVersion", 1);

    ResponseEntity<Map> response = restTemplate.postForEntity(
        url("/api/legacy-bridge/routes"),
        jsonRequest(body),
        Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void findUnknownRoute_returns404() {
    ResponseEntity<Map> response = restTemplate.getForEntity(
        url("/api/legacy-bridge/routes/missing-" + System.nanoTime()),
        Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void recordCheckpoint_returns201_andCheckpointIsFetchable() {
    String checkpointId = TestFixtures.newId("ckpt");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/legacy-bridge/checkpoints"),
        jsonRequest(TestFixtures.recordCheckpointBody(checkpointId)),
        Map.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody());
    assertEquals(checkpointId, created.getBody().get("checkpointId"));

    ResponseEntity<Map> fetched = restTemplate.getForEntity(
        url("/api/legacy-bridge/checkpoints/" + checkpointId),
        Map.class);
    assertEquals(HttpStatus.OK, fetched.getStatusCode());
  }

  @Test
  void findUnknownCheckpoint_returns404() {
    ResponseEntity<Map> response = restTemplate.getForEntity(
        url("/api/legacy-bridge/checkpoints/missing-" + System.nanoTime()),
        Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private static HttpEntity<Map<String, Object>> jsonRequest(Map<String, Object> body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }
}
