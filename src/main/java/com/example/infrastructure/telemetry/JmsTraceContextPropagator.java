package com.example.infrastructure.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BANK S-35 — JMS-side context propagator.
 *
 * <p>Companion to {@link HttpTraceContextFilter} for the asynchronous half
 * of the acceptance criterion "Context propagation works across HTTP and
 * JMS boundaries". The configured OpenTelemetry propagator (W3C trace-context
 * by default) is dialect-agnostic — what changes per protocol is the
 * <em>carrier</em>. For JMS the carrier is the {@link Message}'s
 * string-property bag:
 *
 * <ul>
 *   <li>{@link #inject(Message)} writes the current trace context onto
 *       outbound messages via {@code message.setStringProperty(key, value)}.
 *       Invoked from {@link com.example.infrastructure.jms.IbmMqJmsMessagePublisher}
 *       inside the publisher's {@code MessagePostProcessor} so every JMS
 *       publish — typed-event or raw-payload — carries the trace forward
 *       to whichever async consumer picks the message up;</li>
 *   <li>{@link #extract(Message)} pulls the trace context back out on the
 *       consumer side and returns a {@link Context} the listener can
 *       {@link Context#makeCurrent() make current} before invoking the
 *       application handler. JMS string properties survive IBM MQ's MQRFH2
 *       header on the wire so the propagation is opaque to mainframe
 *       consumers that ignore the extra keys.</li>
 * </ul>
 *
 * <p>JMS property names are constrained to identifier-style strings — they
 * cannot contain {@code -} or {@code .}. The W3C trace-context spec uses
 * {@code traceparent} and {@code tracestate} which are already
 * identifier-safe so no escaping is needed; if a different propagator
 * (B3 multi-header, Jaeger) is configured at runtime the keys it emits
 * (e.g. {@code X-B3-TraceId} on HTTP, {@code b3} on single-header)
 * become {@code X_B3_TraceId} when written to the JMS property bag below.
 * This is intentional: the same propagator decodes both directions so
 * keys round-trip cleanly within the application; consumers using a
 * different propagator family would need their own mapping.
 *
 * <p>When the underlying {@link OpenTelemetry} bean is the no-op instance
 * (telemetry disabled), both methods become cheap no-ops: the propagator
 * emits no keys on inject, the carrier's {@link TextMapGetter#keys
 * keys()} iteration runs but finds nothing on extract, and the returned
 * context is whatever was already current.
 */
public class JmsTraceContextPropagator {

  /**
   * Carrier setter that writes propagation keys onto outbound JMS
   * messages. {@link Message#setStringProperty(String, String)} throws
   * {@link JMSException}; we surface that as an unchecked exception so
   * the propagator API is callable from a {@code MessagePostProcessor}
   * lambda without forcing every publisher to declare the checked
   * exception in its signature.
   */
  private static final TextMapSetter<Message> SETTER = (carrier, key, value) -> {
    if (carrier == null) {
      return;
    }
    try {
      carrier.setStringProperty(sanitize(key), value);
    } catch (JMSException e) {
      throw new TraceContextInjectionException(
          "Failed to inject trace-context key " + key + " on JMS message: " + e.getMessage(), e);
    }
  };

  /**
   * Carrier getter that reads propagation keys back off inbound JMS
   * messages. Unlike HTTP headers, JMS string-property enumeration
   * returns an {@link java.util.Enumeration}; we materialize it into a
   * {@code List} once so the SDK can iterate it multiple times.
   */
  private static final TextMapGetter<Message> GETTER = new TextMapGetter<>() {
    @Override
    public Iterable<String> keys(Message carrier) {
      if (carrier == null) {
        return Collections.emptyList();
      }
      try {
        List<String> out = new ArrayList<>();
        java.util.Enumeration<?> e = carrier.getPropertyNames();
        while (e.hasMoreElements()) {
          out.add(String.valueOf(e.nextElement()));
        }
        return out;
      } catch (JMSException e) {
        // Failing to enumerate property names is non-fatal for tracing —
        // we degrade to "no extracted context" rather than blow up the
        // listener.
        return Collections.emptyList();
      }
    }

    @Override
    public String get(Message carrier, String key) {
      if (carrier == null) {
        return null;
      }
      try {
        return carrier.getStringProperty(sanitize(key));
      } catch (JMSException e) {
        return null;
      }
    }
  };

  private final OpenTelemetry openTelemetry;

  public JmsTraceContextPropagator(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  /** Inject the current trace context onto an outbound JMS message. */
  public void inject(Message message) {
    if (message == null) {
      return;
    }
    openTelemetry.getPropagators()
        .getTextMapPropagator()
        .inject(Context.current(), message, SETTER);
  }

  /**
   * Extract trace context from an inbound JMS message, layering it on top
   * of the current context. The returned {@link Context} should be
   * {@link Context#makeCurrent() made current} by the listener wrapper
   * before invoking the application handler.
   */
  public Context extract(Message message) {
    return openTelemetry.getPropagators()
        .getTextMapPropagator()
        .extract(Context.current(), message, GETTER);
  }

  /**
   * Replace characters that are illegal in JMS string-property names
   * ({@code .} and {@code -}) with underscore so propagation keys that
   * are valid in HTTP headers round-trip cleanly through JMS without
   * breaking the {@link Message#setStringProperty} validity rule. W3C
   * trace-context's own {@code traceparent}/{@code tracestate} keys are
   * already identifier-safe so this is only relevant for other propagator
   * families.
   */
  private static String sanitize(String key) {
    if (key == null) {
      return null;
    }
    StringBuilder out = new StringBuilder(key.length());
    for (int i = 0; i < key.length(); i++) {
      char c = key.charAt(i);
      out.append(c == '.' || c == '-' ? '_' : c);
    }
    return out.toString();
  }

  /**
   * Unchecked exception used so the {@link TextMapSetter} lambda can
   * surface a JMS failure without declaring the checked
   * {@link JMSException} on the propagator API.
   */
  public static final class TraceContextInjectionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public TraceContextInjectionException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
