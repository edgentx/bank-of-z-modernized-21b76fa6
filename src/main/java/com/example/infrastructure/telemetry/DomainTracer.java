package com.example.infrastructure.telemetry;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * BANK S-35 — helper for opening custom spans around domain-service
 * operations.
 *
 * <p>The acceptance criterion "Custom spans are created for domain service
 * operations" needs a small, opinionated API that application/domain code can
 * call without dragging the raw OpenTelemetry {@link Tracer} +
 * {@link Span#makeCurrent()} + try-with-resources + exception-status-mapping
 * boilerplate into every method. {@link DomainTracer} centralizes that
 * pattern:
 *
 * <pre>{@code
 *   return tracer.trace("legacy-bridge.evaluate-routing",
 *       span -> span.setAttribute(LEGACY_ROUTE_ID, cmd.routeId()),
 *       () -> applicationLogic());
 * }</pre>
 *
 * <p>The helper:
 * <ul>
 *   <li>opens a {@link SpanKind#INTERNAL} span with the caller-supplied
 *       name (operations like {@code "legacy-bridge.evaluate-routing"} stay
 *       low-cardinality; per-record IDs go on attributes, not the span
 *       name);</li>
 *   <li>scopes the new span into the current {@link io.opentelemetry.context.Context}
 *       so any nested call (HTTP egress, JMS publish, child span) sees it
 *       as the parent automatically;</li>
 *   <li>maps a thrown {@link Throwable} onto {@link StatusCode#ERROR}
 *       with the message recorded as an exception event, so failure traces
 *       show the actual cause without hand-coded {@code span.recordException}
 *       at every call site;</li>
 *   <li>always closes the span in a {@code finally} block — a leaked span
 *       blocks the exporter's queue and shows up as a never-finishing
 *       trace in the collector.</li>
 * </ul>
 *
 * <p>When the underlying {@link Tracer} is the no-op tracer (returned by
 * {@code OpenTelemetry.noop()} when {@code telemetry.otel.enabled=false}),
 * all of the above becomes cheap no-ops — the caller is unchanged.
 */
public class DomainTracer {

  private final Tracer tracer;

  public DomainTracer(Tracer tracer) {
    this.tracer = Objects.requireNonNull(tracer, "tracer");
  }

  /**
   * Open a span named {@code operationName}, run {@code body}, and record
   * the result. Exceptions from {@code body} are recorded on the span and
   * re-thrown unchanged so application error handling is not affected.
   *
   * @param operationName low-cardinality span name
   *                      (e.g. {@code "legacy-bridge.evaluate-routing"})
   * @param decorator     callback that attaches request-scoped attributes
   *                      (CICS/IMS pathway ids, route id, etc.) before the
   *                      operation runs
   * @param body          the work to execute inside the span
   * @param <T>           return type of {@code body}
   */
  public <T> T trace(String operationName, Consumer<Span> decorator, Supplier<T> body) {
    Objects.requireNonNull(operationName, "operationName");
    Objects.requireNonNull(decorator, "decorator");
    Objects.requireNonNull(body, "body");

    Span span = tracer.spanBuilder(operationName)
        .setSpanKind(SpanKind.INTERNAL)
        .startSpan();
    decorator.accept(span);
    try (Scope ignored = span.makeCurrent()) {
      T result = body.get();
      span.setStatus(StatusCode.OK);
      return result;
    } catch (RuntimeException | Error e) {
      span.recordException(e);
      span.setStatus(StatusCode.ERROR, e.getClass().getSimpleName());
      throw e;
    } finally {
      span.end();
    }
  }

  /**
   * Convenience overload for void-returning operations.
   */
  public void trace(String operationName, Consumer<Span> decorator, Runnable body) {
    trace(operationName, decorator, () -> {
      body.run();
      return null;
    });
  }

  /**
   * Convenience overload that attaches a single string attribute before
   * running {@code body}. Equivalent to calling
   * {@link #trace(String, Consumer, Supplier)} with a one-line decorator.
   */
  public <T> T trace(String operationName, AttributeKey<String> key, String value,
      Supplier<T> body) {
    return trace(operationName, span -> span.setAttribute(key, value), body);
  }

  /** Expose the underlying tracer for callers that need raw SDK access. */
  public Tracer tracer() {
    return tracer;
  }
}
