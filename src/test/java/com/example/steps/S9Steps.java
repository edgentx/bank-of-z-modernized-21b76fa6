package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1", "acct-1");
        aggregate.apply(new StatementGeneratedEvent("stmt-1", "acct-1", new BigDecimal("100.00"), new BigDecimal("150.00"), false, java.time.Instant.now()));
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // ID is set in aggregate creation
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format will be provided in command construction
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-2", "acct-2");
        aggregate.apply(new StatementGeneratedEvent("stmt-2", "acct-2", new BigDecimal("0.00"), new BigDecimal("0.00"), true, java.time.Instant.now()));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-3", "acct-3");
        // Create a scenario where internal checks might fail, though modeled via domain validation
        aggregate.apply(new StatementGeneratedEvent("stmt-3", "acct-3", new BigDecimal("999.99"), new BigDecimal("100.00"), false, java.time.Instant.now()));
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull("Expected no exception", caughtException);
        assertNotNull("Expected events to be generated", resultEvents);
        assertEquals("Expected 1 event", 1, resultEvents.size());
        assertTrue("Expected StatementExportedEvent", resultEvents.get(0) instanceof StatementExportedEvent);

        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("PDF", event.format());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        assertTrue("Expected IllegalStateException or IllegalArgumentException",
                   caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}