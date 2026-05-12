package com.example.infrastructure.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.domain.shared.DomainEvent;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.jms.support.converter.MessageConversionException;

/**
 * BANK S-34 — {@link DomainEventMessageConverter} round-trip tests.
 *
 * <p>The converter is the wire-format authority: a regression here ripples
 * silently through every listener. The tests therefore lock down:
 * <ul>
 *   <li>domain-event → JSON TextMessage with the three required JMS
 *       string-properties ({@code eventType}, {@code aggregateId},
 *       {@code occurredAt}) plus the JMS-native {@code JMSType};</li>
 *   <li>String pass-through (legacy bridge case);</li>
 *   <li>arbitrary POJO → JSON;</li>
 *   <li>fromMessage parses TextMessage JSON into Map and refuses non-text
 *       JMS messages cleanly;</li>
 *   <li>applyHeaders attaches caller-supplied string-properties and skips
 *       null entries gracefully.</li>
 * </ul>
 */
class DomainEventMessageConverterTest {

  private final DomainEventMessageConverter converter = new DomainEventMessageConverter();

  // ---------------------------------------------------------------------------
  // toMessage
  // ---------------------------------------------------------------------------

  @Test
  void toMessageSerializesDomainEventAsJsonAndCopiesPropertiesOntoJmsMessage()
      throws JMSException {
    CapturingTextMessage captured = new CapturingTextMessage();
    Session session = mock(Session.class);
    when(session.createTextMessage(anyString())).thenAnswer(inv -> {
      captured.setText(inv.getArgument(0));
      return captured;
    });
    Instant when = Instant.parse("2026-05-12T12:00:00Z");
    SampleEvent event = new SampleEvent("acct-1", "USD", 100, when);

    Message message = converter.toMessage(event, session);

    assertTrue(message instanceof TextMessage, "expected a TextMessage");
    TextMessage text = (TextMessage) message;
    assertTrue(text.getText().contains("\"aggregateId\":\"acct-1\""),
        "body should include the event payload as JSON");
    assertEquals("account.opened",
        captured.stringProperties.get(DomainEventMessageConverter.PROP_EVENT_TYPE));
    assertEquals("acct-1",
        captured.stringProperties.get(DomainEventMessageConverter.PROP_AGGREGATE_ID));
    assertEquals("2026-05-12T12:00:00Z",
        captured.stringProperties.get(DomainEventMessageConverter.PROP_OCCURRED_AT));
    assertEquals("account.opened", captured.jmsType,
        "JMSType should mirror eventType so MQ-side selectors can filter without parsing");
  }

  @Test
  void toMessagePassesStringPayloadThroughAsTextMessageWithoutProperties()
      throws JMSException {
    CapturingTextMessage captured = new CapturingTextMessage();
    Session session = mock(Session.class);
    when(session.createTextMessage(anyString())).thenAnswer(inv -> {
      captured.setText(inv.getArgument(0));
      return captured;
    });

    Message message = converter.toMessage("plain wire payload", session);

    assertEquals("plain wire payload", ((TextMessage) message).getText());
    assertTrue(captured.stringProperties.isEmpty(),
        "no eventType/aggregateId for a non-DomainEvent payload");
    assertNull(captured.jmsType);
  }

  @Test
  void toMessageSerializesArbitraryPojoAsJson() throws JMSException {
    CapturingTextMessage captured = new CapturingTextMessage();
    Session session = mock(Session.class);
    when(session.createTextMessage(anyString())).thenAnswer(inv -> {
      captured.setText(inv.getArgument(0));
      return captured;
    });
    SampleLegacyPayload payload = new SampleLegacyPayload("DEPOSIT", 42);

    Message message = converter.toMessage(payload, session);

    String body = ((TextMessage) message).getText();
    assertTrue(body.contains("\"kind\":\"DEPOSIT\""));
    assertTrue(body.contains("\"amount\":42"));
  }

  @Test
  void toMessageRejectsNullPayload() {
    Session session = mock(Session.class);
    assertThrows(MessageConversionException.class,
        () -> converter.toMessage(null, session));
  }

  // ---------------------------------------------------------------------------
  // fromMessage
  // ---------------------------------------------------------------------------

  @Test
  void fromMessageParsesTextMessageJsonIntoMap() throws JMSException {
    TextMessage message = mock(TextMessage.class);
    when(message.getText()).thenReturn("{\"aggregateId\":\"acct-9\",\"currency\":\"EUR\"}");

    Object result = converter.fromMessage(message);

    assertTrue(result instanceof Map);
    Map<?, ?> body = (Map<?, ?>) result;
    assertEquals("acct-9", body.get("aggregateId"));
    assertEquals("EUR", body.get("currency"));
  }

  @Test
  void fromMessageRejectsNonTextMessage() {
    Message message = mock(Message.class);

    assertThrows(MessageConversionException.class, () -> converter.fromMessage(message));
  }

  @Test
  void fromMessageReturnsEmptyMapForBlankBody() throws JMSException {
    TextMessage message = mock(TextMessage.class);
    when(message.getText()).thenReturn("");

    Object result = converter.fromMessage(message);

    assertTrue(result instanceof Map && ((Map<?, ?>) result).isEmpty());
  }

  // ---------------------------------------------------------------------------
  // applyHeaders
  // ---------------------------------------------------------------------------

  @Test
  void applyHeadersAttachesCallerSuppliedProperties() throws JMSException {
    Message message = mock(Message.class);
    Map<String, String> headers = new HashMap<>();
    headers.put("x-cics-region", "PROD01");
    headers.put("x-trace-id", "abc-123");
    headers.put(null, "skipped-null-key");
    headers.put("x-null-value", null);

    DomainEventMessageConverter.applyHeaders(message, headers);

    verify(message).setStringProperty("x-cics-region", "PROD01");
    verify(message).setStringProperty("x-trace-id", "abc-123");
    verify(message, never()).setStringProperty(anyString(), eq("skipped-null-key"));
  }

  @Test
  void applyHeadersIsNoopForNullOrEmptyHeaders() throws JMSException {
    Message message = mock(Message.class);

    DomainEventMessageConverter.applyHeaders(message, null);
    DomainEventMessageConverter.applyHeaders(message, Map.of());

    verifyNoInteractions(message);
  }

  // ---------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------

  /**
   * Minimal {@link TextMessage} stand-in that records the converter's
   * {@code setText}, {@code setJMSType}, and {@code setStringProperty}
   * calls so the test can assert on them without depending on the full
   * Jakarta JMS API surface (which a hand-rolled implements drags in).
   */
  static final class CapturingTextMessage implements TextMessage {

    final Map<String, String> stringProperties = new HashMap<>();
    final Set<String> propertyNames = new HashSet<>();
    String text;
    String jmsType;

    @Override public String getText() { return text; }
    @Override public void setText(String text) { this.text = text; }
    @Override public void setJMSType(String type) { this.jmsType = type; }
    @Override public String getJMSType() { return jmsType; }
    @Override public void setStringProperty(String name, String value) {
      stringProperties.put(name, value);
      propertyNames.add(name);
    }
    @Override public String getStringProperty(String name) { return stringProperties.get(name); }
    @Override public boolean propertyExists(String name) { return propertyNames.contains(name); }
    @Override public java.util.Enumeration<String> getPropertyNames() {
      return java.util.Collections.enumeration(propertyNames);
    }
    @Override public void clearProperties() { stringProperties.clear(); propertyNames.clear(); }

    // ---- delegating no-op stubs for the rest of TextMessage (unused) ----
    @Override public String getJMSMessageID() { return null; }
    @Override public void setJMSMessageID(String id) {}
    @Override public long getJMSTimestamp() { return 0; }
    @Override public void setJMSTimestamp(long t) {}
    @Override public byte[] getJMSCorrelationIDAsBytes() { return new byte[0]; }
    @Override public void setJMSCorrelationIDAsBytes(byte[] c) {}
    @Override public void setJMSCorrelationID(String c) {}
    @Override public String getJMSCorrelationID() { return null; }
    @Override public jakarta.jms.Destination getJMSReplyTo() { return null; }
    @Override public void setJMSReplyTo(jakarta.jms.Destination d) {}
    @Override public jakarta.jms.Destination getJMSDestination() { return null; }
    @Override public void setJMSDestination(jakarta.jms.Destination d) {}
    @Override public int getJMSDeliveryMode() { return 0; }
    @Override public void setJMSDeliveryMode(int m) {}
    @Override public boolean getJMSRedelivered() { return false; }
    @Override public void setJMSRedelivered(boolean r) {}
    @Override public long getJMSExpiration() { return 0; }
    @Override public void setJMSExpiration(long e) {}
    @Override public long getJMSDeliveryTime() { return 0; }
    @Override public void setJMSDeliveryTime(long d) {}
    @Override public int getJMSPriority() { return 0; }
    @Override public void setJMSPriority(int p) {}
    @Override public boolean getBooleanProperty(String name) { return false; }
    @Override public byte getByteProperty(String name) { return 0; }
    @Override public short getShortProperty(String name) { return 0; }
    @Override public int getIntProperty(String name) { return 0; }
    @Override public long getLongProperty(String name) { return 0; }
    @Override public float getFloatProperty(String name) { return 0; }
    @Override public double getDoubleProperty(String name) { return 0; }
    @Override public Object getObjectProperty(String name) { return stringProperties.get(name); }
    @Override public void setBooleanProperty(String name, boolean value) {}
    @Override public void setByteProperty(String name, byte value) {}
    @Override public void setShortProperty(String name, short value) {}
    @Override public void setIntProperty(String name, int value) {}
    @Override public void setLongProperty(String name, long value) {}
    @Override public void setFloatProperty(String name, float value) {}
    @Override public void setDoubleProperty(String name, double value) {}
    @Override public void setObjectProperty(String name, Object value) {}
    @Override public void acknowledge() {}
    @Override public void clearBody() { this.text = null; }
    @Override public <T> T getBody(Class<T> c) { return c.cast(text); }
    @Override public boolean isBodyAssignableTo(Class c) { return c.isAssignableFrom(String.class); }
  }

  /** Tiny domain event used to drive the converter through the event branch. */
  record SampleEvent(String aggregateId, String currency, int amount, Instant occurredAt)
      implements DomainEvent {
    @Override public String type() { return "account.opened"; }
  }

  /** Tiny POJO used to drive the converter through the generic-JSON branch. */
  record SampleLegacyPayload(String kind, int amount) {}
}
