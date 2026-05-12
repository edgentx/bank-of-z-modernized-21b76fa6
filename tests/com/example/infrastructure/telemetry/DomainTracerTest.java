package com.example.infrastructure.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * BANK S-35 — {@link DomainTracer} span-recording behavior.
 *
 * <p>Backed by the OpenTelemetry SDK's {@link InMemorySpanExporter} so we
 * can assert that custom domain spans are actually recorded with the
 * configured attributes — the acceptance criteria "Custom spans are
 * created for domain service operations" and "Trace context includes
 * CICS/IMS pathway identifiers" both require this evidence at the span
 * level, not at the bean-graph level.
 */
class DomainTracerTest {

  private InMemorySpanExporter exporter;
  private SdkTracerProvider tracerProvider;
  private DomainTracer tracer;

  @BeforeEach
  void setUp() {
    exporter = InMemorySpanExporter.create();
    // SimpleSpanProcessor flushes synchronously on end() so assertions can
    // read the spans without sleeping — the BatchSpanProcessor used in prod
    // is wrong for tests because it schedules an async export thread.
    tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(SimpleSpanProcessor.create(exporter))
        .build();
    OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .build();
    Tracer raw = sdk.getTracer("test");
    tracer = new DomainTracer(raw);
  }

  @AfterEach
  void tearDown() {
    tracerProvider.close();
  }

  @Test
  void recordsSpanWithCicsImsAttributesAndStatusOk() {
    String result = tracer.trace(
        "legacy-bridge.evaluate-routing",
        span -> {
          span.setAttribute(CicsImsAttributes.LEGACY_ROUTE_ID, "route-42");
          span.setAttribute(CicsImsAttributes.CICS_TRANSACTION_ID, "ACCT");
          span.setAttribute(CicsImsAttributes.CICS_PROGRAM_NAME, "ACCTOPEN");
          span.setAttribute(CicsImsAttributes.LEGACY_PATHWAY, "CICS");
        },
        () -> "OK");

    assertEquals("OK", result, "trace() must return the body's return value");
    List<SpanData> spans = exporter.getFinishedSpanItems();
    assertEquals(1, spans.size());
    SpanData span = spans.get(0);
    assertEquals("legacy-bridge.evaluate-routing", span.getName());
    assertEquals(StatusCode.OK, span.getStatus().getStatusCode());
    assertEquals("route-42", span.getAttributes().get(CicsImsAttributes.LEGACY_ROUTE_ID));
    assertEquals("ACCT", span.getAttributes().get(CicsImsAttributes.CICS_TRANSACTION_ID));
    assertEquals("ACCTOPEN", span.getAttributes().get(CicsImsAttributes.CICS_PROGRAM_NAME));
    assertEquals("CICS", span.getAttributes().get(CicsImsAttributes.LEGACY_PATHWAY));
  }

  @Test
  void recordsErrorStatusAndRethrowsWhenBodyThrows() {
    RuntimeException boom = new IllegalStateException("downstream CICS unreachable");

    RuntimeException raised = assertThrows(RuntimeException.class,
        () -> tracer.trace(
            "legacy-bridge.evaluate-routing",
            span -> span.setAttribute(CicsImsAttributes.LEGACY_ROUTE_ID, "route-99"),
            () -> { throw boom; }));

    assertSame(boom, raised, "exception must be re-thrown unchanged so the " +
        "application's own error-handling layer sees the original cause");
    List<SpanData> spans = exporter.getFinishedSpanItems();
    assertEquals(1, spans.size());
    assertEquals(StatusCode.ERROR, spans.get(0).getStatus().getStatusCode());
    assertTrue(spans.get(0).getEvents().stream()
            .anyMatch(e -> "exception".equals(e.getName())),
        "the SDK must record the throwable as an exception event so the " +
            "collector can surface the cause in trace UIs");
  }

  @Test
  void singleAttributeOverloadConvenience() {
    Integer result = tracer.trace("op", CicsImsAttributes.LEGACY_ROUTE_ID, "route-7",
        () -> 17);

    assertEquals(17, result);
    SpanData span = exporter.getFinishedSpanItems().get(0);
    assertEquals("route-7", span.getAttributes().get(CicsImsAttributes.LEGACY_ROUTE_ID));
  }

  @Test
  void runnableOverloadCompletesAndRecordsSpan() {
    tracer.trace("op",
        span -> span.setAttribute(CicsImsAttributes.LEGACY_PATHWAY, "MODERN"),
        () -> { /* no-op runnable */ });

    List<SpanData> spans = exporter.getFinishedSpanItems();
    assertEquals(1, spans.size());
    assertEquals("MODERN", spans.get(0).getAttributes().get(CicsImsAttributes.LEGACY_PATHWAY));
  }

  @Test
  void exposesUnderlyingTracerForRawSdkAccess() {
    assertNotNull(tracer.tracer(),
        "raw Tracer accessor must be available for callers that need " +
            "spanBuilder() / spanLink() outside the helper's API");
  }
}
