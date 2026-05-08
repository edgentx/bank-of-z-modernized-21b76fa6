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
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesRetroactive() {
        aggregate = new StatementAggregate("stmt-retro");
        aggregate.markAsRetroactive();
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMismatch() {
        aggregate = new StatementAggregate("stmt-mismatch");
        aggregate.markWithBalanceMismatch();
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Handled by aggregate construction in Given steps
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Format provided in the When step
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the ExportStatementCmd command is executed on violating aggregate")
    public void theExportStatementCmdCommandIsExecutedOnViolatingAggregate() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        assertEquals("statement.exported", resultEvents.get(0).type());
        assertNull(caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        // Verify the message matches one of the invariants
        String msg = caughtException.getMessage();
        assertTrue(msg.contains("retroactively") || msg.contains("closing balance"));
        assertNull(resultEvents); // No events should be emitted
    }
}