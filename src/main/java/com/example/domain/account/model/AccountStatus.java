package com.example.domain.account.model;

/**
 * Strongly-typed lifecycle status for an Account aggregate.
 *
 * Replaces free-form string status values, making invalid states (e.g. typos
 * like "FROOZEN") unrepresentable at the domain layer.
 */
public enum AccountStatus {
  ACTIVE,
  FROZEN,
  CLOSED;

  /**
   * Parse a case-insensitive status string from a command boundary.
   *
   * @throws IllegalArgumentException if the value does not match a known status.
   */
  public static AccountStatus parse(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("newStatus required");
    }
    try {
      return AccountStatus.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("unknown account status: " + value);
    }
  }
}
