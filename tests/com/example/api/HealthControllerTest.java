package com.example.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void healthReturns200ForDeploymentProbeCompatibility() throws Exception {
    assertHealthOk("/health");
  }

  @Test
  void rootAndApiBaseReturn200ForDeploymentSmokeChecks() throws Exception {
    assertHealthOk("/");
    assertHealthOk("/api");
    assertHealthOk("/api/health");
  }

  @Test
  void rootAndApiBaseReturn200ForBrowserAcceptHeaders() throws Exception {
    assertHealthOkForBrowser("/");
    assertHealthOkForBrowser("/api");
  }

  private void assertHealthOk(String path) throws Exception {
    mockMvc.perform(get(path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }

  private void assertHealthOkForBrowser(String path) throws Exception {
    mockMvc.perform(get(path).accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }
}
