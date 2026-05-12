package com.example.infrastructure.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.domain.shared.DomainEvent;
import com.example.infrastructure.telemetry.JmsTraceContextPropagator;
import com.example.ports.MessagingException;
import io.opentelemetry.api.OpenTelemetry;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * BANK S-34 — {@link IbmMqJmsMessagePublisher} contract tests.
 *
 * <p>Drives the adapter against a mocked {@link JmsTemplate} to validate:
 * <ul>
 *   <li>logical → wire destination resolution via
 *       {@link IbmMqJmsProperties#resolveDestination(String)};</li>
 *   <li>the convert-and-send delegation path for {@link DomainEvent} sends;</li>
 *   <li>the message-post-processor hook that attaches caller headers on the
 *       {@code send(payload, headers)} escape hatch;</li>
 *   <li>null/blank input rejection;</li>
 *   <li>{@link org.springframework.jms.JmsException} → {@link MessagingException}
 *       translation so application code never imports Spring JMS exceptions.</li>
 * </ul>
 */
class IbmMqJmsMessagePublisherTest {

  private final JmsTemplate template = mock(JmsTemplate.class);
  private final IbmMqJmsProperties props = new IbmMqJmsProperties();
  /**
   * S-35: publisher always carries a {@link JmsTraceContextPropagator}; in
   * unit tests we wire it against {@code OpenTelemetry.noop()} so the
   * trace-context inject path is exercised end to end but emits no keys
   * (the property bag on the captured post-processor message therefore
   * only contains the caller-supplied headers).
   */
  private final JmsTraceContextPropagator propagator =
      new JmsTraceContextPropagator(OpenTelemetry.noop());
  private IbmMqJmsMessagePublisher publisher;

  @BeforeEach
  void setUp() {
    props.setDestinations(Map.of(
        "account.events", "BANK.ACCT.EVT.Q",
        "transaction.events", "BANK.TXN.EVT.Q"));
    publisher = new IbmMqJmsMessagePublisher(template, props, propagator);
  }

  // ---------------------------------------------------------------------------
  // publish(domain event)
  // ---------------------------------------------------------------------------

  @Test
  void publishResolvesLogicalDestinationAndDelegatesToTemplate() {
    SampleEvent event = new SampleEvent("acct-7", Instant.parse("2026-05-12T12:00:00Z"));

    publisher.publish("account.events", event);

    // S-35: publish() now goes through the 3-arg convertAndSend so the
    // outbound message can have trace-context properties injected on it
    // by the JMS propagator post-processor.
    verify(template).convertAndSend(eq("BANK.ACCT.EVT.Q"), eq(event), any(MessagePostProcessor.class));
  }

  @Test
  void publishPassesThroughUndeclaredDestinationAsWireName() {
    SampleEvent event = new SampleEvent("acct-7", Instant.parse("2026-05-12T12:00:00Z"));

    publisher.publish("ad.hoc.queue", event);

    verify(template).convertAndSend(eq("ad.hoc.queue"), eq(event), any(MessagePostProcessor.class));
  }

  @Test
  void publishRejectsBlankDestination() {
    SampleEvent event = new SampleEvent("acct-7", Instant.parse("2026-05-12T12:00:00Z"));

    MessagingException ex = assertThrows(MessagingException.class,
        () -> publisher.publish("  ", event));
    assertTrue(ex.getMessage().contains("destination"));
  }

  @Test
  void publishRejectsNullEvent() {
    assertThrows(MessagingException.class, () -> publisher.publish("account.events", null));
  }

  @Test
  void publishMapsSpringJmsExceptionToPortException() {
    SampleEvent event = new SampleEvent("acct-7", Instant.parse("2026-05-12T12:00:00Z"));
    doThrow(new UncategorizedJmsException("MQ down"))
        .when(template).convertAndSend(anyString(), any(Object.class), any(MessagePostProcessor.class));

    MessagingException ex = assertThrows(MessagingException.class,
        () -> publisher.publish("account.events", event));
    assertTrue(ex.getMessage().contains("BANK.ACCT.EVT.Q"));
    assertSame(UncategorizedJmsException.class, ex.getCause().getClass());
  }

  // ---------------------------------------------------------------------------
  // send(payload, headers)
  // ---------------------------------------------------------------------------

  @Test
  void sendDelegatesToTemplateWithHeadersAppliedViaPostProcessor() throws Exception {
    publisher.send("legacy.bridge", "RAW PAYLOAD",
        Map.of("x-cics-region", "PROD01", "x-trace-id", "abc-123"));

    org.mockito.ArgumentCaptor<MessagePostProcessor> captor =
        org.mockito.ArgumentCaptor.forClass(MessagePostProcessor.class);
    verify(template).convertAndSend(eq("legacy.bridge"), eq("RAW PAYLOAD"), captor.capture());

    // Exercise the captured post-processor against a recordable message to
    // prove headers actually land on the wire — not merely that a hook was
    // wired up.
    DomainEventMessageConverterTest.CapturingTextMessage captured =
        new DomainEventMessageConverterTest.CapturingTextMessage();
    captor.getValue().postProcessMessage(captured);
    assertEquals("PROD01", captured.stringProperties.get("x-cics-region"));
    assertEquals("abc-123", captured.stringProperties.get("x-trace-id"));
  }

  @Test
  void sendAllowsNullHeaders() throws Exception {
    publisher.send("legacy.bridge", "RAW PAYLOAD", null);

    org.mockito.ArgumentCaptor<MessagePostProcessor> captor =
        org.mockito.ArgumentCaptor.forClass(MessagePostProcessor.class);
    verify(template).convertAndSend(eq("legacy.bridge"), eq("RAW PAYLOAD"), captor.capture());

    // Null headers => post-processor is a no-op, so passing a captured
    // message through it must not throw.
    DomainEventMessageConverterTest.CapturingTextMessage captured =
        new DomainEventMessageConverterTest.CapturingTextMessage();
    captor.getValue().postProcessMessage(captured);
    assertTrue(captured.stringProperties.isEmpty());
  }

  @Test
  void sendRejectsNullPayload() {
    assertThrows(MessagingException.class,
        () -> publisher.send("legacy.bridge", null, Map.of()));
  }

  @Test
  void sendRejectsBlankDestination() {
    assertThrows(MessagingException.class,
        () -> publisher.send("", "payload", Map.of()));
  }

  // ---------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------

  record SampleEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "account.opened"; }
  }
}
