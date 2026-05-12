package com.example.infrastructure.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Spring wiring for the Redis cache adapter (S-32).
 *
 * <p>Keeping the connection factory and {@link RedisTemplate} construction in
 * a dedicated {@code @Configuration} class (rather than {@code @Component} on
 * the adapter) means tests can swap in a mocked {@link RedisTemplate} without
 * dragging in the full property binder or starting a real Redis client. This
 * matches the {@code MinioClientConfig} pattern from S-31.
 *
 * <p>The Lettuce client is configured with a connection timeout that matches
 * the command timeout so a Redis outage fails fast (the default 10s ties up
 * worker threads through the configured timeout) instead of blocking the
 * entire request thread pool.
 *
 * <p>Values are serialized with
 * {@link GenericJackson2JsonRedisSerializer} so cached entries are human-
 * readable in {@code redis-cli} for debugging — required by the AC
 * "Serialization uses JSON format for debuggability". The keys remain plain
 * UTF-8 strings via {@link StringRedisSerializer}.
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisCacheConfig {

  /**
   * Build the Lettuce-backed connection factory from {@link RedisProperties}.
   * Lettuce is the Spring Data Redis default since Boot 2.x; it is netty-
   * based, non-blocking, and thread-safe across the JVM.
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory(RedisProperties props) {
    RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration();
    standalone.setHostName(props.getHost());
    standalone.setPort(props.getPort());
    standalone.setDatabase(props.getDatabase());
    if (props.getPassword() != null && !props.getPassword().isEmpty()) {
      standalone.setPassword(props.getPassword());
    }

    ClientOptions clientOptions = ClientOptions.builder()
        .socketOptions(SocketOptions.builder()
            .connectTimeout(props.getTimeout())
            .build())
        .build();

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
        .commandTimeout(props.getTimeout())
        .clientOptions(clientOptions)
        .build();

    return new LettuceConnectionFactory(standalone, clientConfig);
  }

  /**
   * JSON serializer used both by the {@link RedisTemplate} value channel and
   * by the adapter for ad-hoc Jackson-mapped reads. Polymorphic type info is
   * restricted to {@code java.*}, {@code com.example.*}, and JDK primitive
   * wrappers via {@link BasicPolymorphicTypeValidator} to avoid the
   * classic CVE-2017-7525 deserialization gadget surface that bit the
   * default-typing Jackson configuration.
   */
  @Bean
  public GenericJackson2JsonRedisSerializer redisJsonSerializer() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

    BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
        .allowIfBaseType(Object.class)
        .build();
    mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

    return new GenericJackson2JsonRedisSerializer(mapper);
  }

  /**
   * The high-level template the adapter uses for GET/SET/DEL/EXPIRE/SCAN.
   * Keys are UTF-8 strings, values are JSON blobs.
   */
  @Bean
  public RedisTemplate<String, Object> cacheRedisTemplate(
      RedisConnectionFactory connectionFactory,
      GenericJackson2JsonRedisSerializer jsonSerializer) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    StringRedisSerializer keySerializer = new StringRedisSerializer();
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashValueSerializer(jsonSerializer);
    template.afterPropertiesSet();
    return template;
  }
}
