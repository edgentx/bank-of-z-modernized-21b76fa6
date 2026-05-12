package com.example.infrastructure.jms;

import com.example.domain.shared.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.util.Map;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * BANK S-34 — Spring JMS {@link MessageConverter} that bridges
 * domain events / arbitrary objects ↔ JMS {@link TextMessage} payloads.
 *
 * <p>Outbound:
 * <ul>
 *   <li>{@link DomainEvent} → JSON {@code TextMessage}; copies {@code type()},
 *       {@code aggregateId()}, and {@code occurredAt()} onto JMS string-
 *       properties so consumers (and MQ selector-based listeners) can route
 *       on event type without parsing the body.</li>
 *   <li>{@link String} → {@code TextMessage} pass-through (legacy bridge
 *       case: the payload is already the wire shape the mainframe expects).</li>
 *   <li>Anything else → Jackson-serialized JSON {@code TextMessage}.</li>
 * </ul>
 *
 * <p>Inbound: deserializes the {@code TextMessage} body as JSON into a
 * generic {@link Map} (callers that need a strongly-typed event reconstruct
 * the concrete record/POJO themselves via Jackson, keyed on the
 * {@code eventType} JMS property — the converter does not own the
 * polymorphic mapping because event types come from many bounded contexts).
 *
 * <p>Why a custom converter rather than Spring's
 * {@code MappingJackson2MessageConverter}: that converter encodes the
 * target class name into a JMS property, which couples the consumer to the
 * producer's package layout — fragile across module boundaries and a
 * deserialization-gadget surface (CVE-2017-7525-shaped). Emitting plain JSON
 * with explicit JMS properties keeps the wire contract format-only.
 */
public class DomainEventMessageConverter implements MessageConverter {

  static final String PROP_EVENT_TYPE = "eventType";
  static final String PROP_AGGREGATE_ID = "aggregateId";
  static final String PROP_OCCURRED_AT = "occurredAt";

  private final ObjectMapper mapper;

  public DomainEventMessageConverter() {
    this(defaultMapper());
  }

  /** Visible for tests that need to override Jackson configuration. */
  DomainEventMessageConverter(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  private static ObjectMapper defaultMapper() {
    ObjectMapper m = new ObjectMapper();
    m.registerModule(new JavaTimeModule());
    m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return m;
  }

  @Override
  public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
    if (object == null) {
      throw new MessageConversionException("Refusing to convert a null payload to a JMS message");
    }
    String body;
    String eventType = null;
    String aggregateId = null;
    String occurredAt = null;
    if (object instanceof DomainEvent event) {
      body = serialize(event);
      eventType = event.type();
      aggregateId = event.aggregateId();
      occurredAt = event.occurredAt() == null ? null : event.occurredAt().toString();
    } else if (object instanceof String s) {
      body = s;
    } else {
      body = serialize(object);
    }
    TextMessage message = session.createTextMessage(body);
    if (eventType != null) {
      message.setStringProperty(PROP_EVENT_TYPE, eventType);
      message.setJMSType(eventType);
    }
    if (aggregateId != null) {
      message.setStringProperty(PROP_AGGREGATE_ID, aggregateId);
    }
    if (occurredAt != null) {
      message.setStringProperty(PROP_OCCURRED_AT, occurredAt);
    }
    return message;
  }

  @Override
  public Object fromMessage(Message message) throws JMSException, MessageConversionException {
    if (!(message instanceof TextMessage text)) {
      throw new MessageConversionException(
          "Only TextMessage is supported (got " + message.getClass().getName() + ")");
    }
    String body = text.getText();
    if (body == null || body.isBlank()) {
      return Map.of();
    }
    try {
      return mapper.readValue(body, Map.class);
    } catch (Exception e) {
      throw new MessageConversionException(
          "Failed to deserialize JMS TextMessage as JSON: " + e.getMessage(), e);
    }
  }

  /**
   * Apply caller-supplied JMS string-properties to {@code message}. Visible
   * to the publisher so the {@code send(payload, headers)} escape-hatch
   * path can decorate the message without bypassing the converter.
   */
  static void applyHeaders(Message message, Map<String, String> headers) throws JMSException {
    if (headers == null || headers.isEmpty()) {
      return;
    }
    for (Map.Entry<String, String> e : headers.entrySet()) {
      if (e.getKey() == null || e.getValue() == null) {
        continue;
      }
      message.setStringProperty(e.getKey(), e.getValue());
    }
  }

  private String serialize(Object value) throws MessageConversionException {
    try {
      return mapper.writeValueAsString(value);
    } catch (Exception e) {
      throw new MessageConversionException(
          "Failed to serialize payload to JSON: " + e.getMessage(), e);
    }
  }
}
