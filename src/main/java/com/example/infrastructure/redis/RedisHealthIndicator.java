package com.example.infrastructure.redis;

import java.util.Properties;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * BANK S-32 — Spring Boot actuator {@code HealthIndicator} that surfaces
 * Redis cache liveness under {@code GET /actuator/health/redis}.
 *
 * <p>Registers itself as the {@code redis} component (the bean name suffix
 * before {@code HealthIndicator} is how Boot names the health entry — so
 * {@code RedisHealthIndicator} → "redis"). We provide our own rather than
 * relying on Boot's auto-configured one because:
 *
 * <ul>
 *   <li>the auto-configured indicator picks up the first connection factory
 *       bean, but we want the indicator wired to the exact factory the
 *       cache adapter uses;</li>
 *   <li>we want the response to include the configured namespace so on-call
 *       can confirm the deployment is talking to the right keyspace.</li>
 * </ul>
 *
 * <p>The check itself is a Redis {@code PING} via
 * {@link RedisConnection#ping()} — the canonical liveness probe; cheap,
 * atomic, returns {@code "PONG"} on success.
 */
@Component("redisHealthIndicator")
public class RedisHealthIndicator extends AbstractHealthIndicator {

  private final RedisTemplate<String, Object> redis;
  private final RedisProperties props;

  public RedisHealthIndicator(RedisTemplate<String, Object> redis, RedisProperties props) {
    super("Redis health check failed");
    this.redis = redis;
    this.props = props;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) {
    try {
      String pong = redis.execute((RedisCallback<String>) RedisConnection::ping);
      builder.up()
          .withDetail("ping", pong == null ? "(null)" : pong)
          .withDetail("namespace", props.getNamespace())
          .withDetail("host", props.getHost())
          .withDetail("port", props.getPort());
    } catch (DataAccessException e) {
      builder.down(e)
          .withDetail("host", props.getHost())
          .withDetail("port", props.getPort());
    }
  }

  /**
   * Test-only hatch to inspect server-info during diagnostics. Kept package-
   * private so it doesn't leak past the infrastructure boundary.
   */
  Properties redisInfo() {
    return redis.execute((RedisCallback<Properties>) RedisConnection::info);
  }
}
