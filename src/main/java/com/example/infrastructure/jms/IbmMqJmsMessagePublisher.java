package com.example.infrastructure.jms;

import com.example.domain.shared.DomainEvent;
import com.example.infrastructure.telemetry.JmsTraceContextPropagator;
import com.example.ports.MessagePublisherPort;
import com.example.ports.MessagingException;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.Map;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

/**
 * BANK S-34 — IBM MQ / JMS implementation of {@link MessagePublisherPort}.
 *
 * <p>The adapter is a thin translation layer over {@link JmsTemplate}:
 *
 * <ul>
 *   <li>resolves the caller's logical destination through
 *       {@link IbmMqJmsProperties#resolveDestination(String)} so application
 *       code never references the wire-level MQ queue names;</li>
 *   <li>delegates serialization to the {@link MessageConverter} configured
 *       on the template ({@link DomainEventMessageConverter}) so the JMS
 *       message body and properties stay consistent with what the listener
 *       containers consume;</li>
 *   <li>applies the caller-supplied JMS-property headers for the
 *       legacy-bridge {@code send(payload, headers)} escape hatch, by
 *       intercepting the template's post-conversion {@link Message} via
 *       {@code convertAndSend}'s {@code MessagePostProcessor} hook;</li>
 *   <li>maps every Spring {@link JmsException} to a port-level
 *       {@link MessagingException} so application code does not import
 *       {@code org.springframework.jms.*}.</li>
 * </ul>
 *
 * <p>The publisher respects whatever {@link org.springframework.jms.connection.JmsTransactionManager}
 * transaction context the caller is in — when invoked inside a
 * {@code @Transactional} boundary backed by the JMS transaction manager
 * the send is only made visible on commit. Outside a transaction the
 * template performs an auto-acknowledged send.
 */
public class IbmMqJmsMessagePublisher implements MessagePublisherPort {

  private final JmsTemplate jmsTemplate;
  private final IbmMqJmsProperties props;
  /**
   * S-35: every outbound JMS publish has the current OpenTelemetry trace
   * context injected onto the message's string-property bag so the
   * downstream listener can rejoin the same trace. Resolved from an
   * {@link org.springframework.beans.factory.ObjectProvider} in
   * {@link IbmMqJmsConfig#ibmMqJmsMessagePublisher} so test contexts that
   * load only the JMS config (without telemetry) get a no-op propagator
   * instance — the publisher itself never has to branch on a feature flag.
   */
  private final JmsTraceContextPropagator tracePropagator;

  public IbmMqJmsMessagePublisher(JmsTemplate jmsTemplate, IbmMqJmsProperties props,
      JmsTraceContextPropagator tracePropagator) {
    this.jmsTemplate = jmsTemplate;
    this.props = props;
    this.tracePropagator = tracePropagator;
  }

  @Override
  public void publish(String destination, DomainEvent event) {
    if (destination == null || destination.isBlank()) {
      throw new MessagingException("destination must not be blank");
    }
    if (event == null) {
      throw new MessagingException("event must not be null");
    }
    String wire = props.resolveDestination(destination);
    try {
      jmsTemplate.convertAndSend(wire, event, message -> {
        tracePropagator.inject(message);
        return message;
      });
    } catch (JmsException e) {
      throw new MessagingException("Failed to publish " + event.type()
          + " to " + wire + ": " + e.getMessage(), e);
    }
  }

  @Override
  public void send(String destination, Object payload, Map<String, String> headers) {
    if (destination == null || destination.isBlank()) {
      throw new MessagingException("destination must not be blank");
    }
    if (payload == null) {
      throw new MessagingException("payload must not be null");
    }
    String wire = props.resolveDestination(destination);
    try {
      jmsTemplate.convertAndSend(wire, payload, message -> {
        try {
          DomainEventMessageConverter.applyHeaders(message, headers);
        } catch (JMSException jms) {
          throw new MessagingException(
              "Failed to apply JMS headers on send to " + wire + ": " + jms.getMessage(), jms);
        }
        tracePropagator.inject(message);
        return message;
      });
    } catch (JmsException e) {
      throw new MessagingException("Failed to send payload to " + wire
          + ": " + e.getMessage(), e);
    }
  }
}
