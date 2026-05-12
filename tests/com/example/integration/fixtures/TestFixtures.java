package com.example.integration.fixtures;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * BANK S-44 — reusable HTTP-body fixtures for the API integration suite.
 *
 * <p>Acceptance criterion: "Test data fixtures support reproducible test
 * execution". This holder produces fresh JSON-shaped {@link Map} payloads
 * for the REST controllers under test. Identifiers are randomised per-call
 * so concurrent test methods cannot collide on the same primary key in
 * Mongo, and the request bodies match the validation rules declared by
 * the controller DTOs (positive amounts, 3-char currency codes, valid emails,
 * etc).
 *
 * <p>Centralising the builders avoids drifting payload shapes across the five
 * controller integration tests: a future DTO field rename is a single-line
 * change here, not five.
 */
public final class TestFixtures {

  private TestFixtures() {
    // static factory holder
  }

  public static String newId(String prefix) {
    return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
  }

  public static Map<String, Object> enrollCustomerBody(String customerId) {
    return Map.of(
        "customerId", customerId,
        "fullName", "Alice Tester",
        "email", customerId + "@example.com",
        "governmentId", "GOV-" + customerId);
  }

  public static Map<String, Object> openAccountBody(String accountId, String customerId) {
    return Map.of(
        "accountId", accountId,
        "customerId", customerId,
        "accountType", "CHECKING",
        "initialDeposit", 1000L,
        "sortCode", "12-34-56");
  }

  public static Map<String, Object> updateAccountStatusBody(String newStatus) {
    return Map.of("newStatus", newStatus);
  }

  public static Map<String, Object> postDepositBody(String txId, String accountId) {
    return Map.of(
        "transactionId", txId,
        "accountId", accountId,
        "amount", new BigDecimal("100.00"),
        "currency", "USD");
  }

  public static Map<String, Object> postWithdrawalBody(String txId, String accountId) {
    return Map.of(
        "transactionId", txId,
        "accountId", accountId,
        "amount", new BigDecimal("25.00"),
        "currency", "USD");
  }

  public static Map<String, Object> reverseTransactionBody() {
    return Map.of("reason", "customer dispute");
  }

  public static Map<String, Object> startSessionBody(String tellerId, String terminalId) {
    return Map.of(
        "tellerId", tellerId,
        "terminalId", terminalId);
  }

  public static Map<String, Object> evaluateRoutingBody(String routeId) {
    return Map.of(
        "routeId", routeId,
        "transactionType", "DEPOSIT",
        "payload", Map.of("source", "BRANCH-NY-01"),
        "rulesVersion", 1);
  }

  public static Map<String, Object> recordCheckpointBody(String checkpointId) {
    return Map.of(
        "checkpointId", checkpointId,
        "syncOffset", 100L,
        "validationHash", "sha256:abc123");
  }

  public static Map<String, Object> updateCustomerDetailsBody() {
    return Map.of(
        "emailAddress", "alice.new@example.com",
        "sortCode", "12-34-56");
  }

  public static Map<String, Object> navigateMenuBody(String menuId, String action) {
    return Map.of(
        "menuId", menuId,
        "action", action);
  }
}
