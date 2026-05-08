package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private String statementId;
    private String format;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // Create a valid aggregate (closed period, balanced)
        statementId = "stmt-123";
        aggregate = new StatementAggregate(statementId, true, false);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        // Force aggregate into invalid state for this invariant (period not closed)
        statementId = "stmt-invalid-period";
        aggregate = new StatementAggregate(statementId, false, false);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        // Force aggregate into invalid state for this invariant (mismatch)
        statementId = "stmt-invalid-balance";
        aggregate = new StatementAggregate(statementId, true, true);
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // statementId is initialized in the Given blocks
        assertNotNull(statementId);
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        this.format = "PDF";
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            Command cmd = new ExportStatementCmd(statementId, format);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertInstanceOf(StatementExportedEvent.class, event);
        
        StatementExportedEvent exportedEvent = (StatementExportedEvent) event;
        assertEquals("statement.exported", exportedEvent.type());
        assertEquals(statementId, exportedEvent.aggregateId());
        assertNotNull(exportedEvent.occurredAt());
        assertEquals(format, exportedEvent.format());
        assertNotNull(exportedEvent.artifactLocation());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain exception but command succeeded.");
        // Verifying it's a standard Java state exception which implies a domain rule violation
        assertInstanceOf(IllegalStateException.class, capturedException);
    }
}
