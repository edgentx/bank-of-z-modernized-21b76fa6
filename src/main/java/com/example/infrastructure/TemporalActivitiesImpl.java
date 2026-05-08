package com.example.infrastructure;

import com.example.application.DefectReportService;
import com.example.domain.vforce360.ReportDefectCommand;
import io.temporal.activity.ActivityInterface;
import io.temporal.spring.boot.ActivityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal Activity implementation.
 * Bridges the Temporal workflow execution with the Spring Application Service.
 */
@Component
@ActivityImpl(taskQueues = "VForce360TaskQueue")
public class TemporalActivitiesImpl implements VForce360Activities {

    private static final Logger log = LoggerFactory.getLogger(TemporalActivitiesImpl.class);
    private final DefectReportService defectReportService;

    public TemporalActivitiesImpl(DefectReportService defectReportService) {
        this.defectReportService = defectReportService;
    }

    @Override
    public void reportDefect(ReportDefectCommand cmd) {
        log.info("Temporal Activity: Executing reportDefect for {}", cmd.defectId());
        defectReportService.reportDefect(cmd);
    }
}
