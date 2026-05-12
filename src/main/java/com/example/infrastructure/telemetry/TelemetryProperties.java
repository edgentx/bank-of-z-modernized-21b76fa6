package com.example.infrastructure.telemetry;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BANK S-35 — externalized config for the OpenTelemetry tracing adapter.
 *
 * <p>Bound to {@code telemetry.otel.*} keys in {@code application.properties}
 * (overridable per environment via {@code TELEMETRY_OTEL_*} env vars). The
 * dedicated {@code telemetry.otel.*} prefix — rather than reusing the SDK's
 * conventional {@code otel.*} system properties — keeps Spring Boot's
 * property-binding semantics applicable (env-var ↔ kebab-case relaxed-binding,
 * {@code @ConfigurationProperties} validation, profile overrides) and matches
 * the {@link com.example.infrastructure.temporal.TemporalProperties} /
 * {@link com.example.infrastructure.jms.IbmMqJmsProperties} convention from
 * S-33 and S-34.
 *
 * <p>{@link #enabled} defaults to {@code false} so unit tests and the Cucumber
 * BDD suite never have to mock a collector — OpenTelemetryConfig hands out a
 * {@code OpenTelemetry.noop()} instance when disabled, so {@code DomainTracer},
 * {@code HttpTraceContextFilter}, and {@code JmsTraceContextPropagator} all
 * silently no-op and downstream code wires unchanged. Container deployments
 * flip {@code TELEMETRY_OTEL_ENABLED=true} to activate the real OTLP pipeline.
 *
 * <p>{@link #samplingRatio} drives the parent-based ratio sampler so a
 * sampling decision made upstream (e.g. by an HTTP gateway) is honored
 * intact, and root spans started inside this service are sampled at the
 * configured ratio. {@code 1.0} = sample everything (sensible for dev/CI),
 * {@code 0.1} = sample 10% (sensible for prod traffic).
 */
@ConfigurationProperties(prefix = "telemetry.otel")
public class TelemetryProperties {

  /**
   * Master kill-switch for the OpenTelemetry pipeline. When {@code false}
   * the config publishes {@link io.opentelemetry.api.OpenTelemetry#noop()}
   * so every tracing call becomes a cheap no-op — domain code does not
   * need to be conditional on this flag.
   */
  private boolean enabled = false;

  /**
   * Logical service name emitted as the {@code service.name} resource
   * attribute on every span and metric. Operators correlate traces with
   * dashboards by this name, so it must stay stable across deploys.
   */
  private String serviceName = "teller-core";

  /**
   * Free-form environment label ({@code dev} / {@code staging} / {@code prod})
   * emitted as the {@code deployment.environment} resource attribute. Lets
   * the collector route traces to the right backend without re-deriving the
   * environment from hostname conventions.
   */
  private String environment = "dev";

  /** OTLP gRPC/HTTP endpoint the exporter ships finished spans to. */
  private String otlpEndpoint = "http://localhost:4317";

  /**
   * Per-export timeout for the OTLP exporter. The default of 10s matches the
   * SDK out-of-the-box; we surface it as a property because collector
   * deployments behind a service mesh sometimes need a higher ceiling.
   */
  private Duration otlpTimeout = Duration.ofSeconds(10);

  /**
   * Parent-based ratio sampler probability — what fraction of root spans
   * started inside this service get sampled. Range {@code [0.0, 1.0]}.
   * Operators commonly start at 1.0 (sample all) in lower environments and
   * drop it under load in prod.
   */
  private double samplingRatio = 1.0;

  /**
   * Max queue size for the BatchSpanProcessor. Spans are buffered here
   * before being shipped to the OTLP exporter; a backed-up queue silently
   * drops spans once full so we surface the knob.
   */
  private int maxQueueSize = 2048;

  /**
   * BatchSpanProcessor schedule delay — the SDK flushes every {@code N} ms
   * or whenever the queue exceeds the export batch size, whichever is
   * sooner. Smaller = lower trace latency, more network chatter.
   */
  private Duration scheduleDelay = Duration.ofMillis(500);

  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }

  public String getServiceName() { return serviceName; }
  public void setServiceName(String serviceName) { this.serviceName = serviceName; }

  public String getEnvironment() { return environment; }
  public void setEnvironment(String environment) { this.environment = environment; }

  public String getOtlpEndpoint() { return otlpEndpoint; }
  public void setOtlpEndpoint(String otlpEndpoint) { this.otlpEndpoint = otlpEndpoint; }

  public Duration getOtlpTimeout() { return otlpTimeout; }
  public void setOtlpTimeout(Duration otlpTimeout) { this.otlpTimeout = otlpTimeout; }

  public double getSamplingRatio() { return samplingRatio; }
  public void setSamplingRatio(double samplingRatio) { this.samplingRatio = samplingRatio; }

  public int getMaxQueueSize() { return maxQueueSize; }
  public void setMaxQueueSize(int maxQueueSize) { this.maxQueueSize = maxQueueSize; }

  public Duration getScheduleDelay() { return scheduleDelay; }
  public void setScheduleDelay(Duration scheduleDelay) { this.scheduleDelay = scheduleDelay; }
}
