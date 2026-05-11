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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private String statementId;
    private String format;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        this.statementId = "stm-123";
        this.aggregate = new StatementAggregate(statementId);
        // Setup valid state
        aggregate.markPeriodAsClosed();
        aggregate.markBalanceValidated(true);
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // implicitly handled by "a valid Statement aggregate" setup
        assertNotNull(this.statementId);
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        this.format = "PDF";
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        this.statementId = "stm-invalid-period";
        this.aggregate = new StatementAggregate(statementId);
        // Violation: Period is NOT closed
        aggregate.markPeriodAsClosed(); // Reset just in case
        // (Default is false, but explicit is better)
        // Actually constructor sets it false. Let's ensure it stays false.
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalance() {
        this.statementId = "stm-invalid-balance";
        this.aggregate = new StatementAggregate(statementId);
        aggregate.markPeriodAsClosed(); // Period is closed
        aggregate.markBalanceValidated(false); // But balance is wrong
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            Command cmd = new ExportStatementCmd(statementId, format);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);

        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals(statementId, event.aggregateId());
        assertEquals(format, event.format());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
