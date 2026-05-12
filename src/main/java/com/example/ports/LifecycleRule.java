package com.example.ports;

import java.util.Objects;

/**
 * Provider-neutral lifecycle rule for {@link DocumentStoragePort#applyLifecycleRules}.
 *
 * <p>Models the two policies BANK actually uses today: <em>expire objects
 * after N days</em> (scoped by an optional key prefix) and <em>abort dangling
 * multipart uploads after N days</em>. Both are first-class S3/MinIO
 * lifecycle primitives, so the adapter can translate this DTO 1:1 without
 * losing fidelity.
 */
public final class LifecycleRule {

  private final String id;
  private final String prefix;
  private final Integer expiryDays;
  private final Integer abortIncompleteMultipartUploadDays;
  private final boolean enabled;

  public LifecycleRule(String id, String prefix, Integer expiryDays,
                       Integer abortIncompleteMultipartUploadDays, boolean enabled) {
    this.id = Objects.requireNonNull(id, "id");
    this.prefix = prefix == null ? "" : prefix;
    this.expiryDays = expiryDays;
    this.abortIncompleteMultipartUploadDays = abortIncompleteMultipartUploadDays;
    this.enabled = enabled;
  }

  public static LifecycleRule expireAfterDays(String id, String prefix, int days) {
    return new LifecycleRule(id, prefix, days, null, true);
  }

  public String getId() {
    return id;
  }

  public String getPrefix() {
    return prefix;
  }

  public Integer getExpiryDays() {
    return expiryDays;
  }

  public Integer getAbortIncompleteMultipartUploadDays() {
    return abortIncompleteMultipartUploadDays;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
