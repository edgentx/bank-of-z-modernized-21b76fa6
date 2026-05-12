package com.example.infrastructure.jms;

import com.example.infrastructure.telemetry.JmsTraceContextPropagator;
import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import io.opentelemetry.api.OpenTelemetry;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * BANK S-34 — Spring wiring for the IBM MQ JMS messaging adapter.
 *
 * <p>This class hand-configures the JMS stack rather than pulling in
 * {@code mq-jms-spring-boot-starter}, matching the convention set by the
 * Redis (S-32), MinIO (S-31), and Temporal (S-33) adapters. Hand-wiring
 * keeps three properties true:
 *
 * <ul>
 *   <li>tests can swap any single bean (e.g. a mocked
 *       {@link JmsTemplate}) without dragging the full auto-config
 *       machinery into a unit test;</li>
 *   <li>bean lifecycle is deterministic — IBM MQ's auto-config has been
 *       known to fight with Spring's connection factory caching when both
 *       attempt to manage the same underlying connection pool;</li>
 *   <li>upgrading IBM MQ or spring-jms does not silently change behavior
 *       through a different starter version.</li>
 * </ul>
 *
 * <p>Beans configured here:
 * <ul>
 *   <li>{@link MQConnectionFactory} — the raw IBM MQ factory configured
 *       with hostname/port/channel/QMgr/CCSID;</li>
 *   <li>{@link ConnectionFactory} (cached) — a {@link CachingConnectionFactory}
 *       wrapper so {@link JmsTemplate}'s per-send connection churn becomes
 *       a single long-lived connection;</li>
 *   <li>{@link DomainEventMessageConverter} — JSON converter shared by the
 *       template and the listener-container factory;</li>
 *   <li>{@link JmsTemplate} — for the publisher adapter and the DLQ error
 *       handler;</li>
 *   <li>{@link JmsTransactionManager} — so {@code @Transactional} on
 *       listener methods (and the publisher when invoked from one) becomes
 *       a JMS-session-transacted unit-of-work;</li>
 *   <li>{@link DeadLetterErrorHandler} — bound to the listener container
 *       factory so failed messages flow to {@code messaging.ibmmq.dead-letter-queue};</li>
 *   <li>{@link DefaultJmsListenerContainerFactory} (named {@code "jmsListenerContainerFactory"},
 *       the {@code @JmsListener} default) — concurrency-bounded, session-
 *       transacted, and pre-bound to the converter + error handler.</li>
 * </ul>
 *
 * <p>{@link EnableJms} switches on Spring's {@code @JmsListener} annotation
 * scanning. The {@link ConditionalOnProperty} guard means the entire JMS
 * stack stays out of the application context when
 * {@code messaging.ibmmq.enabled=false}, which is the default for unit
 * tests and the embedded H2 BDD suite so they never need a broker.
 */
@Configuration
@EnableJms
@EnableConfigurationProperties(IbmMqJmsProperties.class)
@ConditionalOnProperty(prefix = "messaging.ibmmq", name = "enabled", havingValue = "true",
    matchIfMissing = false)
public class IbmMqJmsConfig {

  /**
   * Construct the raw IBM MQ connection factory. This is a config POJO —
   * no network I/O happens until {@code createConnection()} is called via
   * the cached wrapper below, so bean creation is safe in tests.
   */
  @Bean
  public MQConnectionFactory mqConnectionFactory(IbmMqJmsProperties props) throws JMSException {
    MQConnectionFactory factory = new MQConnectionFactory();
    factory.setHostName(props.getHostName());
    factory.setPort(props.getPort());
    factory.setChannel(props.getChannel());
    factory.setQueueManager(props.getQueueManager());
    factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
    factory.setCCSID(props.getCcsid());
    factory.setStringProperty(WMQConstants.USERID, props.getUser());
    if (props.getPassword() != null && !props.getPassword().isEmpty()) {
      factory.setStringProperty(WMQConstants.PASSWORD, props.getPassword());
    }
    if (props.isSslEnabled() && !props.getSslCipherSuite().isBlank()) {
      factory.setSSLCipherSuite(props.getSslCipherSuite());
    }
    return factory;
  }

  /**
   * Wrap the IBM MQ factory in a {@link CachingConnectionFactory} so the
   * {@link JmsTemplate} reuses a single underlying MQ connection across
   * sends instead of opening a fresh one per call (which on IBM MQ is a
   * 100ms+ TCP+TLS+CHLAUTH handshake and quickly becomes the bottleneck).
   *
   * <p>{@code cacheConsumers=false} keeps the consumer-side caching off
   * because we drive consumers through the
   * {@link DefaultJmsListenerContainerFactory} below, which manages its
   * own consumer lifecycle and would conflict with cache eviction here.
   */
  @Bean
  @Primary
  public ConnectionFactory cachingConnectionFactory(MQConnectionFactory mq) {
    CachingConnectionFactory caching = new CachingConnectionFactory(mq);
    caching.setSessionCacheSize(10);
    caching.setCacheConsumers(false);
    caching.setReconnectOnException(true);
    return caching;
  }

  /** JSON converter shared by the template and listener containers. */
  @Bean
  public DomainEventMessageConverter jmsMessageConverter() {
    return new DomainEventMessageConverter();
  }

  /**
   * High-level template the {@link IbmMqJmsMessagePublisher} uses. The
   * receive timeout is set so a downed broker fails fast — the
   * default of {@code -1} (block forever) would hang request threads.
   */
  @Bean
  public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory,
      DomainEventMessageConverter converter,
      IbmMqJmsProperties props) {
    JmsTemplate template = new JmsTemplate(connectionFactory);
    template.setMessageConverter(converter);
    template.setReceiveTimeout(props.getReceiveTimeout().toMillis());
    template.setSessionTransacted(props.isSessionTransacted());
    return template;
  }

  /**
   * Spring transaction manager backed by the JMS connection factory.
   * Beans annotated with {@code @Transactional("jmsTransactionManager")}
   * (or in a deployment where this is the primary TM, just
   * {@code @Transactional}) participate in a JMS-session-level
   * transaction: the receive, the application work, the DLQ-route on
   * failure, and the publish all commit together — or none of them do.
   *
   * <p>Note: when DB-side persistence is in the same unit-of-work (Mongo
   * is not transactional with JMS; DB2 is via XA), the deployment binds
   * its own {@code ChainedTransactionManager} or XA TM in front of this
   * one. That composition is per-environment policy, not adapter
   * concern.
   */
  @Bean
  public PlatformTransactionManager jmsTransactionManager(ConnectionFactory connectionFactory) {
    return new JmsTransactionManager(connectionFactory);
  }

  /**
   * Listener-side error handler that routes failed messages to the
   * configured DLQ. Bound to the listener container factory below so
   * every {@code @JmsListener} in the application gets DLQ semantics
   * for free.
   */
  @Bean
  public DeadLetterErrorHandler deadLetterErrorHandler(JmsTemplate jmsTemplate, IbmMqJmsProperties props) {
    return new DeadLetterErrorHandler(jmsTemplate, props.getDeadLetterQueue());
  }

  /**
   * The {@link com.example.ports.MessagePublisherPort} bean. Registered
   * here (rather than via {@code @Component} on the adapter class) so the
   * whole adapter — config + publisher — is gated together by
   * {@code messaging.ibmmq.enabled}: tests and the BDD suite see neither
   * the {@link JmsTemplate} nor the publisher in the context, and never
   * have to mock around a broker that does not exist.
   */
  @Bean
  public IbmMqJmsMessagePublisher ibmMqJmsMessagePublisher(JmsTemplate jmsTemplate,
      IbmMqJmsProperties props,
      ObjectProvider<JmsTraceContextPropagator> tracePropagatorProvider) {
    // S-35: resolve the OpenTelemetry JMS propagator if telemetry is on the
    // classpath; fall back to a no-op propagator backed by OpenTelemetry.noop()
    // so contexts that load only the MQ stack (the existing tests, the BDD
    // suite, the operator-tool standalones) do not require telemetry beans.
    JmsTraceContextPropagator propagator = tracePropagatorProvider.getIfAvailable(
        () -> new JmsTraceContextPropagator(OpenTelemetry.noop()));
    return new IbmMqJmsMessagePublisher(jmsTemplate, props, propagator);
  }

  /**
   * The {@code @JmsListener}-default container factory. Spring's
   * annotation processor looks up a bean named exactly
   * {@code "jmsListenerContainerFactory"} unless the {@code containerFactory}
   * attribute on the annotation says otherwise.
   *
   * <p>Configuration choices:
   * <ul>
   *   <li>{@code sessionTransacted=true} — the listener Session
   *       participates in the JmsTransactionManager unit-of-work, so
   *       application code can use {@code @Transactional} to atomically
   *       receive + process + publish + ack.</li>
   *   <li>{@code concurrency} from properties — concurrency is queue-
   *       semantic-sensitive on MQ, so it lives in config (not code).</li>
   *   <li>{@code errorHandler} = DLQ router — failed processing pivots
   *       to the application DLQ rather than the silent log-and-discard
   *       that is Spring's default.</li>
   * </ul>
   *
   * <p>{@code autoStartup} is bound to {@code messaging.ibmmq.listener-enabled}
   * so unit tests can keep the listener container quiet without removing
   * the bean.
   */
  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
      ConnectionFactory connectionFactory,
      DomainEventMessageConverter converter,
      DeadLetterErrorHandler errorHandler,
      PlatformTransactionManager jmsTransactionManager,
      IbmMqJmsProperties props) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(converter);
    factory.setErrorHandler(errorHandler);
    factory.setSessionTransacted(props.isSessionTransacted());
    factory.setTransactionManager(jmsTransactionManager);
    factory.setConcurrency(props.getConcurrency());
    factory.setAutoStartup(props.isListenerEnabled());
    return factory;
  }
}
