package com.example.infrastructure.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized config for the Redis cache adapter (S-32).
 *
 * <p>Bound to {@code cache.redis.*} keys in {@code application.properties}
 * (and overridable per environment via env vars like
 * {@code CACHE_REDIS_HOST}, {@code CACHE_REDIS_PORT}, etc.). A separate
 * {@code cache.redis.*} prefix (rather than reusing Spring's
 * {@code spring.data.redis.*}) keeps cache wiring distinct from any future
 * use of Redis for non-cache concerns (e.g. session store, pub/sub) and
 * lets the adapter own its own connection factory.
 *
 * <p>Per-region TTL policies live under
 * {@code cache.redis.regions.<name>.ttl=PT15M} so cache lifetime is a
 * config decision, not a code decision. Regions not declared here fall back
 * to {@link #getDefaultTtl()}.
 */
@ConfigurationProperties(prefix = "cache.redis")
public class RedisProperties {

  /** Redis host. */
  private String host = "localhost";

  /** Redis port. */
  private int port = 6379;

  /** Optional password (Redis AUTH). */
  private String password = "";

  /** Logical database index. */
  private int database = 0;

  /** Connection / command timeout. */
  private Duration timeout = Duration.ofSeconds(2);

  /**
   * Namespace prefix prepended to every key so multiple deployments can share
   * a single Redis cluster without collisions. The final key is
   * {@code <namespace>:<region>:<key>}.
   */
  private String namespace = "bank";

  /** TTL applied when a region has no explicit override. */
  private Duration defaultTtl = Duration.ofMinutes(10);

  /** Per-region TTL overrides keyed by region name. */
  private Map<String, Region> regions = new HashMap<>();

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getDatabase() {
    return database;
  }

  public void setDatabase(int database) {
    this.database = database;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public void setTimeout(Duration timeout) {
    this.timeout = timeout;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public Duration getDefaultTtl() {
    return defaultTtl;
  }

  public void setDefaultTtl(Duration defaultTtl) {
    this.defaultTtl = defaultTtl;
  }

  public Map<String, Region> getRegions() {
    return regions;
  }

  public void setRegions(Map<String, Region> regions) {
    this.regions = regions;
  }

  /**
   * Resolve the effective TTL for {@code region}: the region-specific
   * override when present, otherwise the global default. Never returns
   * {@code null}.
   */
  public Duration ttlFor(String region) {
    Region cfg = regions.get(region);
    if (cfg != null && cfg.getTtl() != null) {
      return cfg.getTtl();
    }
    return defaultTtl;
  }

  /** Per-region settings; currently just TTL, leaves room to grow. */
  public static class Region {
    private Duration ttl;

    public Duration getTtl() {
      return ttl;
    }

    public void setTtl(Duration ttl) {
      this.ttl = ttl;
    }
  }
}
