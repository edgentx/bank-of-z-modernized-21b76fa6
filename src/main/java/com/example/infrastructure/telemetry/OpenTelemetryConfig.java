package com.example.infrastructure.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BANK S-35 — Spring wiring for the OpenTelemetry tracing adapter.
 *
 * <p>Mirrors the hand-wired-adapter pattern set by {@link
 * com.example.infrastructure.temporal.TemporalConfig},
 * {@link com.example.infrastructure.redis.RedisCacheConfig},
 * {@link com.example.infrastructure.minio.MinioClientConfig}, and
 * {@link com.example.infrastructure.jms.IbmMqJmsConfig}: configuration
 * lives in a dedicated {@code @Configuration} class (not
 * {@code @Component} on the adapter) so:
 *
 * <ul>
 *   <li>tests can swap the {@link OpenTelemetry} bean for a test SDK
 *       backed by an in-memory exporter without standing up a collector;</li>
 *   <li>upgrading the OpenTelemetry SDK does not silently change
 *       behavior through a starter-version bump — every configuration
 *       choice is visible in this file;</li>
 *   <li>bean lifecycle is deterministic: the configured propagator,
 *       sampler, and resource are constructed in one place.</li>
 * </ul>
 *
 * <p>Beans configured here:
 * <ul>
 *   <li>{@link OpenTelemetry} — the SDK root. When
 *       {@code telemetry.otel.enabled=false} (the default for unit tests
 *       and the embedded BDD suite) we publish {@code OpenTelemetry.noop()}
 *       so every {@link DomainTracer}/{@link HttpTraceContextFilter}/{@link
 *       JmsTraceContextPropagator} call becomes a cheap no-op and downstream
 *       code wires unchanged;</li>
 *   <li>{@link Tracer} — convenience accessor for the application
 *       instrumentation scope, scoped under
 *       {@code com.example.infrastructure.telemetry};</li>
 *   <li>{@link DomainTracer} — the helper application code uses to wrap
 *       domain-service operations in custom spans;</li>
 *   <li>{@link HttpTraceContextFilter} — registers automatically into the
 *       servlet filter chain because Spring Boot picks up
 *       {@code jakarta.servlet.Filter} beans by default;</li>
 *   <li>{@link JmsTraceContextPropagator} — referenced by
 *       {@link com.example.infrastructure.jms.IbmMqJmsMessagePublisher}
 *       (via {@code ObjectProvider}) so outbound MQ messages carry the
 *       trace context forward.</li>
 * </ul>
 *
 * <p>The {@link Resource} captures {@code service.name},
 * {@code service.namespace}, and {@code deployment.environment} so the
 * collector can route + dashboard traces without hand-coded fan-out.
 * The {@link Sampler#parentBasedTraceIdRatio(double)} sampler honors an
 * upstream sampling decision (the W3C {@code traceparent} sampled bit) and
 * applies the configured ratio only to spans this service roots.
 */
@Configuration
@EnableConfigurationProperties(TelemetryProperties.class)
public class OpenTelemetryConfig {

  // Resource attribute keys come from the OpenTelemetry semantic-conventions
  // spec; we declare them here to avoid pulling the (alpha) semconv jar onto
  // the classpath just for two string constants. Stable conventions whose
  // names will not change in 1.x.
  private static final AttributeKey<String> SERVICE_NAME =
      AttributeKey.stringKey("service.name");
  private static final AttributeKey<String> SERVICE_NAMESPACE =
      AttributeKey.stringKey("service.namespace");
  private static final AttributeKey<String> DEPLOYMENT_ENVIRONMENT =
      AttributeKey.stringKey("deployment.environment");

  /**
   * Root {@link OpenTelemetry} bean. Either a fully-wired SDK with the
   * OTLP gRPC exporter, the parent-based ratio sampler, and the configured
   * resource — or the no-op instance when telemetry is disabled. The
   * decision is made once at startup so downstream beans never have to
   * branch on a feature flag at runtime.
   */
  @Bean
  public OpenTelemetry openTelemetry(TelemetryProperties props) {
    if (!props.isEnabled()) {
      return OpenTelemetry.noop();
    }

    Resource resource = Resource.getDefault().merge(
        Resource.create(Attributes.of(
            SERVICE_NAME, props.getServiceName(),
            SERVICE_NAMESPACE, "bank",
            DEPLOYMENT_ENVIRONMENT, props.getEnvironment())));

    OtlpGrpcSpanExporter exporter = OtlpGrpcSpanExporter.builder()
        .setEndpoint(props.getOtlpEndpoint())
        .setTimeout(props.getOtlpTimeout())
        .build();

    SpanProcessor processor = BatchSpanProcessor.builder(exporter)
        .setMaxQueueSize(props.getMaxQueueSize())
        .setScheduleDelay(props.getScheduleDelay())
        .build();

    SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
        .setResource(resource)
        .setSampler(Sampler.parentBased(
            Sampler.traceIdRatioBased(clampRatio(props.getSamplingRatio()))))
        .addSpanProcessor(processor)
        .build();

    TextMapPropagator propagator =
        io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator.getInstance();

    return OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setPropagators(ContextPropagators.create(propagator))
        .build();
  }

  /**
   * Convenience {@link Tracer} bean keyed under the application
   * instrumentation scope. Beans that need raw SDK access can
   * {@code @Autowired Tracer tracer} instead of going through
   * {@link DomainTracer}.
   */
  @Bean
  public Tracer tracer(OpenTelemetry openTelemetry) {
    return openTelemetry.getTracer("com.example.infrastructure.telemetry");
  }

  /** Domain-service span helper. */
  @Bean
  public DomainTracer domainTracer(Tracer tracer) {
    return new DomainTracer(tracer);
  }

  /**
   * Servlet filter that extracts inbound trace context. Spring Boot
   * discovers {@code jakarta.servlet.Filter} beans automatically; we do
   * not need to register a {@code FilterRegistrationBean}.
   */
  @Bean
  public HttpTraceContextFilter httpTraceContextFilter(OpenTelemetry openTelemetry) {
    return new HttpTraceContextFilter(openTelemetry);
  }

  /**
   * JMS message context propagator used by the IBM MQ adapter when MQ is
   * enabled. Pulled in by
   * {@link com.example.infrastructure.jms.IbmMqJmsConfig} via
   * {@code ObjectProvider} so tests that load only IbmMqJmsConfig
   * (without telemetry on the classpath) still work.
   */
  @Bean
  public JmsTraceContextPropagator jmsTraceContextPropagator(OpenTelemetry openTelemetry) {
    return new JmsTraceContextPropagator(openTelemetry);
  }

  /**
   * The sampler API takes any double but only {@code [0.0, 1.0]} is
   * meaningful. Misconfigured operators sometimes set 1.5 expecting
   * "always sample"; clamp here so the SDK does not refuse to start.
   */
  private static double clampRatio(double ratio) {
    if (ratio < 0.0) {
      return 0.0;
    }
    if (ratio > 1.0) {
      return 1.0;
    }
    return ratio;
  }
}
