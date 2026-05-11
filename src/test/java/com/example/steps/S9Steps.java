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

/**
 * Cucumber Steps for S-9: ExportStatementCmd.
 */
public class S9Steps {

    private StatementAggregate aggregate;
    private ExportStatementCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // Create a fresh aggregate
        aggregate = new StatementAggregate("stmt-valid-123");
        
        // Setup valid state: Period is closed, balances are assumed valid
        aggregate.setPeriodClosed(true);
        aggregate.setStatus(StatementAggregate.Status.GENERATED);
        aggregate.markAsViolatingOpeningBalanceInvariant(false);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-open-123");
        
        // Setup invalid state: Period is OPEN
        aggregate.setPeriodClosed(false);
        aggregate.setStatus(StatementAggregate.Status.GENERATED);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-balance-bad-123");
        
        // Setup valid state for closed period, but invalid balance continuity
        aggregate.setPeriodClosed(true);
        aggregate.setStatus(StatementAggregate.Status.GENERATED);
        
        // Mark this specific invariant as violated
        aggregate.markAsViolatingOpeningBalanceInvariant(true);
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // ID is implicitly provided by the aggregate initialization in the Given steps
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // We create the command here assuming the ID from the aggregate
        this.command = new ExportStatementCmd(aggregate.id(), "PDF");
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
        
        StatementExportedEvent exportedEvent = (StatementExportedEvent) event;
        assertEquals("statement.exported", exportedEvent.type());
        assertEquals(aggregate.id(), exportedEvent.aggregateId());
        assertEquals("PDF", exportedEvent.format());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // In Java domain modeling, this is often an IllegalStateException or a custom DomainException
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
    }
}
