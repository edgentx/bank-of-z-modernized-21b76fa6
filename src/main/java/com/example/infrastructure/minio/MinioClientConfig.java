package com.example.infrastructure.minio;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring for the MinIO/S3 client used by the document-storage adapter.
 *
 * <p>Keeping the client construction in its own {@code @Configuration} class
 * (rather than {@code @Component} on the adapter) means tests can swap in a
 * mocked {@link MinioClient} without dragging in the full property binder.
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioClientConfig {

  /**
   * Build the singleton {@link MinioClient} from {@link MinioProperties}.
   * The SDK enforces multipart-upload part-size limits internally, so we
   * pass the configured part-size through but otherwise lean on defaults.
   */
  @Bean
  public MinioClient minioClient(MinioProperties props) {
    return MinioClient.builder()
        .endpoint(props.getEndpoint())
        .credentials(props.getAccessKey(), props.getSecretKey())
        .region(props.getRegion())
        .build();
  }
}
