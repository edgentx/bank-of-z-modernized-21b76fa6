package com.example.api.legacybridge.dto;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;

public record RouteResponse(
    String routeId,
    String targetSystem,
    boolean evaluated,
    int version
) {
  public static RouteResponse from(LegacyTransactionRoute route) {
    return new RouteResponse(
        route.id(),
        route.getTargetSystem(),
        route.isEvaluated(),
        route.getVersion());
  }
}
