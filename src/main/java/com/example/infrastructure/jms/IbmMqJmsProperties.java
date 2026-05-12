package com.example.infrastructure.jms;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BANK S-34 — externalized config for the IBM MQ JMS adapter.
 *
 * <p>Bound to {@code messaging.ibmmq.*} keys in
 * {@code application.properties} (and overridable per environment via
 * {@code MESSAGING_IBMMQ_*} env vars). A dedicated prefix (rather than
 * reusing the IBM-published {@code ibm.mq.*}) keeps the spring-boot-starter
 * out of the dependency graph and matches the {@link
 * com.example.infrastructure.redis.RedisProperties} / {@link
 * com.example.infrastructure.temporal.TemporalProperties} convention from
 * S-32 and S-33.
 *
 * <p>The {@link #listenerEnabled} default is {@code false} so unit tests
 * and the Cucumber BDD suite never need a running broker. Containerized
 * deployments flip it on via {@code MESSAGING_IBMMQ_LISTENER_ENABLED=true}.
 *
 * <p>{@link #destinations} provides a logical-to-wire indirection so
 * application code references queues by domain-meaningful names
 * ({@code "account.events"}) and the deployment maps them to mainframe-
 * style wire names ({@code "BANK.ACCT.EVT.Q"}). When a logical name is not
 * in the map, the adapter passes it through unchanged (the local-dev case).
 */
@ConfigurationProperties(prefix = "messaging.ibmmq")
public class IbmMqJmsProperties {

  /** IBM MQ queue manager name (CCDT context). */
  private String queueManager = "QM1";

  /** Host of the listener port on the queue manager. */
  private String hostName = "localhost";

  /** TCP listener port. */
  private int port = 1414;

  /** SVRCONN channel name. */
  private String channel = "DEV.APP.SVRCONN";

  /** App-user used to bind (MQ CHLAUTH controls actual access). */
  private String user = "app";

  /** App password — env vars resolve this at deploy time. */
  private String password = "";

  /**
   * MQ CCSID for the connection. 1208 = UTF-8. Mainframe consumers often
   * expect 819 (Latin-1) or 1140 (EBCDIC-US); keep this configurable.
   */
  private int ccsid = 1208;

  /** Whether to enable SSL/TLS on the SVRCONN channel. */
  private boolean sslEnabled = false;

  /** Cipher suite when {@link #sslEnabled} is true. */
  private String sslCipherSuite = "";

  /**
   * Default dead-letter queue used by the listener error handler when a
   * message cannot be processed. The MQ-built-in
   * {@code SYSTEM.DEAD.LETTER.QUEUE} is reserved for the queue manager's
   * own poison handling — this is an application-level DLQ we control
   * end-to-end (write is transactional, the original message body is
   * preserved, and the failure reason is added as a JMS string-property).
   */
  private String deadLetterQueue = "BANK.DLQ";

  /**
   * Whether to start {@code @JmsListener}-driven listener containers at
   * boot. Off by default so unit tests do not need a broker.
   */
  private boolean listenerEnabled = false;

  /**
   * Number of consumer threads the listener container factory should
   * provision per listener. "1-3" means start at 1, grow up to 3 under
   * load. Keep concurrency modest — mainframe consumers serialize on the
   * MQ queue order and bursting parallelism here can re-order events the
   * downstream system expects in sequence.
   */
  private String concurrency = "1-3";

  /**
   * Whether the listener-side {@code Session} is transactional. When
   * {@code true} (the default), receive + processing + DLQ-route + ack
   * are one atomic operation; a thrown exception rolls back delivery and
   * MQ redelivers up to {@code BackoutThreshold} (configured on the queue
   * itself) before the error handler routes to the DLQ.
   */
  private boolean sessionTransacted = true;

  /**
   * Reconnect/receive timeout applied to the JMS template. Keep short
   * enough that a downed broker fails fast rather than hanging the request
   * thread; the upstream caller is expected to surface the failure to the
   * user (or queue a retry of its own).
   */
  private Duration receiveTimeout = Duration.ofSeconds(5);

  /** Logical-to-wire destination map. See class javadoc. */
  private Map<String, String> destinations = new HashMap<>();

  // ---- getters/setters ----

  public String getQueueManager() { return queueManager; }
  public void setQueueManager(String queueManager) { this.queueManager = queueManager; }

  public String getHostName() { return hostName; }
  public void setHostName(String hostName) { this.hostName = hostName; }

  public int getPort() { return port; }
  public void setPort(int port) { this.port = port; }

  public String getChannel() { return channel; }
  public void setChannel(String channel) { this.channel = channel; }

  public String getUser() { return user; }
  public void setUser(String user) { this.user = user; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public int getCcsid() { return ccsid; }
  public void setCcsid(int ccsid) { this.ccsid = ccsid; }

  public boolean isSslEnabled() { return sslEnabled; }
  public void setSslEnabled(boolean sslEnabled) { this.sslEnabled = sslEnabled; }

  public String getSslCipherSuite() { return sslCipherSuite; }
  public void setSslCipherSuite(String sslCipherSuite) { this.sslCipherSuite = sslCipherSuite; }

  public String getDeadLetterQueue() { return deadLetterQueue; }
  public void setDeadLetterQueue(String deadLetterQueue) { this.deadLetterQueue = deadLetterQueue; }

  public boolean isListenerEnabled() { return listenerEnabled; }
  public void setListenerEnabled(boolean listenerEnabled) { this.listenerEnabled = listenerEnabled; }

  public String getConcurrency() { return concurrency; }
  public void setConcurrency(String concurrency) { this.concurrency = concurrency; }

  public boolean isSessionTransacted() { return sessionTransacted; }
  public void setSessionTransacted(boolean sessionTransacted) { this.sessionTransacted = sessionTransacted; }

  public Duration getReceiveTimeout() { return receiveTimeout; }
  public void setReceiveTimeout(Duration receiveTimeout) { this.receiveTimeout = receiveTimeout; }

  public Map<String, String> getDestinations() { return destinations; }
  public void setDestinations(Map<String, String> destinations) { this.destinations = destinations; }

  /**
   * Resolve a caller-supplied logical destination to the configured wire
   * name. Pass-through when the logical name is not declared in the map so
   * local-dev (with auto-created queues) Just Works.
   */
  public String resolveDestination(String logical) {
    String wire = destinations.get(logical);
    return wire == null ? logical : wire;
  }
}
