package com.example.steps;

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
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> result;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        this.aggregate = new StatementAggregate("stmt-123");
        // Setup valid state
        aggregate.setAccountAndBalances("acct-1", BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00));
        aggregate.setPeriodClosed(true);
        aggregate.setGenerated(true);
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Handled by aggregate constructor in 'aValidStatementAggregate'
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Handled inside execute step
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
        try {
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) result.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("PDF", event.format());
        assertTrue(event.artifactLocation().endsWith(".PDF"));
    }

    @Given("A Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesPeriodClosed() {
        this.aggregate = new StatementAggregate("stmt-invalid-period");
        aggregate.setAccountAndBalances("acct-1", BigDecimal.ZERO, BigDecimal.ZERO);
        aggregate.setPeriodClosed(false); // Violation: Period is open
        aggregate.setGenerated(true);
    }

    @Given("A Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMismatch() {
        this.aggregate = new StatementAggregate("stmt-invalid-bal");
        // Violation: Opening (100) != Closing (200)
        aggregate.setAccountAndBalances("acct-1", BigDecimal.valueOf(100), BigDecimal.valueOf(200));
        aggregate.setPeriodClosed(true);
        aggregate.setGenerated(true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof UnknownCommandException);
    }
}