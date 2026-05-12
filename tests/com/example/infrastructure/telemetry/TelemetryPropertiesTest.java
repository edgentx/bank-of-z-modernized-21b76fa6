package com.example.infrastructure.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * BANK S-35 — coverage for {@link TelemetryProperties} default values and
 * setter round-trip.
 *
 * <p>The acceptance criterion "Sampling configuration is adjustable via
 * environment variables" requires the {@code telemetry.otel.*} prefix to
 * round-trip cleanly through {@code @ConfigurationProperties}; these tests
 * pin the defaults so an accidental rename of a setter breaks the build
 * rather than silently dropping a tunable.
 */
class TelemetryPropertiesTest {

  @Test
  void defaultsAreSafeForLocalDev() {
    TelemetryProperties props = new TelemetryProperties();

    assertFalse(props.isEnabled(), "OTel disabled by default — opt-in via env var");
    assertEquals("teller-core", props.getServiceName());
    assertEquals("dev", props.getEnvironment());
    assertEquals("http://localhost:4317", props.getOtlpEndpoint());
    assertEquals(Duration.ofSeconds(10), props.getOtlpTimeout());
    assertEquals(1.0, props.getSamplingRatio(), 1e-9);
    assertEquals(2048, props.getMaxQueueSize());
    assertEquals(Duration.ofMillis(500), props.getScheduleDelay());
  }

  @Test
  void settersUpdateAllTunables() {
    TelemetryProperties props = new TelemetryProperties();
    props.setEnabled(true);
    props.setServiceName("teller-core-prod");
    props.setEnvironment("prod");
    props.setOtlpEndpoint("http://otel-collector.observability.svc:4317");
    props.setOtlpTimeout(Duration.ofSeconds(20));
    props.setSamplingRatio(0.1);
    props.setMaxQueueSize(8192);
    props.setScheduleDelay(Duration.ofSeconds(2));

    assertTrue(props.isEnabled());
    assertEquals("teller-core-prod", props.getServiceName());
    assertEquals("prod", props.getEnvironment());
    assertEquals("http://otel-collector.observability.svc:4317", props.getOtlpEndpoint());
    assertEquals(Duration.ofSeconds(20), props.getOtlpTimeout());
    assertEquals(0.1, props.getSamplingRatio(), 1e-9);
    assertEquals(8192, props.getMaxQueueSize());
    assertEquals(Duration.ofSeconds(2), props.getScheduleDelay());
  }
}
