package com.example.config;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for the Validation Context.
 * Wires the Aggregate with its necessary ports (adapters).
 */
@Configuration
public class ValidationConfig {

    /**
     * Factory for ValidationAggregate.
     * Note: Aggregates are usually created via Repository factories, but for
     * command handling or simple usage, we can define a prototype bean or helper.
     * Here we define a supplier/functional approach if needed, but standard Spring
     * injection is handled via constructors.
     */
    
    // We don't necessarily define a Singleton Bean for the Aggregate itself 
    // as Aggregates hold state. We inject the Ports into the Aggregate factory/handlers.
}
