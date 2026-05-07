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
    private DepositPostedEvent lastEvent;
    private Exception domainException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        transaction = new Transaction(UUID.randomUUID());
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled in context setup or combined below, usually part of command construction
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Context setup
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Context setup
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        // Default valid command for positive path
        if (command == null) {
            command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
        }
        try {
            transaction.execute(command);
            if (!transaction.getUncommittedEvents().isEmpty()) {
                lastEvent = (DepositPostedEvent) transaction.getUncommittedEvents().get(0);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            domainException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(lastEvent, "Event should not be null");
        Assertions.assertEquals("ACC-123", lastEvent.accountNumber());
        Assertions.assertEquals(0, new BigDecimal("100.00").compareTo(lastEvent.amount()));
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount() {
        transaction = new Transaction(UUID.randomUUID());
        command = new PostDepositCmd("ACC-123", new BigDecimal("-50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        transaction = new Transaction(UUID.randomUUID());
        // Simulate already posted by applying a prior event manually for test
        transaction.markPosted(); 
        command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance() {
        transaction = new Transaction(UUID.randomUUID());
        // Use a special flag or setup in the aggregate to force this failure for the test
        // In a real app, this might depend on external state, here we simulate via input validation or aggregate state
        command = new PostDepositCmd("INVALID-BALANCE", new BigDecimal("100.00"), "USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(domainException, "Expected an exception to be thrown");
    }
}
