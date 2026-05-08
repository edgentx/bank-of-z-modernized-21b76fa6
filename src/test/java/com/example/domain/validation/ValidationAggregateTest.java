package com.example.domain.validation;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

/*
 * Test Driven Development (Red Phase)
 * Story: S-FB-1
 * Target: Validation Aggregate logic defect reporting
 */
public class ValidationAggregateTest {

    @Test
    public void test_report_defect_command_generates_event_with_ticket_url() {
        // Arrange
        String defectId = "VW-454";
        ValidationAggregate aggregate = new ValidationAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(
            defectId,
            "Validating VW-454 — GitHub URL in Slack body",
            "LOW",
            "validation"
        );

        // Act
        var events = aggregate.execute(cmd);

        // Assert
        assertThat(events).hasSize(1);
        
        ValidationReportedEvent event = (ValidationReportedEvent) events.get(0);
        assertThat(event.aggregateId()).isEqualTo(defectId);
        assertThat(event.description()).contains("GitHub URL");
        assertThat(event.severity()).isEqualTo("LOW");
        
        // CRITICAL ACCEPTANCE CRITERION: Verify GitHub URL is present
        assertThat(event.ticketUrl()).isNotBlank();
        assertThat(event.ticketUrl()).startsWith("https://github.com");
        assertThat(aggregate.getTicketUrl()).isNotNull();
    }

    @Test
    public void test_report_defect_fails_with_blank_description() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("ID-1");
        ReportDefectCmd cmd = new ReportDefectCmd("ID-1", "", "LOW", "validation");

        // Act & Assert
        assertThatThrownBy(() -> aggregate.execute(cmd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("description required");
    }

    @Test
    public void test_report_defect_fails_with_null_defect_id() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate(null);
        ReportDefectCmd cmd = new ReportDefectCmd(null, "desc", "LOW", "validation");

        // Act & Assert
        assertThatThrownBy(() -> aggregate.execute(cmd))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void test_unsupported_command_throws_exception() {
        // Arrange
        ValidationAggregate aggregate = new ValidationAggregate("ID-1");
        String cmdString = "InvalidCommand";
        
        // Creating a proxy or anonymous class for the interface to test failure path
        Command invalidCmd = () -> "InvalidCommand";

        // Act & Assert
        assertThatThrownBy(() -> aggregate.execute(invalidCmd))
            .isInstanceOf(UnknownCommandException.class);
    }
}