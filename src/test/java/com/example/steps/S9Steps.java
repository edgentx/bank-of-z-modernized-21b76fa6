package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private ExportStatementCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate("stmt-violation-closed");
        // Force the aggregate into a state where it is closed but altered
        aggregate.markAsClosedAndAltered();
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        aggregate = new StatementAggregate("stmt-violation-balance");
        // Force the aggregate into a state where opening balance is mismatched
        aggregate.markAsBalanceMismatch();
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Handled implicitly by the aggregate construction or specific setup if needed
        if (aggregate == null) {
            aggregate = new StatementAggregate("stmt-456");
        }
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // The cmd construction is handled in the 'When' step to keep state clean
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            // We use a default valid format. If invalid format was needed, it would be in the Given/And steps.
            cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
        
        StatementExportedEvent exportedEvent = (StatementExportedEvent) event;
        assertEquals("statement.exported", exportedEvent.type());
        assertEquals(aggregate.id(), exportedEvent.aggregateId());
        assertEquals("PDF", exportedEvent.format());
        assertNotNull(exportedEvent.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown, but none was caught");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
