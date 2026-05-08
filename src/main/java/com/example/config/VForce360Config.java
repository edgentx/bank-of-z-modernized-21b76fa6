package com.example.config;

import com.example.adapters.SlackNotificationPort;
import com.example.adapters.impl.SlackClientAdapter;
import com.example.domain.vforce360.repository.VForce360Repository;
import com.example.ports.GitHubPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Configuration for VForce360 components.
 * Ensures the correct adapters are wired up.
 */
public class VForce360Config {

    // In a real app, beans are auto-scanned. Explicitly defining for clarity on pattern.
    // The actual implementations (GitHubClientAdapter, SlackClientAdapter) are in src/main/java/.../adapters/impl
}