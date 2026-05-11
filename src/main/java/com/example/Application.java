package com.example;

import com.example.application.DefectReportService;
import com.example.domain.ports.SlackNotificationPort;
import com.example.domain.shared.Command;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application class.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Primary entry point for the Defect Reporting Logic.
     * This wiring allows the Temporal worker or any other entry point to trigger the flow.
     */
    @Bean
    public DefectWorkflow defectWorkflow(DefectReportService service) {
        return new DefectWorkflow(service);
    }

    /**
     * Inner class representing the Workflow Interface/Orchestrator.
     * In a real Temporal setup, this would implement the Workflow Interface.
     */
    public static class DefectWorkflow {
        private final DefectReportService service;

        public DefectWorkflow(DefectReportService service) {
            this.service = service;
        }

        public void executeReportDefect(Command cmd) {
            service.reportDefect(cmd);
        }
    }
}
