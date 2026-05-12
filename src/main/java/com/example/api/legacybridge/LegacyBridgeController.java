package com.example.api.legacybridge;

import com.example.api.legacybridge.dto.CheckpointResponse;
import com.example.api.legacybridge.dto.EvaluateRoutingRequest;
import com.example.api.legacybridge.dto.RecordSyncCheckpointRequest;
import com.example.api.legacybridge.dto.RouteResponse;
import com.example.api.legacybridge.dto.UpdateRoutingRuleRequest;
import com.example.api.legacybridge.dto.VerifyDataParityRequest;
import com.example.application.legacybridge.LegacyBridgeAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/legacy-bridge", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Legacy Bridge", description = "Strangler-fig routing and data-parity verification")
public class LegacyBridgeController {

  private final LegacyBridgeAppService service;

  public LegacyBridgeController(LegacyBridgeAppService service) {
    this.service = service;
  }

  @PostMapping(value = "/routes", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Evaluate routing for a new transaction")
  public ResponseEntity<RouteResponse> evaluateRouting(@Valid @RequestBody EvaluateRoutingRequest request) {
    return ResponseEntity.status(201).body(RouteResponse.from(service.evaluateRouting(request.toCommand())));
  }

  @PutMapping(value = "/routes/{routeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update an existing routing rule")
  public RouteResponse updateRoutingRule(
      @PathVariable String routeId,
      @Valid @RequestBody UpdateRoutingRuleRequest request) {
    return RouteResponse.from(service.updateRoutingRule(routeId, request.toCommand()));
  }

  @GetMapping("/routes/{routeId}")
  @Operation(summary = "Fetch a routing rule by id")
  public RouteResponse findRoute(@PathVariable String routeId) {
    return RouteResponse.from(service.findRoute(routeId));
  }

  @PostMapping(value = "/checkpoints", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Record a data-sync checkpoint")
  public ResponseEntity<CheckpointResponse> recordCheckpoint(@Valid @RequestBody RecordSyncCheckpointRequest request) {
    return ResponseEntity.status(201).body(CheckpointResponse.from(service.recordCheckpoint(request.toCommand())));
  }

  @PostMapping(value = "/checkpoints/parity", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Verify data parity between legacy and modern stores")
  public CheckpointResponse verifyParity(@Valid @RequestBody VerifyDataParityRequest request) {
    return CheckpointResponse.from(service.verifyDataParity(request.toCommand()));
  }

  @GetMapping("/checkpoints/{checkpointId}")
  @Operation(summary = "Fetch a data-sync checkpoint by id")
  public CheckpointResponse findCheckpoint(@PathVariable String checkpointId) {
    return CheckpointResponse.from(service.findCheckpoint(checkpointId));
  }
}
