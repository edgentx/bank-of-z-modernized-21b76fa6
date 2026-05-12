package com.example.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc auto-scans @RestController + @Tag annotations to generate the
 * OpenAPI 3 schema at /v3/api-docs (JSON) and /swagger-ui.html (interactive
 * UI). This bean only provides the document metadata.
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI tellerCoreOpenAPI() {
    return new OpenAPI().info(
        new Info()
            .title("Teller Core API")
            .version("0.0.1-SNAPSHOT")
            .description("REST API for the Bank-of-Z modernized teller-core platform "
                + "(customer-management, account-management, transaction-processing, "
                + "user-interface-navigation, legacy-bridge bounded contexts)."));
  }
}
