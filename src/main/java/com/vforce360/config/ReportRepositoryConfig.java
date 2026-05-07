package com.vforce360.config;

import com.vforce360.ports.IModernizationReportRepository;
import com.vforce360.adapters.MongoModernizationReportAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for switching between Real Adapters and Mocks.
 * 
 * Uses application properties to determine the implementation.
 * Default: Real MongoDB Adapter.
 */
@Configuration
public class ReportRepositoryConfig {

    @Bean
    @ConditionalOnProperty(
        name = "app.report.repository.impl", 
        havingValue = "mongo", 
        matchIfMissing = true
    )
    public IModernizationReportRepository mongoRepository(
            com.mongodb.client.MongoClient mongoClient, 
            com.fasterxml.jackson.databind.ObjectMapper mapper) {
        return new MongoModernizationReportAdapter(mongoClient, mapper);
    }

    // Note: The Mock implementation is primarily used in tests (@SpringBootTest context)
    // but could be wired here for local dev profiles if needed.
    /*
    @Bean
    @ConditionalOnProperty(name = "app.report.repository.impl", havingValue = "mock")
    public IModernizationReportRepository mockRepository() {
        return new MockModernizationReportRepository();
    }
    */
}
