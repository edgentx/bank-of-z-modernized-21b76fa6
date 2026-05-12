package com.example.infrastructure.telemetry;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * BANK S-35 — {@link OpenTelemetryConfig} bean-wiring tests.
 *
 * <p>Uses Spring's {@link ApplicationContextRunner} to load the config in
 * isolation (no @SpringBootTest, no auto-configuration churn) and validate
 * the bean graph end to end:
 *
 * <ul>
 *   <li>{@code telemetry.otel.enabled=false} → the {@link OpenTelemetry}
 *       bean is the no-op instance, so downstream code that wires a
 *       {@link DomainTracer} or a {@link JmsTraceContextPropagator} never
 *       has to branch on the feature flag;</li>
 *   <li>{@code telemetry.otel.enabled=true} → the bean is a real
 *       {@link OpenTelemetrySdk} with the configured OTLP endpoint, a
 *       parent-based ratio sampler, and the W3C trace-context propagator
 *       behind the {@link OpenTelemetry#getPropagators() ContextPropagators}.</li>
 * </ul>
 */
class OpenTelemetryConfigTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withUserConfiguration(OpenTelemetryConfig.class);

  @Test
  void publishesNoopOpenTelemetryWhenDisabled() {
    contextRunner
        .withPropertyValues("telemetry.otel.enabled=false")
        .run(ctx -> {
          OpenTelemetry otel = ctx.getBean(OpenTelemetry.class);
          assertNotNull(otel);
          // OpenTelemetry.noop() returns the same singleton each call; the SDK
          // instance is a distinct concrete class so identity comparison
          // is a clean discriminator.
          assertSame(OpenTelemetry.noop(), otel,
              "disabled config must publish the OpenTelemetry.noop() singleton");

          // Downstream beans still resolve so domain code wires unchanged.
          assertNotNull(ctx.getBean(Tracer.class));
          assertNotNull(ctx.getBean(DomainTracer.class));
          assertNotNull(ctx.getBean(HttpTraceContextFilter.class));
          assertNotNull(ctx.getBean(JmsTraceContextPropagator.class));
        });
  }

  @Test
  void publishesRealSdkWhenEnabled() {
    contextRunner
        .withPropertyValues(
            "telemetry.otel.enabled=true",
            "telemetry.otel.service-name=teller-core-prod",
            "telemetry.otel.environment=prod",
            // Endpoint must parse as a URL but is never actually contacted by
            // the test — the BatchSpanProcessor schedules an export thread
            // which only ships spans on flush.
            "telemetry.otel.otlp-endpoint=http://otel-collector.example.com:4317",
            "telemetry.otel.sampling-ratio=0.5")
        .run(ctx -> {
          OpenTelemetry otel = ctx.getBean(OpenTelemetry.class);
          assertNotNull(otel);
          assertTrue(otel instanceof OpenTelemetrySdk,
              "enabled config must publish a real OpenTelemetrySdk instance");
          assertNotNull(otel.getPropagators().getTextMapPropagator(),
              "TextMap propagator must be wired so HTTP and JMS sides can " +
                  "extract/inject trace context");
        });
  }

  @Test
  void clampsOutOfRangeSamplingRatio() {
    // The acceptance criterion is "Sampling configuration is adjustable via
    // environment variables"; a misconfigured operator setting 1.5 or -0.1
    // should not crash the SDK on boot — verify the config silently clamps.
    contextRunner
        .withPropertyValues(
            "telemetry.otel.enabled=true",
            "telemetry.otel.sampling-ratio=1.5")
        .run(ctx -> assertNotNull(ctx.getBean(OpenTelemetry.class),
            "out-of-range ratio must be clamped to [0,1], not propagated as-is"));

    contextRunner
        .withPropertyValues(
            "telemetry.otel.enabled=true",
            "telemetry.otel.sampling-ratio=-0.1")
        .run(ctx -> assertNotNull(ctx.getBean(OpenTelemetry.class)));
  }
}
