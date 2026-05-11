package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryAccountRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private OpenAccountCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Setup for a fresh aggregate per scenario
    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        String newId = java.util.UUID.randomUUID().toString();
        aggregate = new AccountAggregate(newId);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId handled in command construction below, just flag valid here
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // accountType handled in command construction below
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // initialDeposit handled in command construction below
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // sortCode handled in command construction below
    }

    // Helper to build the command with default valid values for the success case
    private OpenAccountCmd buildDefaultCommand() {
        return new OpenAccountCmd(
            aggregate.id(),
            "cust-123",
            "CHECKING",
            new BigDecimal("500.00"),
            "10-20-30"
        );
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        // Assume the 'valid' steps imply we use the default builder, 
        // or we could store state in fields, but defaults work for the BDD flow described.
        // If specific violations are needed, we modify the command in the 'Given violation' steps.
        
        if (command == null) {
            // No specific override command set, use default valid one
            command = buildDefaultCommand();
        }

        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("account.opened", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        aggregate = new AccountAggregate(java.util.UUID.randomUUID().toString());
        // Setup command to fail: SAVINGS requires 100, we give 50
        command = new OpenAccountCmd(
            aggregate.id(),
            "cust-123",
            "SAVINGS",
            new BigDecimal("50.00"),
            "10-20-30"
        );
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // Simulate an account that is already ACTIVE (or opened) and try to open it again.
        aggregate = new AccountAggregate(java.util.UUID.randomUUID().toString());
        // Open it once
        aggregate.execute(new OpenAccountCmd(aggregate.id(), "c", "CHECKING", BigDecimal.ZERO, "sc"));
        // Now trying to execute OpenAccountCmd again violates state requirements (cannot open already active)
        command = new OpenAccountCmd(
            aggregate.id(),
            "cust-123",
            "CHECKING",
            new BigDecimal("100.00"),
            "10-20-30"
        );
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        aggregate = new AccountAggregate("fixed-immutable-id");
        // Open it once, setting immutable flag to true
        aggregate.execute(new OpenAccountCmd(aggregate.id(), "c", "CHECKING", BigDecimal.ZERO, "sc"));
        // Try to open it again with the same ID
        command = new OpenAccountCmd(
            "fixed-immutable-id", // Same ID
            "cust-456",
            "CHECKING",
            new BigDecimal("100.00"),
            "10-20-30"
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
