package com.example.steps;

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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-valid-1");
        aggregate.markAsGenerated();
        // Set defaults to satisfy invariants for a happy path
        aggregate.configureClosedPeriod(true);
        aggregate.setBalances(BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00));
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by aggregate construction in previous step
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in the execution step
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-invalid-period");
        aggregate.markAsGenerated();
        // Violation: Period is not closed
        aggregate.configureClosedPeriod(false);
        // Ensure other invariants are met to isolate this failure
        aggregate.setBalances(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
        // Verify the message matches the invariant rule
        assertTrue(caughtException.getMessage().contains("closed period") || 
                   caughtException.getMessage().contains("retroactively"));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        this.aggregate = new StatementAggregate("stmt-invalid-balance");
        aggregate.markAsGenerated();
        // Satisfy other invariants
        aggregate.configureClosedPeriod(true);
        // Violation: Mismatched balances
        aggregate.setBalances(BigDecimal.valueOf(50.00), BigDecimal.valueOf(100.00));
    }
}