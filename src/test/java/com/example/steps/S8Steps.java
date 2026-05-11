package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Account number is provided in the cmd construction in the 'When' step
    }

    @Given("a valid periodEnd is provided")
    public void aValidPeriodEndIsProvided() {
        // Period end is provided in the cmd construction in the 'When' step
    }

    @When("the GenerateStatementCmd command is executed")
    public void theGenerateStatementCmdCommandIsExecuted() {
        try {
            // Command setup with valid data matching the "Given"s
            cmd = new GenerateStatementCmd(
                "stmt-123",
                "acc-456",
                Instant.now(),
                new BigDecimal("100.00"),
                new BigDecimal("100.00") // Previous closing matches opening
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void aStatementGeneratedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent, "Event type mismatch");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        // Simulate closed period by pre-generating the statement
        aggregate = new StatementAggregate("stmt-123");
        // Force the aggregate into a generated state (simulating a closed period)
        GenerateStatementCmd initialCmd = new GenerateStatementCmd(
             "stmt-123", "acc-456", Instant.now(), new BigDecimal("100.00"), new BigDecimal("100.00"));
        aggregate.execute(initialCmd); 
        // Now aggregate is 'generated'. Attempting to execute again should violate the invariant.
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        aggregate = new StatementAggregate("stmt-123");
        // Setup command with mismatched opening/previous closing
        cmd = new GenerateStatementCmd(
            "stmt-123",
            "acc-456",
            Instant.now(),
            new BigDecimal("100.00"), // Opening
            new BigDecimal("50.00")   // Previous Closing (Mismatch!)
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Verify it's the specific domain error or a general IllegalStateException/IllegalArgumentException
        // depending on how strict the BDD text is. "Domain error" usually covers RuntimeExceptions in this context.
    }
}
