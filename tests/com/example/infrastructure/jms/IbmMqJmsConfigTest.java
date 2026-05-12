package com.example.infrastructure.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.ports.MessagePublisherPort;
import jakarta.jms.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * BANK S-34 — {@link IbmMqJmsConfig} bean-wiring tests.
 *
 * <p>Uses Spring's {@link ApplicationContextRunner} to load the config in
 * isolation (no @SpringBootTest, no auto-configuration churn) and validate
 * the bean graph end to end:
 *
 * <ul>
 *   <li>{@code messaging.ibmmq.enabled=false} → no JMS beans, the whole
 *       stack stays out of the context (test/BDD-suite default);</li>
 *   <li>{@code messaging.ibmmq.enabled=true} → publisher, template, listener
 *       container factory, DLQ handler, JmsTransactionManager are all
 *       wired with the configured concurrency, transaction settings, and
 *       error handler.</li>
 * </ul>
 */
class IbmMqJmsConfigTest {

  private ApplicationContextRunner contextRunner;

  /**
   * Cached system properties so we can reset MQ client knobs after each
   * test. The IBM MQ Jakarta client reads a handful of system properties
   * eagerly on first use; the config under test does not modify them but
   * the cache keeps drift from leaking between tests.
   */
  private final java.util.Properties savedProps = new java.util.Properties();

  @BeforeEach
  void setUp() {
    savedProps.clear();
    savedProps.putAll(System.getProperties());
    contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(IbmMqJmsConfig.class);
  }

  @AfterEach
  void tearDown() {
    System.setProperties(savedProps);
  }

  @Test
  void noJmsBeansWhenAdapterDisabled() {
    contextRunner
        .withPropertyValues("messaging.ibmmq.enabled=false")
        .run(ctx -> {
          assertFalse(ctx.containsBean("jmsTemplate"),
              "JmsTemplate must not be in the context when adapter is disabled");
          assertFalse(ctx.containsBean("jmsListenerContainerFactory"));
          assertFalse(ctx.containsBean("ibmMqJmsMessagePublisher"));
        });
  }

  @Test
  void fullStackWiredWhenAdapterEnabled() {
    contextRunner
        .withPropertyValues(
            "messaging.ibmmq.enabled=true",
            "messaging.ibmmq.host-name=mq.local",
            "messaging.ibmmq.port=1416",
            "messaging.ibmmq.channel=SBX.APP.SVRCONN",
            "messaging.ibmmq.queue-manager=SBX1",
            "messaging.ibmmq.dead-letter-queue=BANK.SBX.DLQ",
            "messaging.ibmmq.concurrency=2-4",
            "messaging.ibmmq.listener-enabled=false")
        .run(ctx -> {
          assertNotNull(ctx.getBean(ConnectionFactory.class));
          JmsTemplate template = ctx.getBean(JmsTemplate.class);
          assertNotNull(template);
          assertSame(ctx.getBean(DomainEventMessageConverter.class),
              template.getMessageConverter(),
              "JmsTemplate must use the DomainEventMessageConverter so wire format is consistent");
          assertTrue(template.isSessionTransacted(),
              "session-transacted must be on by default for atomic receive+publish");

          DefaultJmsListenerContainerFactory factory =
              ctx.getBean("jmsListenerContainerFactory", DefaultJmsListenerContainerFactory.class);
          assertNotNull(factory);

          assertTrue(ctx.getBean(PlatformTransactionManager.class) instanceof JmsTransactionManager);

          DeadLetterErrorHandler errorHandler = ctx.getBean(DeadLetterErrorHandler.class);
          assertNotNull(errorHandler);

          MessagePublisherPort publisher = ctx.getBean(MessagePublisherPort.class);
          assertTrue(publisher instanceof IbmMqJmsMessagePublisher,
              "MessagePublisherPort must resolve to the IBM MQ adapter");

          IbmMqJmsProperties props = ctx.getBean(IbmMqJmsProperties.class);
          assertEquals("mq.local", props.getHostName());
          assertEquals(1416, props.getPort());
          assertEquals("BANK.SBX.DLQ", props.getDeadLetterQueue());
          assertEquals("2-4", props.getConcurrency());
        });
  }
}
