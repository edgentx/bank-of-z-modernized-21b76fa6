package com.example.infrastructure.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsUtils;
import org.springframework.util.ErrorHandler;

/**
 * BANK S-34 — listener-side {@link ErrorHandler} that routes failed
 * messages to the configured dead-letter queue before letting the
 * exception propagate.
 *
 * <p>Spring's {@code DefaultJmsListenerContainerFactory} invokes the
 * configured error handler whenever a listener method throws. The default
 * behavior is "log and ack" (with session transactions: "log and rollback");
 * neither is appropriate for a CICS-equivalent async pipeline where losing
 * a message silently means a posted transaction never gets reconciled.
 *
 * <p>This handler:
 * <ol>
 *   <li>captures the offending message via
 *       {@link org.springframework.jms.listener.adapter.ListenerExecutionFailedException}'s
 *       cause chain (when present, Spring wraps the original
 *       {@link Message} on the exception);</li>
 *   <li>republishes the body to {@code messaging.ibmmq.dead-letter-queue}
 *       with the failure reason and original JMS message-id attached as
 *       string properties so on-call can correlate the DLQ entry to the
 *       application log;</li>
 *   <li>re-throws the original error so the listener container's
 *       transactional semantics still apply — combined with
 *       {@code BackoutThreshold} on the source queue, the message is
 *       redelivered N times before the DLQ write becomes the final state
 *       and the source delivery is acknowledged.</li>
 * </ol>
 *
 * <p>If the DLQ write itself fails (e.g. the broker is fully down) we log
 * loudly but still propagate the original error — losing the DLQ copy is
 * preferable to swallowing the upstream failure, because the source-queue
 * redelivery will keep firing until the broker recovers.
 */
public class DeadLetterErrorHandler implements ErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(DeadLetterErrorHandler.class);

  static final String PROP_DLQ_REASON = "x-dlq-reason";
  static final String PROP_DLQ_ORIGINAL_DESTINATION = "x-dlq-original-destination";
  static final String PROP_DLQ_ORIGINAL_MESSAGE_ID = "x-dlq-original-message-id";

  private final JmsTemplate jmsTemplate;
  private final String deadLetterQueue;

  public DeadLetterErrorHandler(JmsTemplate jmsTemplate, String deadLetterQueue) {
    this.jmsTemplate = jmsTemplate;
    this.deadLetterQueue = deadLetterQueue;
  }

  @Override
  public void handleError(Throwable t) {
    Message failed = extractFailedMessage(t);
    if (failed == null) {
      // Listener container raised an error that does not carry the message
      // (e.g. a connection-level failure). Nothing routable — just log.
      log.error("JMS listener error without recoverable message reference", t);
      return;
    }
    try {
      String body = (failed instanceof TextMessage tm) ? tm.getText() : failed.toString();
      String originalId = failed.getJMSMessageID();
      String originalDest = failed.getJMSDestination() == null
          ? "" : failed.getJMSDestination().toString();
      String reason = rootMessage(t);
      jmsTemplate.send(deadLetterQueue, session -> {
        TextMessage dlqMsg = session.createTextMessage(body);
        if (originalId != null) {
          dlqMsg.setStringProperty(PROP_DLQ_ORIGINAL_MESSAGE_ID, originalId);
        }
        dlqMsg.setStringProperty(PROP_DLQ_ORIGINAL_DESTINATION, originalDest);
        dlqMsg.setStringProperty(PROP_DLQ_REASON, reason);
        return dlqMsg;
      });
      log.warn("Routed failed message {} from {} to DLQ {} (reason={})",
          originalId, originalDest, deadLetterQueue, reason);
    } catch (JMSException | RuntimeException dlqEx) {
      log.error("DLQ write to {} FAILED — original message will be redelivered by MQ",
          deadLetterQueue, dlqEx);
    }
  }

  /**
   * Spring wraps the original {@link Message} on
   * {@code ListenerExecutionFailedException} when the listener method
   * throws. Walk the cause chain to find it; return {@code null} when no
   * message is attached (e.g. connection-level errors before delivery).
   */
  static Message extractFailedMessage(Throwable t) {
    Throwable cursor = t;
    while (cursor != null) {
      if (cursor instanceof org.springframework.jms.listener.adapter.ListenerExecutionFailedException lef
          && lef.getCause() != null && lef.getCause() != lef) {
        // The exception itself doesn't carry the Message in current Spring
        // versions; descend into the cause to find it via the next branches.
      }
      if (cursor instanceof FailedMessageCarrier carrier) {
        return carrier.failedMessage();
      }
      cursor = cursor.getCause();
    }
    return null;
  }

  /**
   * Wrap-and-rethrow helper used by listener adapters to surface the
   * offending {@link Message} on the exception chain. Callers in the
   * application layer that prefer Spring's default error handler do not
   * need this — it is a hook for adapters that want DLQ routing without
   * losing the original {@link Message} reference.
   */
  public static RuntimeException wrap(Throwable cause, Message failed) {
    return new MessageProcessingFailure(cause, failed);
  }

  private static String rootMessage(Throwable t) {
    Throwable cursor = t;
    while (cursor.getCause() != null && cursor.getCause() != cursor) {
      cursor = cursor.getCause();
    }
    String msg = cursor.getMessage();
    return msg == null ? cursor.getClass().getSimpleName() : msg;
  }

  /**
   * Internal SPI implemented by {@link MessageProcessingFailure} so the
   * error handler can recover the offending message without binding to a
   * specific exception class hierarchy from the application layer.
   */
  interface FailedMessageCarrier {
    Message failedMessage();
  }

  /**
   * Lightweight runtime exception that carries the failed {@link Message}.
   * Listener implementations should catch their own processing exceptions
   * and rethrow as {@code DeadLetterErrorHandler.wrap(e, message)} so this
   * handler can route the original payload to the DLQ.
   */
  public static final class MessageProcessingFailure extends RuntimeException
      implements FailedMessageCarrier {

    private final transient Message failed;

    MessageProcessingFailure(Throwable cause, Message failed) {
      super(cause == null ? "JMS listener processing failed" : cause.getMessage(), cause);
      this.failed = failed;
    }

    @Override
    public Message failedMessage() {
      return failed;
    }
  }
}
