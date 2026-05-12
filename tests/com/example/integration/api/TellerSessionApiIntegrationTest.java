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
 * BANK S-44 — API-level integration test for {@code /api/sessions}.
 */
class TellerSessionApiIntegrationTest extends BaseApiIntegrationTest {

  @Test
  void startAndFetchSession_persistsThroughMongo() {
    String tellerId = TestFixtures.newId("teller");
    String terminalId = TestFixtures.newId("term");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/sessions"),
        jsonRequest(TestFixtures.startSessionBody(tellerId, terminalId)),
        Map.class);

    assertEquals(HttpStatus.CREATED, created.getStatusCode());
    assertNotNull(created.getBody());

    String sessionId = (String) created.getBody().get("sessionId");
    assertNotNull(sessionId);

    ResponseEntity<Map> fetched = restTemplate.getForEntity(
        url("/api/sessions/" + sessionId),
        Map.class);
    assertEquals(HttpStatus.OK, fetched.getStatusCode());
    assertNotNull(fetched.getBody());
    assertEquals(sessionId, fetched.getBody().get("sessionId"));
  }

  @Test
  void startSession_returns400_whenTellerIdBlank() {
    Map<String, Object> body = Map.of(
        "tellerId", "",
        "terminalId", TestFixtures.newId("term"));

    ResponseEntity<Map> response = restTemplate.postForEntity(
        url("/api/sessions"),
        jsonRequest(body),
        Map.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void fetchUnknownSession_returns404() {
    ResponseEntity<Map> response = restTemplate.getForEntity(
        url("/api/sessions/missing-" + System.nanoTime()),
        Map.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void endSession_returns204() {
    String tellerId = TestFixtures.newId("teller");
    String terminalId = TestFixtures.newId("term");

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/sessions"),
        jsonRequest(TestFixtures.startSessionBody(tellerId, terminalId)),
        Map.class);
    String sessionId = (String) created.getBody().get("sessionId");

    ResponseEntity<Void> response = restTemplate.exchange(
        url("/api/sessions/" + sessionId),
        HttpMethod.DELETE,
        null,
        Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  private static HttpEntity<Map<String, Object>> jsonRequest(Map<String, Object> body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }
}
