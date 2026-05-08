package com.example.config;

import com.example.adapters.PostgresVForce360Repository;
import com.example.domain.vforce360.repository.VForce360Repository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Repository configuration to wire up the Real Adapter.
 */
@Configuration
public class RepoConfig {

    /**
     * Real implementation of VForce360Repository.
     * In a full Spring Boot app, this would likely extend JpaRepository for the entity mapping.
     * For this fix, we provide a stub that satisfies the interface.
     */
    @Bean
    @ConditionalOnProperty(name = "repository.impl", havingValue = "postgres", matchIfMissing = true)
    public VForce360Repository vForce360Repository() {
        // In a real scenario, we would inject a DAO here.
        // For the purpose of fixing the compilation and satisfying the interface requirement:
        return new PostgresVForce360Repository();
    }
}
