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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        this.aggregate = new StatementAggregate("stmt-valid-123");
        // Set up valid state for happy path
        aggregate.setClosedPeriod(true);
        aggregate.setBalances(BigDecimal.valueOf(100.00), BigDecimal.valueOf(150.00));
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesPeriodClosed() {
        this.aggregate = new StatementAggregate("stmt-open-123");
        // State is open period
        aggregate.setClosedPeriod(false);
        aggregate.setBalances(BigDecimal.valueOf(100.00), BigDecimal.valueOf(150.00));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMismatch() {
        this.aggregate = new StatementAggregate("stmt-bad-bal-123");
        // Period is closed, but balances don't match previous (simulated by null or negative)
        aggregate.setClosedPeriod(true);
        // Simulating a state that triggers the specific error message or null
        aggregate.setBalances(null, BigDecimal.valueOf(150.00));
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Handled by the aggregate construction in previous steps
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Handled in the When step
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNotNull(event.occurredAt());
        assertEquals("PDF", event.format());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Checking for IllegalStateException based on aggregate logic
        assertTrue(thrownException instanceof IllegalStateException);
    }
}