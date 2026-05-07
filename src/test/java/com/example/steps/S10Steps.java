package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private DomainException caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        transaction = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        if (command == null) command = new PostDepositCmd("ACC-123", BigDecimal.ONE, "USD");
        command.accountNumber = "ACC-VALID-100";
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (command == null) command = new PostDepositCmd("ACC-123", BigDecimal.ONE, "USD");
        command.amount = BigDecimal.TEN;
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (command == null) command = new PostDepositCmd("ACC-123", BigDecimal.ONE, "USD");
        command.currency = "USD";
    }

    // -- Violations --

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        transaction = new Transaction(UUID.randomUUID());
        command = new PostDepositCmd("ACC-123", BigDecimal.ZERO, "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_cannot_be_altered() {
        transaction = new Transaction(UUID.randomUUID());
        // Simulate posted state
        transaction.markPosted();
        command = new PostDepositCmd("ACC-123", BigDecimal.TEN, "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        transaction = new Transaction(UUID.randomUUID());
        // Set a constraint that would fail (e.g. max balance)
        transaction.setMaxBalance(BigDecimal.ZERO); 
        command = new PostDepositCmd("ACC-123", BigDecimal.TEN, "USD");
    }

    // -- Action --

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            transaction.execute(command);
        } catch (DomainException e) {
            caughtException = e;
        }
    }

    // -- Outcomes --

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertFalse(transaction.getUncommittedEvents().isEmpty(), "Should have uncommitted events");
        Assertions.assertTrue(transaction.getUncommittedEvents().get(0) instanceof DepositPostedEvent, "Event should be DepositPostedEvent");

        DepositPostedEvent event = (DepositPostedEvent) transaction.getUncommittedEvents().get(0);
        Assertions.assertEquals(command.accountNumber, event.getAccountNumber());
        Assertions.assertEquals(command.amount, event.getAmount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected DomainException to be thrown");
        Assertions.assertTrue(transaction.getUncommittedEvents().isEmpty(), "No events should be emitted on failure");
    }
}