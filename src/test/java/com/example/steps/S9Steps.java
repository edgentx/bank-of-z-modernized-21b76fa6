package com.example.steps;

import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S9Steps {

    private StatementAggregate aggregate;
    private String statementId;
    private String format;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        this.statementId = "stmt-123";
        this.aggregate = new StatementAggregate(statementId);
        // Simulate the aggregate being in a valid, generated state.
        // In a full repo, we would replay events, but for unit testing we instantiate directly.
        // We assume the constructor or a factory puts it in a state where it can be exported.
    }

    @Given("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Setup in previous step
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        this.format = "PDF";
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        Command cmd = new ExportStatementCmd(statementId, format);
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        Assertions.assertEquals("PDF", event.format());
        Assertions.assertNotNull(event.artifactId());
    }

    // --- Rejection Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesPeriodClosed() {
        this.statementId = "stmt-invalid-period";
        // We construct the aggregate in a state that will fail validation.
        // The aggregate's logic checks if it is retroactively alterable.
        this.aggregate = new StatementAggregate(statementId);
        this.aggregate.markAsRetroactivelyAlterable(); // If this is true, validation passes. If false (default), and we require open period, it might fail.
        // For this specific scenario, we assume the aggregate is in a state where it considers the period "closed".
        // In this specific implementation, the invariant is enforced by checking a flag or state.
        // We will simulate a state that is "closed for modification".
        this.aggregate.markPeriodClosed(); // This method sets the internal flag to closed.
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMismatch() {
        this.statementId = "stmt-bad-balance";
        this.aggregate = new StatementAggregate(statementId);
        this.format = "PDF";
        // Set the previous closing balance to something different from current opening
        this.aggregate.setPreviousStatementClosingBalance(new BigDecimal("100.00"));
        this.aggregate.setCurrentOpeningBalance(new BigDecimal("50.00")); // Mismatch
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // We expect a runtime exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        Assertions.assertNull(resultEvents);
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvidedForRejection() {
        this.format = "PDF";
    }

}
