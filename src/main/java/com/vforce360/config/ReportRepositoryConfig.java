package com.vforce360.config;

import com.mongodb.client.MongoClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vforce360.ports.MarReportPort;
import com.vforce360.adapters.MongoModernizationReportAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportRepositoryConfig {

    @Bean
    public MarReportPort marReportPort(MongoClient mongoClient, ObjectMapper mapper) {
        // Assuming we fix the Adapter constructor to match this signature
        return new MongoModernizationReportAdapter(mongoClient, mapper);
    }
}