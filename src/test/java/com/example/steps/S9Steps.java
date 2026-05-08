package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCommand;
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
        aggregate = new StatementAggregate("stmt-123");
        // Configure valid state: period closed, balances present
        aggregate.configureStatement(
            new BigDecimal("100.00"), 
            new BigDecimal("200.00"), 
            "USD", 
            true // isPeriodClosed
        );
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled implicitly by the aggregate constructor in the previous step
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Validated implicitly in command creation
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCommand cmd = new ExportStatementCommand("stmt-123", "PDF");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("PDF", event.format());
    }

    // --- Failure Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-bad-period");
        // Configure invalid state: period OPEN
        aggregate.configureStatement(
            new BigDecimal("100.00"),
            new BigDecimal("200.00"),
            "USD",
            false // isPeriodClosed = false
        );
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate("stmt-bad-bal");
        // Configure invalid state: balances are null (simulating mismatch/missing data)
        aggregate.configureStatement(
            null, // null opening balance violates the invariant check
            new BigDecimal("200.00"),
            "USD",
            true
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // In this domain layer, we use IllegalStateException for invariant violations
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
