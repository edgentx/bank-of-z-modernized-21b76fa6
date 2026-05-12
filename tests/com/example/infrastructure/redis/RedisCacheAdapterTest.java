package com.example.infrastructure.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.ports.CacheException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * BANK S-32 — Redis cache adapter unit + integration tests.
 *
 * <p>These tests drive the {@link RedisCacheAdapter} through its full code
 * path with a mocked {@link RedisTemplate}, validating that:
 *
 * <ul>
 *   <li>get/put/evict compose the namespaced key correctly;</li>
 *   <li>{@link com.example.ports.CachePort#put(String, String, Object)} resolves
 *       the region's TTL from properties (per-region override > default);</li>
 *   <li>cache HIT short-circuits the loader and cache MISS triggers it,
 *       then stores the loaded value;</li>
 *   <li>refusing to cache {@code null} is enforced;</li>
 *   <li>Spring {@link org.springframework.dao.DataAccessException}s map to
 *       {@link CacheException} so application code never imports
 *       {@code org.springframework.dao.*}.</li>
 * </ul>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class RedisCacheAdapterTest {

  private final RedisTemplate<String, Object> template = mock(RedisTemplate.class);
  private final ValueOperations<String, Object> ops = mock(ValueOperations.class);
  private final RedisProperties props = new RedisProperties();
  private RedisCacheAdapter adapter;

  @BeforeEach
  void setUp() {
    when(template.opsForValue()).thenReturn(ops);
    props.setNamespace("banktest");
    props.setDefaultTtl(Duration.ofMinutes(10));
    RedisProperties.Region session = new RedisProperties.Region();
    session.setTtl(Duration.ofSeconds(30));
    props.setRegions(Map.of("teller-session", session));
    adapter = new RedisCacheAdapter(template, props);
  }

  // ---------------------------------------------------------------------------
  // key composition
  // ---------------------------------------------------------------------------

  @Test
  void composeBuildsNamespacedKey() {
    assertEquals("banktest:teller-session:abc123", adapter.compose("teller-session", "abc123"));
  }

  // ---------------------------------------------------------------------------
  // get — hit + miss
  // ---------------------------------------------------------------------------

  @Test
  void getReturnsValueOnCacheHit() {
    when(ops.get("banktest:account-balance:42")).thenReturn("USD 1000.00");

    Optional<String> got = adapter.get("account-balance", "42", String.class);

    assertTrue(got.isPresent());
    assertEquals("USD 1000.00", got.get());
  }

  @Test
  void getReturnsEmptyOnCacheMiss() {
    when(ops.get(anyString())).thenReturn(null);

    Optional<String> got = adapter.get("account-balance", "missing", String.class);

    assertFalse(got.isPresent());
  }

  @Test
  void getCoercesMapShapedJsonIntoTargetType() {
    // GenericJackson2JsonRedisSerializer may surface a polymorphically-typed
    // value as a Map<String,Object> on read; the adapter must coerce via
    // Jackson convertValue to the caller's class.
    Map<String, Object> raw = Map.of("amount", 1234, "currency", "USD");
    when(ops.get("banktest:rate-quote:q1")).thenReturn(raw);

    Optional<RateQuote> got = adapter.get("rate-quote", "q1", RateQuote.class);

    assertTrue(got.isPresent());
    assertEquals(1234, got.get().amount);
    assertEquals("USD", got.get().currency);
  }

  @Test
  void getWrapsDataAccessExceptionAsCacheException() {
    when(ops.get(anyString())).thenThrow(new QueryTimeoutException("Redis down"));

    CacheException ex = assertThrows(CacheException.class,
        () -> adapter.get("teller-session", "tx-1", String.class));
    assertTrue(ex.getMessage().contains("teller-session"));
  }

  // ---------------------------------------------------------------------------
  // put — region TTL resolution
  // ---------------------------------------------------------------------------

  @Test
  void putUsesPerRegionTtlOverride() {
    adapter.put("teller-session", "session-1", "{\"userId\":\"alice\"}");

    verify(ops).set("banktest:teller-session:session-1", "{\"userId\":\"alice\"}", Duration.ofSeconds(30));
  }

  @Test
  void putFallsBackToDefaultTtlForUnknownRegion() {
    adapter.put("rate-quote", "q-1", "0.0123");

    verify(ops).set("banktest:rate-quote:q-1", "0.0123", Duration.ofMinutes(10));
  }

  @Test
  void putWithExplicitTtlOverridesRegionConfig() {
    adapter.put("teller-session", "session-2", "value", Duration.ofMinutes(5));

    verify(ops).set("banktest:teller-session:session-2", "value", Duration.ofMinutes(5));
  }

  @Test
  void putWithZeroOrNegativeTtlSetsWithoutExpiry() {
    adapter.put("teller-session", "permanent", "value", Duration.ZERO);

    verify(ops).set("banktest:teller-session:permanent", "value");
    verify(ops, never()).set(anyString(), any(), any(Duration.class));
  }

  @Test
  void putRejectsNullValue() {
    CacheException ex = assertThrows(CacheException.class,
        () -> adapter.put("teller-session", "x", null));
    assertTrue(ex.getMessage().contains("null"));
    verifyNoInteractions(ops);
  }

  // ---------------------------------------------------------------------------
  // evict / clear
  // ---------------------------------------------------------------------------

  @Test
  void evictDelegatesToRedisDelete() {
    adapter.evict("teller-session", "session-1");

    verify(template).delete("banktest:teller-session:session-1");
  }

  @Test
  void evictWrapsTransportFailure() {
    doThrow(new QueryTimeoutException("Redis down"))
        .when(template).delete("banktest:teller-session:k");

    assertThrows(CacheException.class, () -> adapter.evict("teller-session", "k"));
  }

  // ---------------------------------------------------------------------------
  // cache-aside (the integration scenario)
  // ---------------------------------------------------------------------------

  @Test
  void getOrLoadReturnsCachedValueAndSkipsLoaderOnHit() {
    when(ops.get("banktest:account-balance:42")).thenReturn("USD 1000.00");
    AtomicInteger loaderCalls = new AtomicInteger();

    Optional<String> got = adapter.getOrLoad("account-balance", "42", String.class, () -> {
      loaderCalls.incrementAndGet();
      return "USD 9999.99";
    });

    assertTrue(got.isPresent());
    assertEquals("USD 1000.00", got.get());
    assertEquals(0, loaderCalls.get(), "loader must not run on cache hit");
    verify(ops, never()).set(anyString(), any());
    verify(ops, never()).set(anyString(), any(), any(Duration.class));
  }

  @Test
  void getOrLoadCallsLoaderAndStoresValueOnMiss() {
    when(ops.get("banktest:account-balance:7")).thenReturn(null);
    AtomicInteger loaderCalls = new AtomicInteger();

    Optional<String> got = adapter.getOrLoad("account-balance", "7", String.class, () -> {
      loaderCalls.incrementAndGet();
      return "USD 42.00";
    });

    assertTrue(got.isPresent());
    assertEquals("USD 42.00", got.get());
    assertEquals(1, loaderCalls.get(), "loader must run exactly once on cache miss");
    // Region falls back to default TTL (10 min) — account-balance has no override.
    verify(ops).set(eq("banktest:account-balance:7"), eq("USD 42.00"), eq(Duration.ofMinutes(10)));
  }

  @Test
  void getOrLoadReturnsEmptyAndDoesNotCacheWhenLoaderReturnsNull() {
    when(ops.get(anyString())).thenReturn(null);

    Optional<String> got = adapter.getOrLoad("account-balance", "missing", String.class, () -> null);

    assertFalse(got.isPresent());
    verify(ops, never()).set(anyString(), any());
    verify(ops, never()).set(anyString(), any(), any(Duration.class));
  }

  // ---------------------------------------------------------------------------
  // type passthrough
  // ---------------------------------------------------------------------------

  @Test
  void getPassesThroughExactTypeWithoutConversion() {
    RateQuote stored = new RateQuote(99, "EUR");
    when(ops.get("banktest:rate-quote:q-exact")).thenReturn(stored);

    Optional<RateQuote> got = adapter.get("rate-quote", "q-exact", RateQuote.class);

    assertTrue(got.isPresent());
    assertSame(stored, got.get(), "exact-type cache hits must avoid the Jackson conversion roundtrip");
    verify(template, atLeastOnce()).opsForValue();
  }

  /** Test-only POJO used to drive type coercion + passthrough paths. */
  static class RateQuote {
    public int amount;
    public String currency;

    public RateQuote() {}

    RateQuote(int amount, String currency) {
      this.amount = amount;
      this.currency = currency;
    }
  }
}
