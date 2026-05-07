package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction aggregate;
    private DomainException capturedError;
    private S10Event publishedEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.aggregate = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Context setup placeholder, data is passed in the command in the 'When' step
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Context setup placeholder
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Context setup placeholder
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        // Valid data as per the happy path scenario
        executeCommand("ACC-001", new BigDecimal("100.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.aggregate = new Transaction(UUID.randomUUID());
    }

    @When("the PostDepositCmd command is executed for zero amount")
    public void the_command_is_executed_for_zero_amount() {
        executeCommand("ACC-001", BigDecimal.ZERO, "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_is_already_posted() {
        this.aggregate = new Transaction(UUID.randomUUID());
        // Force the aggregate into a posted state
        aggregate.markAsPosted();
    }

    @When("the PostDepositCmd command is executed on posted aggregate")
    public void the_command_is_executed_on_posted_aggregate() {
        executeCommand("ACC-001", new BigDecimal("50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        this.aggregate = new Transaction(UUID.randomUUID());
        // Flag the repository or aggregate context to simulate a balance validation failure
        this.aggregate.simulateInvalidBalanceState(true);
    }

    @When("the PostDepositCmd command is executed with invalid balance implications")
    public void the_command_is_executed_with_invalid_balance_implications() {
        executeCommand("ACC-999", new BigDecimal("999999.00"), "USD");
    }

    private void executeCommand(String account, BigDecimal amount, String currency) {
        try {
            S10Command cmd = new S10Command(account, amount, currency);
            this.publishedEvent = this.aggregate.execute(cmd);
        } catch (DomainException e) {
            this.capturedError = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(publishedEvent, "Event should not be null");
        Assertions.assertTrue(publishedEvent instanceof DepositPostedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedError, "Expected a DomainException to be thrown");
        Assertions.assertFalse(capturedError.getMessage().isEmpty());
    }
}
