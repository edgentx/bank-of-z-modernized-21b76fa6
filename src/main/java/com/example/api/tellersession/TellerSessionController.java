package com.example.api.tellersession;

import com.example.api.tellersession.dto.NavigateMenuRequest;
import com.example.api.tellersession.dto.StartSessionRequest;
import com.example.api.tellersession.dto.TellerSessionResponse;
import com.example.application.tellersession.TellerSessionAppService;
import com.example.domain.tellersession.model.EndSessionCmd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Teller Session", description = "Start, navigate, and end teller terminal sessions")
public class TellerSessionController {

  private final TellerSessionAppService service;

  public TellerSessionController(TellerSessionAppService service) {
    this.service = service;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Start a new teller session")
  public ResponseEntity<TellerSessionResponse> start(@Valid @RequestBody StartSessionRequest request) {
    return ResponseEntity.status(201).body(TellerSessionResponse.from(service.startSession(request.toCommand())));
  }

  @PostMapping(value = "/{sessionId}/navigate", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Navigate a teller-session menu")
  public TellerSessionResponse navigate(
      @PathVariable String sessionId,
      @Valid @RequestBody NavigateMenuRequest request) {
    return TellerSessionResponse.from(service.navigateMenu(sessionId, request.toCommand(sessionId)));
  }

  @DeleteMapping("/{sessionId}")
  @Operation(summary = "End a teller session")
  public ResponseEntity<Void> end(@PathVariable String sessionId) {
    service.endSession(sessionId, new EndSessionCmd(sessionId));
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{sessionId}")
  @Operation(summary = "Fetch a teller session by id")
  public TellerSessionResponse find(@PathVariable String sessionId) {
    return TellerSessionResponse.from(service.findById(sessionId));
  }
}
