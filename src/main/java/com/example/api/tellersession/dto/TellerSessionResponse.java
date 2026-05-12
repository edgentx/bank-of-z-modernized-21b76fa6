package com.example.api.tellersession.dto;

import com.example.domain.tellersession.model.TellerSessionAggregate;

public record TellerSessionResponse(
    String sessionId,
    String status,
    boolean authenticated,
    boolean timedOut,
    int version
) {
  public static TellerSessionResponse from(TellerSessionAggregate agg) {
    return new TellerSessionResponse(
        agg.id(),
        agg.getStatus() != null ? agg.getStatus().name() : null,
        agg.isAuthenticated(),
        agg.isTimedOut(),
        agg.getVersion());
  }
}
