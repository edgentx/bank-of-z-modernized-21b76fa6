package com.example.api.customer.dto;

import com.example.domain.customer.model.CustomerAggregate;

public record CustomerResponse(
    String customerId,
    String fullName,
    String email,
    String sortCode,
    boolean enrolled,
    int version
) {
  public static CustomerResponse from(CustomerAggregate agg) {
    return new CustomerResponse(
        agg.id(),
        agg.getFullName(),
        agg.getEmail(),
        agg.getSortCode(),
        agg.isEnrolled(),
        agg.getVersion());
  }
}
