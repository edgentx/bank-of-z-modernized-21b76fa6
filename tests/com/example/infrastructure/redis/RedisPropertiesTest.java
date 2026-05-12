package com.example.infrastructure.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

/**
 * BANK S-32 — TTL resolution rules for {@link RedisProperties}.
 *
 * <p>The acceptance criterion "TTL policies are configurable per cache
 * region" implies a precedence chain: region-specific override > global
 * default. {@link RedisProperties#ttlFor(String)} is the only place that
 * chain is evaluated, so it gets its own focused tests.
 */
class RedisPropertiesTest {

  @Test
  void ttlForUsesDefaultWhenRegionUnknown() {
    RedisProperties props = new RedisProperties();
    props.setDefaultTtl(Duration.ofMinutes(7));

    assertEquals(Duration.ofMinutes(7), props.ttlFor("not-configured"));
  }

  @Test
  void ttlForUsesRegionOverrideWhenPresent() {
    RedisProperties props = new RedisProperties();
    props.setDefaultTtl(Duration.ofMinutes(10));
    RedisProperties.Region region = new RedisProperties.Region();
    region.setTtl(Duration.ofSeconds(15));
    HashMap<String, RedisProperties.Region> regions = new HashMap<>();
    regions.put("session", region);
    props.setRegions(regions);

    assertEquals(Duration.ofSeconds(15), props.ttlFor("session"));
  }

  @Test
  void ttlForFallsBackToDefaultWhenRegionHasNullTtl() {
    RedisProperties props = new RedisProperties();
    props.setDefaultTtl(Duration.ofMinutes(3));
    RedisProperties.Region region = new RedisProperties.Region(); // TTL left null
    HashMap<String, RedisProperties.Region> regions = new HashMap<>();
    regions.put("partial", region);
    props.setRegions(regions);

    assertEquals(Duration.ofMinutes(3), props.ttlFor("partial"));
  }
}
