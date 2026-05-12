package com.example.ports;

import com.example.domain.shared.DomainEvent;
import java.util.Map;

/**
 * BANK S-34 — Hex port for outbound asynchronous messaging.
 *
 * <p>Backed by IBM MQ via JMS in the modernized stack
 * ({@code com.example.infrastructure.jms}), but the contract is provider-
 * neutral so the same port can sit in front of ActiveMQ Artemis, RabbitMQ's
 * JMS bridge, or an in-memory test double when a deployment requires it.
 * The legacy mainframe relied on IBM MQ for async fan-out between CICS
 * transactions and downstream batch/IMS consumers; this port preserves that
 * pattern while letting application code stay free of {@code jakarta.jms.*}
 * imports.
 *
 * <p>Two operations are exposed:
 * <ul>
 *   <li>{@link #publish(String, DomainEvent)} — the common case, where a
 *       domain event from an aggregate (e.g. {@code TransactionPostedEvent})
 *       is serialized to JSON and sent to a logical queue or topic. The
 *       adapter copies the event's {@code type()} and {@code aggregateId()}
 *       onto JMS message properties so listeners can filter without
 *       deserializing the body.</li>
 *   <li>{@link #send(String, Object, Map)} — escape hatch for non-event
 *       payloads (e.g. a legacy CICS COPYBOOK shape mapped to a POJO) with
 *       explicit JMS-property headers. Used by the legacy-bridge module
 *       when it must speak the same wire contract the mainframe consumers
 *       already expect.</li>
 * </ul>
 *
 * <p>Both operations are <em>synchronous</em> with respect to the JMS
 * provider — they return once the broker has acknowledged the send (or has
 * accepted it into the local transactional session, when the caller is
 * already inside a transaction). They do NOT wait for the consumer to
 * process the message; that is the whole point of the queue.
 *
 * @see MessagingException for the failure-mode contract.
 */
public interface MessagePublisherPort {

  /**
   * Publish a domain event to the named destination. The adapter:
   * <ul>
   *   <li>serializes the event body as JSON via the configured
   *       {@code MessageConverter} (see
   *       {@code DomainEventMessageConverter});</li>
   *   <li>sets the JMS message type to the event's {@code type()};</li>
   *   <li>copies {@code aggregateId} and {@code occurredAt} onto JMS
   *       properties for content-based routing on the consumer side;</li>
   *   <li>respects the caller's transaction context — if invoked inside
   *       a Spring-managed JMS transaction, the send is only made visible
   *       on commit.</li>
   * </ul>
   *
   * @param destination logical queue or topic name (the adapter resolves
   *                    the wire-level IBM MQ object via
   *                    {@code messaging.ibmmq.destinations.<name>=QUEUE.NAME}
   *                    in {@code application.properties}, falling back to
   *                    using the logical name as the wire name).
   * @param event       the domain event to publish.
   * @throws MessagingException on serialization or broker failures.
   */
  void publish(String destination, DomainEvent event);

  /**
   * Send an arbitrary payload with caller-supplied JMS-property headers.
   * Used by the legacy bridge to emit messages whose shape is dictated by
   * the existing mainframe consumers and does not match a modern domain
   * event.
   *
   * @param destination logical queue/topic name.
   * @param payload     serializable payload — the adapter's converter turns
   *                    {@code String} into a TextMessage and everything else
   *                    into a JSON TextMessage.
   * @param headers     extra JMS string-properties to set on the message
   *                    (e.g. {@code "x-cics-region" -> "PROD01"}). May be
   *                    {@code null} or empty.
   * @throws MessagingException on serialization or broker failures.
   */
  void send(String destination, Object payload, Map<String, String> headers);
}
