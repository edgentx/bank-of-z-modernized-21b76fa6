package com.example.domain.defect;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefectAggregateTest {
    
    @Test
    public void testReportDefectGeneratesGitHubUrl() {
        // Given
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454 — GitHub URL in Slack body",
            "Defect reported by user.",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        
        // When
        var events = aggregate.execute(cmd);
        
        // Then
        assertThat(events).hasSize(1);
        var event = events.get(0);
        assertThat(event.aggregateId()).isEqualTo(defectId);
        assertThat(event.githubUrl()).isNotNull();
        assertThat(event.githubUrl()).isNotEmpty();
        assertThat(event.githubUrl()).contains("github.com");
    }
    
    @Test
    public void testDefectAggregateHasGitHubUrlAfterReporting() {
        // Given
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454 — GitHub URL in Slack body",
            "Defect reported by user.",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        
        // When
        aggregate.execute(cmd);
        
        // Then
        assertThat(aggregate.getGithubUrl()).isNotNull();
        assertThat(aggregate.getGithubUrl()).isNotEmpty();
        assertThat(aggregate.getGithubUrl()).contains("github.com");
    }
    
    @Test
    public void testCannotReportDefectWithoutTitle() {
        // Given
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "",
            "Defect reported by user.",
            "LOW",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        
        // When/Then
        assertThatThrownBy(() -> aggregate.execute(cmd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("title is required");
    }
    
    @Test
    public void testCannotReportDefectWithoutSeverity() {
        // Given
        String defectId = "VW-454";
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454 — GitHub URL in Slack body",
            "Defect reported by user.",
            "",
            "validation",
            "21b76fa6-afb6-4593-9e1b-b5d7548ac4d1"
        );
        
        // When/Then
        assertThatThrownBy(() -> aggregate.execute(cmd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("severity is required");
    }
}