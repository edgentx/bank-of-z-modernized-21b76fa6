package com.example.infrastructure.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * BANK S-35 — {@link JmsTraceContextPropagator} inject/extract round-trip.
 *
 * <p>Acceptance criterion "Context propagation works across HTTP and JMS
 * boundaries" requires that a span started on the publisher side surfaces
 * the same trace id on the consumer side after a JMS hop. We model that
 * here with two SDK instances (publisher + consumer) sharing the same
 * W3C propagator — inject onto a recording message on one side, extract
 * the context out on the other, and assert the trace id round-trips.
 */
class JmsTraceContextPropagatorTest {

  @Test
  void injectsW3CTraceparentOntoMessageProperty() {
    OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
        .setTracerProvider(SdkTracerProvider.builder().build())
        .setPropagators(ContextPropagators.create(
            io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator.getInstance()))
        .build();
    JmsTraceContextPropagator propagator = new JmsTraceContextPropagator(sdk);

    Span span = sdk.getTracer("test").spanBuilder("publish").startSpan();
    RecordingMessage message = new RecordingMessage();
    try (Scope ignored = span.makeCurrent()) {
      propagator.inject(message);
    } finally {
      span.end();
    }

    // W3C trace-context spec: traceparent header is mandatory and looks like
    // "00-<traceId-32hex>-<spanId-16hex>-<flags>". Tracestate is optional and
    // only present when set. Both should land in the JMS string-property bag.
    String traceparent = message.props.get("traceparent");
    assertNotNull(traceparent,
        "publisher-side inject must write the W3C traceparent property so " +
            "the consumer can extract it");
    assertEquals(span.getSpanContext().getTraceId(),
        // traceparent layout: "00-<traceId>-<spanId>-<flags>"
        traceparent.split("-")[1],
        "the trace id inside the traceparent value must match the publisher's " +
            "active span — otherwise consumer-side traces are disconnected " +
            "from the publisher trace");
  }

  @Test
  void extractRestoresContextFromInboundMessageProperties() {
    OpenTelemetrySdk publisherSdk = OpenTelemetrySdk.builder()
        .setTracerProvider(SdkTracerProvider.builder().build())
        .setPropagators(ContextPropagators.create(
            io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator.getInstance()))
        .build();
    OpenTelemetrySdk consumerSdk = OpenTelemetrySdk.builder()
        .setTracerProvider(SdkTracerProvider.builder().build())
        .setPropagators(ContextPropagators.create(
            io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator.getInstance()))
        .build();

    // Publisher side — inject the current span's context onto a message.
    Span publisherSpan = publisherSdk.getTracer("publisher").spanBuilder("send").startSpan();
    RecordingMessage onWire = new RecordingMessage();
    try (Scope ignored = publisherSpan.makeCurrent()) {
      new JmsTraceContextPropagator(publisherSdk).inject(onWire);
    } finally {
      publisherSpan.end();
    }

    // Consumer side — extract the trace context off the inbound message and
    // verify it carries the publisher's trace id forward.
    Context extracted = new JmsTraceContextPropagator(consumerSdk).extract(onWire);
    assertEquals(publisherSpan.getSpanContext().getTraceId(),
        Span.fromContext(extracted).getSpanContext().getTraceId(),
        "consumer-side extracted context must reference the publisher's " +
            "trace id — that is the entire point of cross-boundary propagation");
  }

  @Test
  void noopPropagatorDoesNotEmitKeys() {
    JmsTraceContextPropagator noop = new JmsTraceContextPropagator(OpenTelemetry.noop());
    RecordingMessage message = new RecordingMessage();

    noop.inject(message);

    // OpenTelemetry.noop() returns a no-op propagator; the spec is that it
    // emits no keys and consumer-side extract returns the current context
    // unchanged. The publisher side must not pollute the message property
    // bag when telemetry is disabled.
    assertEquals(0, message.props.size(),
        "no-op propagator must leave the outbound message unchanged " +
            "so telemetry-disabled deployments do not waste broker space");
  }

  @Test
  void extractFromNullMessageReturnsCurrentContext() {
    JmsTraceContextPropagator p = new JmsTraceContextPropagator(OpenTelemetry.noop());
    assertSame(Context.current(), p.extract(null),
        "null-safe extract path so listener wrappers that race against " +
            "a closed session do not NPE during shutdown");
  }

  @Test
  void injectOnNullMessageIsNoOp() {
    JmsTraceContextPropagator p = new JmsTraceContextPropagator(OpenTelemetry.noop());
    p.inject(null);
    // no exception thrown == pass — null-safe inject lets the publisher
    // skip context injection on an already-closed message without forcing
    // a defensive null-check at every call site.
  }

  /**
   * Minimal recording {@link Message} that captures the
   * {@code setStringProperty} / {@code getStringProperty} pair the
   * propagator drives. Implements only the methods the propagator needs;
   * every other JMS API throws {@link UnsupportedOperationException} so
   * accidental over-reach during refactors is loud.
   */
  static final class RecordingMessage implements Message {
    final Map<String, String> props = new HashMap<>();

    @Override public void setStringProperty(String name, String value) { props.put(name, value); }
    @Override public String getStringProperty(String name) { return props.get(name); }

    @Override public Enumeration<?> getPropertyNames() {
      return java.util.Collections.enumeration(props.keySet());
    }

    // ---- unused methods — throw if anyone exercises them by accident ----
    @Override public String getJMSMessageID() { throw u(); }
    @Override public void setJMSMessageID(String id) { throw u(); }
    @Override public long getJMSTimestamp() { throw u(); }
    @Override public void setJMSTimestamp(long timestamp) { throw u(); }
    @Override public byte[] getJMSCorrelationIDAsBytes() { throw u(); }
    @Override public void setJMSCorrelationIDAsBytes(byte[] correlationID) { throw u(); }
    @Override public void setJMSCorrelationID(String correlationID) { throw u(); }
    @Override public String getJMSCorrelationID() { throw u(); }
    @Override public jakarta.jms.Destination getJMSReplyTo() { throw u(); }
    @Override public void setJMSReplyTo(jakarta.jms.Destination replyTo) { throw u(); }
    @Override public jakarta.jms.Destination getJMSDestination() { throw u(); }
    @Override public void setJMSDestination(jakarta.jms.Destination destination) { throw u(); }
    @Override public int getJMSDeliveryMode() { throw u(); }
    @Override public void setJMSDeliveryMode(int deliveryMode) { throw u(); }
    @Override public boolean getJMSRedelivered() { throw u(); }
    @Override public void setJMSRedelivered(boolean redelivered) { throw u(); }
    @Override public String getJMSType() { throw u(); }
    @Override public void setJMSType(String type) { throw u(); }
    @Override public long getJMSExpiration() { throw u(); }
    @Override public void setJMSExpiration(long expiration) { throw u(); }
    @Override public long getJMSDeliveryTime() { throw u(); }
    @Override public void setJMSDeliveryTime(long deliveryTime) { throw u(); }
    @Override public int getJMSPriority() { throw u(); }
    @Override public void setJMSPriority(int priority) { throw u(); }
    @Override public void clearProperties() { props.clear(); }
    @Override public boolean propertyExists(String name) { return props.containsKey(name); }
    @Override public boolean getBooleanProperty(String name) { throw u(); }
    @Override public byte getByteProperty(String name) { throw u(); }
    @Override public short getShortProperty(String name) { throw u(); }
    @Override public int getIntProperty(String name) { throw u(); }
    @Override public long getLongProperty(String name) { throw u(); }
    @Override public float getFloatProperty(String name) { throw u(); }
    @Override public double getDoubleProperty(String name) { throw u(); }
    @Override public Object getObjectProperty(String name) { return props.get(name); }
    @Override public void setBooleanProperty(String name, boolean value) { throw u(); }
    @Override public void setByteProperty(String name, byte value) { throw u(); }
    @Override public void setShortProperty(String name, short value) { throw u(); }
    @Override public void setIntProperty(String name, int value) { throw u(); }
    @Override public void setLongProperty(String name, long value) { throw u(); }
    @Override public void setFloatProperty(String name, float value) { throw u(); }
    @Override public void setDoubleProperty(String name, double value) { throw u(); }
    @Override public void setObjectProperty(String name, Object value) { throw u(); }
    @Override public void acknowledge() throws JMSException { throw u(); }
    @Override public void clearBody() throws JMSException { throw u(); }
    @Override public <T> T getBody(Class<T> c) throws JMSException { throw u(); }
    @Override public boolean isBodyAssignableTo(Class c) throws JMSException { throw u(); }

    private UnsupportedOperationException u() {
      return new UnsupportedOperationException("test-only stub — extend if a new path is exercised");
    }
  }
}
