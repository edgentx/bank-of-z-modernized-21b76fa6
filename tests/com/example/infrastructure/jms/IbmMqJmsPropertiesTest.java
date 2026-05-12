package com.example.infrastructure.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * BANK S-34 — {@link IbmMqJmsProperties} binding and helper tests.
 *
 * <p>Mirrors the S-32 {@link com.example.infrastructure.redis.RedisProperties}
 * test style: validate defaults, then drive {@link
 * IbmMqJmsProperties#resolveDestination(String)} through the
 * declared/undeclared paths so the adapter doesn't ship surprising
 * pass-through semantics.
 */
class IbmMqJmsPropertiesTest {

  @Test
  void defaultsAreSafeForLocalDev() {
    IbmMqJmsProperties props = new IbmMqJmsProperties();

    assertEquals("QM1", props.getQueueManager());
    assertEquals("localhost", props.getHostName());
    assertEquals(1414, props.getPort());
    assertEquals("DEV.APP.SVRCONN", props.getChannel());
    assertEquals(1208, props.getCcsid());
    assertFalse(props.isSslEnabled());
    assertEquals("BANK.DLQ", props.getDeadLetterQueue());
    assertFalse(props.isListenerEnabled(),
        "listener must default to disabled so unit tests do not need a broker");
    assertTrue(props.isSessionTransacted());
    assertEquals(Duration.ofSeconds(5), props.getReceiveTimeout());
  }

  @Test
  void resolveDestinationReturnsMappedWireNameWhenDeclared() {
    IbmMqJmsProperties props = new IbmMqJmsProperties();
    props.setDestinations(Map.of("account.events", "BANK.ACCT.EVT.Q"));

    assertEquals("BANK.ACCT.EVT.Q", props.resolveDestination("account.events"));
  }

  @Test
  void resolveDestinationPassesThroughWhenUndeclared() {
    IbmMqJmsProperties props = new IbmMqJmsProperties();

    // Local dev with auto-created queues — the caller's logical name is
    // also the wire name. No silent rewrite, no surprise.
    assertEquals("ad.hoc.queue", props.resolveDestination("ad.hoc.queue"));
  }
}
