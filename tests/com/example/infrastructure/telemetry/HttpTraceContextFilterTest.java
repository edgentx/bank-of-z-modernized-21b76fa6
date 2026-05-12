package com.example.infrastructure.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * BANK S-35 — {@link HttpTraceContextFilter} contract tests.
 *
 * <p>Validates the HTTP half of the "context propagation works across
 * HTTP and JMS boundaries" acceptance criterion: a W3C
 * {@code traceparent} header on the inbound request becomes a span
 * parent the downstream code can attach children to.
 */
class HttpTraceContextFilterTest {

  private InMemorySpanExporter exporter;
  private SdkTracerProvider tracerProvider;
  private OpenTelemetrySdk sdk;

  @BeforeEach
  void setUp() {
    exporter = InMemorySpanExporter.create();
    tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(SimpleSpanProcessor.create(exporter))
        .build();
    sdk = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setPropagators(ContextPropagators.create(
            io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator.getInstance()))
        .build();
  }

  @AfterEach
  void tearDown() {
    tracerProvider.close();
  }

  @Test
  void recordsServerSpanWithStatusCode() throws ServletException, IOException {
    HttpTraceContextFilter filter = new HttpTraceContextFilter(sdk);
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/accounts/123");
    MockHttpServletResponse resp = new MockHttpServletResponse();
    resp.setStatus(204);

    filter.doFilter(req, resp, new MockFilterChain());

    SpanData span = exporter.getFinishedSpanItems().get(0);
    assertEquals("GET /accounts/123", span.getName());
    assertEquals(204L, span.getAttributes().get(
        io.opentelemetry.api.common.AttributeKey.longKey("http.status_code")));
  }

  @Test
  void extractsUpstreamTraceparentAsParent() throws ServletException, IOException {
    HttpTraceContextFilter filter = new HttpTraceContextFilter(sdk);
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/health");
    // Sampled trace (last byte = 01 = sampled flag set).
    String upstreamTraceId = "4bf92f3577b34da6a3ce929d0e0e4736";
    req.addHeader("traceparent",
        "00-" + upstreamTraceId + "-00f067aa0ba902b7-01");

    filter.doFilter(req, new MockHttpServletResponse(), new MockFilterChain());

    SpanData span = exporter.getFinishedSpanItems().get(0);
    assertEquals(upstreamTraceId, span.getTraceId(),
        "the upstream traceparent must become the parent context so the " +
            "modernized service joins the caller's trace rather than rooting " +
            "a fresh disconnected one");
  }

  @Test
  void noopOpenTelemetryStillCompletesFilterChain() throws ServletException, IOException {
    // When telemetry.otel.enabled=false the bean is OpenTelemetry.noop().
    // The filter must still run the downstream chain — otherwise turning
    // telemetry off would break request handling, which would be a much
    // bigger production hazard than dropping a few spans.
    HttpTraceContextFilter filter = new HttpTraceContextFilter(OpenTelemetry.noop());
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/health");
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(req, new MockHttpServletResponse(), chain);

    // The MockFilterChain records the request that flowed through it.
    assertTrue(chain.getRequest() != null,
        "filter must always invoke the downstream chain even when " +
            "telemetry is disabled");
    assertFalse(Span.fromContext(Context.current()).getSpanContext().isValid(),
        "no-op tracer must not leak a fake span onto the calling context " +
            "after the filter completes");
  }
}
