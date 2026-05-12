package com.example.ports;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

/**
 * BANK S-31 — Hex port for document object-storage.
 *
 * <p>Backed by an S3-compatible blob store (MinIO in the modernized stack, but
 * the contract is provider-neutral so the same port can sit in front of AWS
 * S3 / GCS / Azure Blob when a deployment requires it). Used by the statement,
 * export, and customer-document slices to persist generated artifacts that
 * are too large or too binary to belong in Mongo.
 */
public interface DocumentStoragePort {

  /**
   * Upload a document to {@code bucket} under {@code objectKey}. The caller
   * is responsible for closing {@code content} after the method returns. The
   * adapter implementation must handle multipart streaming for large
   * payloads (anything above the configured part-size threshold).
   *
   * @param size content length in bytes, or {@code -1} when unknown; in the
   *             unknown case the adapter MUST fall back to its multipart
   *             stream upload path.
   * @param contentType MIME type, e.g. {@code application/pdf}; stored as
   *             object metadata so {@link #download} can echo it back.
   */
  void upload(String bucket, String objectKey, InputStream content, long size, String contentType);

  /**
   * Stream the document body and its stored {@code contentType} back to the
   * caller. The returned stream is a live network stream and MUST be closed
   * after consumption.
   *
   * @throws DocumentNotFoundException when the object does not exist.
   * @throws BucketNotFoundException when the bucket does not exist.
   */
  DocumentStream download(String bucket, String objectKey);

  /** Delete an object. No-op when the object is already absent. */
  void delete(String bucket, String objectKey);

  /**
   * Generate a pre-signed GET URL the caller can hand to a browser for
   * direct, time-limited access without proxying bytes through the
   * application tier.
   */
  String presignedGetUrl(String bucket, String objectKey, Duration expiry);

  /**
   * Apply (replace) the lifecycle configuration on {@code bucket}. Used at
   * deploy time to enforce retention policies for statement exports and
   * temporary upload buckets without manual S3 console work.
   */
  void applyLifecycleRules(String bucket, List<LifecycleRule> rules);

  /** Whether {@code bucket} exists. */
  boolean bucketExists(String bucket);
}
