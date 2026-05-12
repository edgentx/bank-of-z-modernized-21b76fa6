package com.example.ports;

/**
 * BANK S-34 — port-level exception for asynchronous-messaging failures.
 *
 * <p>Thrown by {@link MessagePublisherPort} implementations so application
 * code does not import {@code jakarta.jms.*}, {@code com.ibm.mq.*}, or
 * Spring's {@code JmsException} hierarchy. Mirrors the
 * {@link CacheException} / {@link WorkflowException} pattern used by the
 * other infrastructure ports.
 *
 * <p>This is an unchecked exception because every JMS provider models
 * connectivity, serialization, and broker-side errors as runtime exceptions
 * (Spring's {@code JmsException} extends {@code NestedRuntimeException});
 * forcing application code to declare {@code throws} on every publish call
 * would not buy any recovery options that catch-blocks don't already give.
 */
public class MessagingException extends RuntimeException {

  public MessagingException(String message) {
    super(message);
  }

  public MessagingException(String message, Throwable cause) {
    super(message, cause);
  }
}
