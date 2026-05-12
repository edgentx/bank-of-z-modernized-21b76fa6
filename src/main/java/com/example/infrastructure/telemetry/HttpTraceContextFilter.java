package com.example.infrastructure.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * BANK S-35 — servlet filter that extracts W3C trace context from inbound
 * HTTP requests and binds the resulting context to the current thread for
 * the duration of the request.
 *
 * <p>This is the modernized-side half of the acceptance criterion
 * "Context propagation works across HTTP and JMS boundaries":
 *
 * <ul>
 *   <li>An upstream caller (API gateway, browser, sister service) sets the
 *       W3C {@code traceparent} / {@code tracestate} headers on the
 *       request. Without this filter Spring MVC would happily process the
 *       request but the spans emitted by downstream code would be orphan
 *       roots — the trace would be cut in half at the service boundary.</li>
 *   <li>This filter calls the {@link OpenTelemetry#getPropagators()
 *       configured propagator} (W3C trace-context by default) to pull
 *       those headers into a fresh {@link Context}, opens a
 *       {@link SpanKind#SERVER} span representing the request handling,
 *       and makes that context current on the request thread.</li>
 *   <li>Any {@link DomainTracer} span opened deeper in the call stack
 *       picks the request span up as its parent automatically, and any
 *       outbound JMS publish (via {@link JmsTraceContextPropagator}) or
 *       HTTP egress carries the same trace forward to the next service.</li>
 * </ul>
 *
 * <p>The filter is registered as a Spring bean from
 * {@link OpenTelemetryConfig}; Spring Boot auto-wires it into the servlet
 * filter chain at the default highest precedence so it runs before
 * {@code @Controller}-level filters and exception handlers see the
 * request. When tracing is disabled, the configured {@link OpenTelemetry}
 * bean is {@code OpenTelemetry.noop()} and every operation below becomes a
 * cheap no-op — the filter still runs but adds no measurable overhead.
 */
public class HttpTraceContextFilter extends OncePerRequestFilter {

  private static final TextMapGetter<HttpServletRequest> GETTER =
      new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(HttpServletRequest carrier) {
          // Spring's HttpHeaders.names returns an iterator over header names;
          // we wrap it as an Iterable<String> for the SDK's contract. Null
          // request is impossible inside doFilterInternal but the SDK calls
          // keys() defensively in error paths, so we guard regardless.
          return carrier == null
              ? Collections.emptyList()
              : Collections.list(carrier.getHeaderNames());
        }

        @Override
        public String get(HttpServletRequest carrier, String key) {
          return carrier == null ? null : carrier.getHeader(key);
        }
      };

  private final OpenTelemetry openTelemetry;
  private final Tracer tracer;

  public HttpTraceContextFilter(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
    // Use the same instrumentation name as the rest of the application so
    // operators see a single library entry in the collector's scope list.
    this.tracer = openTelemetry.getTracer("com.example.infrastructure.telemetry");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    Context extracted = openTelemetry.getPropagators()
        .getTextMapPropagator()
        .extract(Context.current(), request, GETTER);

    // Span name is low-cardinality (method + path template would be ideal,
    // but Spring MVC URL templates are not resolved until handler mapping
    // runs; method + URI here is sufficient for the request envelope span,
    // and Spring-MVC-instrumentation can refine it later).
    String spanName = request.getMethod() + " " + request.getRequestURI();
    Span span = tracer.spanBuilder(spanName)
        .setSpanKind(SpanKind.SERVER)
        .setParent(extracted)
        .startSpan();
    try (Scope ignored = span.makeCurrent()) {
      chain.doFilter(request, response);
      span.setAttribute("http.status_code", response.getStatus());
    } catch (RuntimeException | ServletException | IOException e) {
      span.recordException(e);
      throw e;
    } finally {
      span.end();
    }
  }
}
