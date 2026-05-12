package com.example.ports;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * BANK S-32 — Hex port for distributed caching.
 *
 * <p>Backed by Redis in the modernized stack (Spring Data Redis + Lettuce in
 * {@code com.example.infrastructure.redis}), but the contract is provider-
 * neutral so the same port can sit in front of Hazelcast / Caffeine / an
 * in-memory test double when a deployment requires it.
 *
 * <p>Cached values are scoped by a logical <strong>region</strong> (e.g.
 * {@code "teller-session"}, {@code "account-balance"}, {@code "rate-quote"}).
 * Each region has an independently configurable TTL — see
 * {@code spring.cache.redis.regions.<name>.ttl} on the adapter implementation.
 * The combined Redis key is {@code <namespace>:<region>:<key>}, scoped by
 * {@code spring.cache.redis.namespace} to keep multiple environments sharing
 * a single Redis cluster from colliding.
 */
public interface CachePort {

  /**
   * Look up a cached value. Returns {@link Optional#empty()} for a cache miss
   * (no key) <em>and</em> for a cached-but-expired value. The adapter is
   * responsible for JSON deserialization into {@code type}.
   *
   * @throws CacheException when the underlying store is unreachable or the
   *         stored value cannot be deserialized as {@code type}.
   */
  <T> Optional<T> get(String region, String key, Class<T> type);

  /**
   * Store {@code value} under {@code region}/{@code key} using the region's
   * configured TTL. Overwrites any existing value. {@code null} values are
   * rejected — callers should evict rather than caching {@code null} (and
   * Redis SET-NIL semantics differ across clients enough that the contract
   * is clearer if we just forbid it).
   *
   * @throws CacheException on serialization or transport failures.
   */
  void put(String region, String key, Object value);

  /**
   * Store {@code value} with an explicit TTL override, ignoring the region's
   * default. Use sparingly — the per-region default keeps TTL policy in one
   * place. Pass a TTL of {@code 0} or a negative duration to store without
   * expiry.
   */
  void put(String region, String key, Object value, java.time.Duration ttl);

  /**
   * Remove a single key. No-op when the key is already absent (Redis DEL
   * semantics — count returned by DEL is not surfaced).
   */
  void evict(String region, String key);

  /**
   * Drop every key in a region. Used at deploy time after schema/contract
   * changes, and from admin tooling.  Walks the keyspace via SCAN to avoid
   * blocking Redis with KEYS on large datasets.
   */
  void clear(String region);

  /**
   * Cache-aside helper. Returns the cached value if present, otherwise calls
   * {@code loader}, stores its result under {@code region}/{@code key} with
   * the region's TTL, and returns it.
   *
   * <p>If {@code loader} returns {@code null} (e.g. the underlying entity
   * does not exist) the result is NOT cached — caching negative results is
   * an opt-in decision callers can make by storing a sentinel themselves.
   * The optional is empty in that case.
   *
   * <p>This is a thin convenience over {@link #get} + {@link #put}; it does
   * NOT take a distributed lock around the loader, so two concurrent misses
   * may both invoke it. That tradeoff (occasional duplicate work vs the
   * complexity of a cluster-wide mutex) is the right default for read-heavy
   * caches; callers that need stricter semantics should put the lock in
   * front of this call themselves.
   */
  <T> Optional<T> getOrLoad(String region, String key, Class<T> type, Supplier<T> loader);
}
