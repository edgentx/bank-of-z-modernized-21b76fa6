package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-123");
        this.aggregate.setAccountId("acct-456");
        this.aggregate.setIsClosedPeriod(true);
        this.aggregate.setMatchesPreviousBalance(true);
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by aggregate constructor in previous step
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in the command execution step
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("PDF", event.format());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-invalid-period");
        this.aggregate.setAccountId("acct-456");
        // Setup to violate the invariant
        this.aggregate.setIsClosedPeriod(false); 
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        this.aggregate = new StatementAggregate("stmt-invalid-bal");
        this.aggregate.setAccountId("acct-456");
        // Setup to satisfy closed period but violate balance
        this.aggregate.setIsClosedPeriod(true);
        this.aggregate.setMatchesPreviousBalance(false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
