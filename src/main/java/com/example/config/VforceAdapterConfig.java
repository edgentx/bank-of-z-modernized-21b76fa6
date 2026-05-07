package com.example.config;

import com.example.adapters.SlackAdapter;
import com.example.adapters.VForce360Adapter;
import com.example.application.DefectReportingActivity;
import com.example.domain.slack.ports.SlackNotifierPort;
import com.example.domain.vforce.ports.VForce360Port;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VforceAdapterConfig {

    @Bean
    public VForce360Port vForce360Port() {
        return new VForce360Adapter();
    }

    @Bean
    public SlackNotifierPort slackNotifierPort() {
        return new SlackAdapter();
    }
}
