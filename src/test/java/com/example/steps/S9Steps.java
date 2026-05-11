package com.example.steps;

import com.example.domain.shared.Command;
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
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-123");
        // Setup valid state: Period is closed
        aggregate.setPeriodClosed(true);
        // Setup valid state: Balances match
        aggregate.setBalances(new BigDecimal("100.00"), new BigDecimal("100.00"));
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate("stmt-456");
        // Setup invalid state: Period is NOT closed
        aggregate.setPeriodClosed(false);
        aggregate.setBalances(new BigDecimal("100.00"), new BigDecimal("100.00"));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMatching() {
        aggregate = new StatementAggregate("stmt-789");
        // Setup valid state for other invariants
        aggregate.setPeriodClosed(true);
        // Setup invalid state: Balances do not match (Opening 100, Prev Close 90)
        aggregate.setBalances(new BigDecimal("100.00"), new BigDecimal("90.00"));
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // ID is implicitly handled by the aggregate instance
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Format is part of the command setup in the 'When' step
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            Command cmd = new ExportStatementCmd(aggregate.id(), "PDF");
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
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals("PDF", event.format());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
