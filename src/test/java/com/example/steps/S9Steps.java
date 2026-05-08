package com.example.steps;

import com.example.domain.statement.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S9Steps {

    private StatementAggregate statement;
    private List<com.example.domain.shared.DomainEvent> results;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        statement = new StatementAggregate("stmt-1", "acct-1", Instant.now().minusSeconds(86400), Instant.now());
        statement.openingBalance = BigDecimal.ZERO;
        statement.closingBalance = BigDecimal.TEN;
        statement.closed = false;
        statement.reconciled = true;
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period_constraint() {
        statement = new StatementAggregate("stmt-2", "acct-1", Instant.now().minusSeconds(86400*30), Instant.now().minusSeconds(86400*15));
        statement.openingBalance = BigDecimal.ZERO;
        statement.closingBalance = BigDecimal.TEN;
        statement.closed = true; 
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching_constraint() {
        statement = new StatementAggregate("stmt-3", "acct-1", Instant.now().minusSeconds(86400), Instant.now());
        statement.openingBalance = BigDecimal.ONE; 
        statement.previousClosingBalance = BigDecimal.ZERO; 
        statement.closingBalance = BigDecimal.TEN;
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled in aggregate initialization
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in command execution
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd(statement.id(), "PDF");
        try {
            results = statement.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(results, "Results should not be null");
        Assertions.assertFalse(results.isEmpty(), "Results should contain at least one event");
        Assertions.assertTrue(results.get(0) instanceof StatementExportedEvent, "First event should be StatementExportedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Depending on implementation, this could be IllegalStateException, IllegalArgumentException, etc.
        // We check that it is a RuntimeException (domain logic error)
        Assertions.assertTrue(caughtException instanceof RuntimeException, "Expected RuntimeException");
    }
}
