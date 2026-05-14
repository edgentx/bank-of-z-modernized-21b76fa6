package com.example.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping(value = {"/", "/health", "/api", "/api/health"},
      produces = {
          MediaType.APPLICATION_JSON_VALUE,
          MediaType.TEXT_HTML_VALUE,
          MediaType.TEXT_PLAIN_VALUE
      })
  public String health() {
    return "{\"status\":\"UP\"}";
  }
}
