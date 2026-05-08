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
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-valid-1");
        // Setup valid state
        aggregate.setClosedPeriod(true);
        aggregate.setOpeningBalance(BigDecimal.valueOf(100.00));
    }

    @Given("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Handled in the command construction in 'When'
    }

    @Given("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Handled in the command construction in 'When'
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        assertEquals("statement.exported", resultEvents.get(0).type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate("stmt-invalid-closed");
        aggregate.setClosedPeriod(false); // Violation: period is open
        aggregate.setOpeningBalance(BigDecimal.ZERO);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMismatch() {
        aggregate = new StatementAggregate("stmt-invalid-bal");
        aggregate.setClosedPeriod(true); // Period is closed, so we hit the next check
        // Simulate the invalid state used in Aggregate logic
        aggregate.markAsBalanceMismatch();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException ||
                   caughtException instanceof UnknownCommandException);
    }
}
