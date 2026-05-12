package com.example.ports;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Result of {@link DocumentStoragePort#download}: the live byte stream plus
 * the metadata REST controllers need to set proper response headers.
 *
 * <p>Implements {@link Closeable} so callers can use it inside a try-with-
 * resources block. Closing the wrapper closes the underlying network stream.
 */
public final class DocumentStream implements Closeable {

  private final InputStream content;
  private final String contentType;
  private final long size;

  public DocumentStream(InputStream content, String contentType, long size) {
    this.content = Objects.requireNonNull(content, "content");
    this.contentType = contentType;
    this.size = size;
  }

  public InputStream content() {
    return content;
  }

  /** MIME type echoed back from object metadata; may be null if not set on upload. */
  public String contentType() {
    return contentType;
  }

  /** Object length in bytes, or {@code -1} when not reported. */
  public long size() {
    return size;
  }

  @Override
  public void close() throws IOException {
    content.close();
  }
}
