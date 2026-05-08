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
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S9Steps {

    private StatementAggregate statement;
    private String statementId;
    private String format;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.statementId = "stmt-123";
        this.statement = new StatementAggregate(statementId);
        this.statement.configureValidState();
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_closed_period() {
        this.statementId = "stmt-closed";
        this.statement = new StatementAggregate(statementId);
        this.statement.configureClosedPeriodViolation();
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_balance_mismatch() {
        this.statementId = "stmt-mismatch";
        this.statement = new StatementAggregate(statementId);
        this.statement.configureBalanceMismatchViolation();
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled in aggregate setup, but ensuring local state matches
        if (this.statement == null) {
            throw new RuntimeException("Statement aggregate not initialized");
        }
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        this.format = "PDF";
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            Command cmd = new ExportStatementCmd(this.statementId, this.format);
            this.resultEvents = this.statement.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(this.resultEvents);
        assertEquals(1, this.resultEvents.size());
        assertTrue(this.resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) this.resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals(this.statementId, event.aggregateId());
        assertEquals("PDF", event.format());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(this.caughtException);
        assertTrue(this.caughtException instanceof IllegalStateException);
    }
}