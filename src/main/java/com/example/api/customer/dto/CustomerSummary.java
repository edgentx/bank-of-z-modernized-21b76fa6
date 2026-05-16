package com.example.api.customer.dto;

import com.example.infrastructure.mongo.customer.CustomerDocument;
import java.time.Instant;

public record CustomerSummary(
    String customerId,
    String fullName,
    String email,
    String phone,
    String status,
    String enrolledAt
) {
  public static CustomerSummary from(CustomerDocument doc) {
    return new CustomerSummary(
        doc.getId(),
        doc.getFullName(),
        doc.getEmail(),
        "",
        doc.isEnrolled() ? "ENROLLED" : "DELETED",
        Instant.now().toString());
  }
}
