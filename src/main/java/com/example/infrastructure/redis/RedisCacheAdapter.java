package com.example.infrastructure.redis;

import com.example.ports.CacheException;
import com.example.ports.CachePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

/**
 * BANK S-32 — Redis-backed {@link CachePort} implementation.
 *
 * <p>The adapter is a thin translation layer over {@link RedisTemplate} that
 *
 * <ul>
 *   <li>composes the namespaced key
 *       ({@code <namespace>:<region>:<key>}) so multiple deployments can
 *       share a Redis cluster without colliding;</li>
 *   <li>looks up the region's TTL from {@link RedisProperties} on every
 *       {@link #put} so policy changes via config-reload take effect without
 *       a restart;</li>
 *   <li>serializes values as JSON (via the template's
 *       {@code GenericJackson2JsonRedisSerializer}) for redis-cli
 *       debuggability;</li>
 *   <li>maps every checked {@link DataAccessException} from Spring Data
 *       Redis to a port-level {@link CacheException} so application code
 *       does not import {@code org.springframework.dao.*};</li>
 *   <li>implements {@link #clear} with a non-blocking SCAN cursor instead of
 *       {@code KEYS} — KEYS blocks the Redis main thread and is the classic
 *       cause of latency spikes on production clusters.</li>
 * </ul>
 */
@Component
public class RedisCacheAdapter implements CachePort {

  private final RedisTemplate<String, Object> redis;
  private final RedisProperties props;
  /**
   * Jackson mapper used to coerce the polymorphically-typed JSON blob back
   * into the caller's requested concrete {@code Class<T>}. The template
   * returns the value as {@code Object} (per Jackson's default-typing
   * configuration) and we re-cast/convert here so the public surface stays
   * type-safe.
   */
  private final ObjectMapper coercionMapper;

  public RedisCacheAdapter(RedisTemplate<String, Object> redis, RedisProperties props) {
    this.redis = redis;
    this.props = props;
    this.coercionMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  // ---------------------------------------------------------------------------
  // CachePort
  // ---------------------------------------------------------------------------

  @Override
  public <T> Optional<T> get(String region, String key, Class<T> type) {
    String fullKey = compose(region, key);
    Object raw;
    try {
      raw = redis.opsForValue().get(fullKey);
    } catch (DataAccessException e) {
      throw new CacheException("Failed to read " + fullKey + ": " + e.getMessage(), e);
    }
    if (raw == null) {
      return Optional.empty();
    }
    return Optional.of(coerce(raw, type, fullKey));
  }

  @Override
  public void put(String region, String key, Object value) {
    put(region, key, value, props.ttlFor(region));
  }

  @Override
  public void put(String region, String key, Object value, Duration ttl) {
    if (value == null) {
      throw new CacheException("Refusing to cache null for " + region + "/" + key
          + "; call evict(region, key) instead");
    }
    String fullKey = compose(region, key);
    try {
      if (ttl == null || ttl.isZero() || ttl.isNegative()) {
        redis.opsForValue().set(fullKey, value);
      } else {
        redis.opsForValue().set(fullKey, value, ttl);
      }
    } catch (DataAccessException e) {
      throw new CacheException("Failed to write " + fullKey + ": " + e.getMessage(), e);
    }
  }

  @Override
  public void evict(String region, String key) {
    String fullKey = compose(region, key);
    try {
      redis.delete(fullKey);
    } catch (DataAccessException e) {
      throw new CacheException("Failed to evict " + fullKey + ": " + e.getMessage(), e);
    }
  }

  @Override
  public void clear(String region) {
    String pattern = compose(region, "*");
    ScanOptions options = ScanOptions.scanOptions().match(pattern).count(500).build();
    try (Cursor<String> cursor = redis.scan(options)) {
      Set<String> batch = new java.util.HashSet<>(512);
      while (cursor.hasNext()) {
        batch.add(cursor.next());
        if (batch.size() >= 500) {
          redis.delete(batch);
          batch.clear();
        }
      }
      if (!batch.isEmpty()) {
        redis.delete(batch);
      }
    } catch (DataAccessException e) {
      throw new CacheException("Failed to clear region " + region + ": " + e.getMessage(), e);
    }
  }

  @Override
  public <T> Optional<T> getOrLoad(String region, String key, Class<T> type, Supplier<T> loader) {
    Optional<T> hit = get(region, key, type);
    if (hit.isPresent()) {
      return hit;
    }
    T loaded = loader.get();
    if (loaded == null) {
      // Negative caching is opt-in; surface the miss to the caller without
      // poisoning the cache with a sentinel they didn't ask for.
      return Optional.empty();
    }
    put(region, key, loaded);
    return Optional.of(loaded);
  }

  // ---------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------

  /**
   * Compose the wire-level Redis key from the configured namespace, the
   * logical region, and the caller's key. Visible for testing so the
   * adapter test can assert on the exact key shape without re-deriving the
   * format string.
   */
  String compose(String region, String key) {
    return props.getNamespace() + ":" + region + ":" + key;
  }

  @SuppressWarnings("unchecked")
  private <T> T coerce(Object raw, Class<T> type, String fullKey) {
    if (type.isInstance(raw)) {
      return (T) raw;
    }
    try {
      return coercionMapper.convertValue(raw, type);
    } catch (IllegalArgumentException e) {
      throw new CacheException(
          "Cached value at " + fullKey + " could not be deserialized as "
              + type.getName() + ": " + e.getMessage(), e);
    }
  }
}
