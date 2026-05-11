package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
        aggregate = new StatementAggregate("stmt-1");
        aggregate.configureClosedPeriod(); // Closed period
        aggregate.setBalances(BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00)); // Matching balances
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // ID is implicit in the aggregate construction, but we ensure the aggregate is initialized
        if (aggregate == null) {
            throw new RuntimeException("Aggregate not initialized");
        }
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format will be provided in the When step
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            Command cmd = new ExportStatementCmd("stmt-1", "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-2");
        aggregate.configureOpenPeriod(); // Violation: Period is open
        aggregate.setBalances(BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-3");
        aggregate.configureClosedPeriod();
        // Violation: Mismatched balances
        aggregate.setBalances(BigDecimal.valueOf(90.00), BigDecimal.valueOf(100.00)); 
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof UnknownCommandException,
                   "Expected a domain error exception, got: " + caughtException.getClass().getSimpleName());
    }
}