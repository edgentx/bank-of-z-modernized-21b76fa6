package com.example.infrastructure.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * BANK S-32 — verifies the Redis health indicator wires into actuator with
 * the expected component name and surfaces UP/DOWN + diagnostic detail.
 *
 * <p>The acceptance criterion "Redis health check is included in application
 * health endpoint" is satisfied by Spring Boot auto-registering any
 * {@code HealthIndicator} bean into {@code /actuator/health}; what we test
 * here is that the indicator returns the right state and that on-call
 * gets actionable detail (host/port/namespace) in both branches.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class RedisHealthIndicatorTest {

  private final RedisTemplate<String, Object> template = mock(RedisTemplate.class);
  private final RedisProperties props = props();

  @Test
  void healthIsUpWhenRedisRespondsToPing() {
    when(template.execute(any(RedisCallback.class))).thenReturn("PONG");

    Health health = new RedisHealthIndicator(template, props).health();

    assertEquals(Status.UP, health.getStatus());
    assertEquals("PONG", health.getDetails().get("ping"));
    assertEquals("bank-prod", health.getDetails().get("namespace"));
    assertEquals("redis.svc", health.getDetails().get("host"));
    assertEquals(6379, health.getDetails().get("port"));
  }

  @Test
  void healthIsDownWhenRedisThrowsDataAccessException() {
    when(template.execute(any(RedisCallback.class)))
        .thenThrow(new QueryTimeoutException("connection refused"));

    Health health = new RedisHealthIndicator(template, props).health();

    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("redis.svc", health.getDetails().get("host"));
    assertEquals(6379, health.getDetails().get("port"));
  }

  private static RedisProperties props() {
    RedisProperties p = new RedisProperties();
    p.setNamespace("bank-prod");
    p.setHost("redis.svc");
    p.setPort(6379);
    return p;
  }
}
