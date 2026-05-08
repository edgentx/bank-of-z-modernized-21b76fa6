package com.example.steps;

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
    private Exception thrownException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.setAccountId("acc-456");
        aggregate.setGenerated(true);
        aggregate.setClosedPeriod(false);
        aggregate.setBalances(BigDecimal.ZERO, BigDecimal.TEN);
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Handled in aggregate constructor or setup
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Handled in the command construction
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("PDF", event.format());
        assertNotNull(event.artifactId());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate("stmt-999");
        aggregate.setClosedPeriod(true); // Triggers violation
        aggregate.setGenerated(true);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMismatch() {
        aggregate = new StatementAggregate("stmt-888");
        aggregate.setGenerated(true);
        aggregate.setClosedPeriod(false);
        // Setup internal state to trigger the specific invariant check failure
        // In our simplified logic, Opening > Closing triggers the error
        aggregate.setBalances(new BigDecimal("100.00"), new BigDecimal("50.00"));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
