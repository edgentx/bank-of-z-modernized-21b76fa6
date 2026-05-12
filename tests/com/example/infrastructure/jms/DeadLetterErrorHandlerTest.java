package com.example.infrastructure.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.jms.Destination;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * BANK S-34 — {@link DeadLetterErrorHandler} routing tests.
 *
 * <p>Validate that:
 * <ul>
 *   <li>A listener failure that carries the offending {@link
 *       jakarta.jms.Message} via
 *       {@link DeadLetterErrorHandler.MessageProcessingFailure} is routed to
 *       the configured DLQ with the original body, message-id, destination,
 *       and a human-readable failure reason attached.</li>
 *   <li>A failure that does NOT carry a message reference is logged (and
 *       does NOT touch the DLQ JmsTemplate at all).</li>
 *   <li>A broker outage during the DLQ write does not propagate or mask
 *       the original error — the source-queue redelivery loop is the
 *       fallback.</li>
 * </ul>
 */
class DeadLetterErrorHandlerTest {

  private final JmsTemplate jmsTemplate = mock(JmsTemplate.class);
  private final DeadLetterErrorHandler handler = new DeadLetterErrorHandler(jmsTemplate, "BANK.DLQ");

  @Test
  void routesFailedMessageToConfiguredDeadLetterQueue() throws Exception {
    TextMessage failedMessage = mock(TextMessage.class);
    when(failedMessage.getText()).thenReturn("{\"aggregateId\":\"acct-7\"}");
    when(failedMessage.getJMSMessageID()).thenReturn("ID:msg-1");
    Destination originalDestination = mock(Destination.class);
    when(originalDestination.toString()).thenReturn("queue:///BANK.ACCT.EVT.Q");
    when(failedMessage.getJMSDestination()).thenReturn(originalDestination);

    RuntimeException listenerError =
        DeadLetterErrorHandler.wrap(new IllegalStateException("boom"), failedMessage);

    // The MessageCreator the handler builds runs inside JmsTemplate.send,
    // so we use doAnswer to fire it against a CapturingTextMessage and assert
    // the DLQ-side message contents.
    DomainEventMessageConverterTest.CapturingTextMessage dlqMessage =
        new DomainEventMessageConverterTest.CapturingTextMessage();
    Session session = mock(Session.class);
    when(session.createTextMessage(any(String.class))).thenAnswer(inv -> {
      dlqMessage.setText(inv.getArgument(0));
      return dlqMessage;
    });

    doAnswer(inv -> {
      MessageCreator creator = inv.getArgument(1);
      creator.createMessage(session);
      return null;
    }).when(jmsTemplate).send(eq("BANK.DLQ"), any(MessageCreator.class));

    handler.handleError(listenerError);

    verify(jmsTemplate).send(eq("BANK.DLQ"), any(MessageCreator.class));
    assertEquals("{\"aggregateId\":\"acct-7\"}", dlqMessage.getText());
    assertEquals("ID:msg-1",
        dlqMessage.stringProperties.get(DeadLetterErrorHandler.PROP_DLQ_ORIGINAL_MESSAGE_ID));
    assertEquals("queue:///BANK.ACCT.EVT.Q",
        dlqMessage.stringProperties.get(DeadLetterErrorHandler.PROP_DLQ_ORIGINAL_DESTINATION));
    String reason = dlqMessage.stringProperties.get(DeadLetterErrorHandler.PROP_DLQ_REASON);
    assertNotNull(reason);
    assertEquals("boom", reason);
  }

  @Test
  void logsButDoesNotTouchDlqWhenErrorHasNoMessageReference() {
    // Connection-level errors before delivery never carry a Message —
    // there is nothing to route, so the DLQ JmsTemplate must not be invoked.
    handler.handleError(new IllegalStateException("connection refused"));

    verifyNoInteractions(jmsTemplate);
  }

  @Test
  void swallowsDlqWriteFailureSoSourceQueueRedeliveryRemainsTheFallback() {
    TextMessage failedMessage = mock(TextMessage.class);
    RuntimeException listenerError =
        DeadLetterErrorHandler.wrap(new IllegalStateException("boom"), failedMessage);
    doThrow(new UncategorizedJmsException("DLQ broker down"))
        .when(jmsTemplate).send(eq("BANK.DLQ"), any(MessageCreator.class));

    // Must not propagate — the source-queue redelivery loop is the safety net
    // and bubbling here would crash the listener-container thread.
    handler.handleError(listenerError);
  }

  @Test
  void wrapPreservesFailedMessageOnExceptionChain() {
    TextMessage failedMessage = mock(TextMessage.class);

    RuntimeException wrapped =
        DeadLetterErrorHandler.wrap(new RuntimeException("boom"), failedMessage);

    assertNotNull(wrapped.getCause());
    DeadLetterErrorHandler.FailedMessageCarrier carrier =
        (DeadLetterErrorHandler.FailedMessageCarrier) wrapped;
    assertEquals(failedMessage, carrier.failedMessage());
  }
}
