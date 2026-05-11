package com.example.config;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.VForce360RepositoryPort;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class DefectWorkflowConfig {

    // Factory to create aggregate instances within workflows
    public static class DefectAggregateFactory implements Function<String, DefectAggregate> {
        private final VForce360RepositoryPort repository;

        public DefectAggregateFactory(VForce360RepositoryPort repository) {
            this.repository = repository;
        }

        @Override
        public DefectAggregate apply(String defectId) {
            return repository.findById(defectId).orElseGet(() -> new DefectAggregate(defectId));
        }
    }
}