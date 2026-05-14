package com.example.deploy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.infrastructure.db2.history.AccountHistoryEntity;
import com.example.infrastructure.db2.history.AccountHistoryRepository;
import com.example.infrastructure.db2.history.TransactionHistoryEntity;
import com.example.infrastructure.db2.history.TransactionHistoryRepository;
import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("vforce_dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
class VforceDevHistoryProfileTest {

  @Configuration
  @EnableAutoConfiguration
  @EntityScan(basePackageClasses = {AccountHistoryEntity.class, TransactionHistoryEntity.class})
  @EnableJpaRepositories(basePackageClasses = {
      AccountHistoryRepository.class,
      TransactionHistoryRepository.class
  })
  static class TestConfig {}

  @Autowired private DataSource dataSource;

  @Test
  void vforceDevProfileStartsHistoryDatasourceWithoutLocalhostDb2() throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      String url = connection.getMetaData().getURL();

      assertTrue(url.startsWith("jdbc:h2:mem:bank-vforce-dev"),
          "vforce_dev must use the embedded DB2-compatible history datasource");
      assertFalse(url.contains("localhost:50000"),
          "vforce_dev must not attempt DB2 history connections against localhost");
      assertTrue(connection.createStatement()
              .executeQuery("select count(*) from transaction_history")
              .next(),
          "Flyway must create the history schema under the vforce_dev profile");
    }
  }
}
