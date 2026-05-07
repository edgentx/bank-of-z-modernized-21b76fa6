package com.example.steps;

import com.example.domain.PostDepositCmd;
import com.example.domain.S10Event;
import com.example.domain.Transaction;
import com.example.domain.ValidationError;
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
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.transaction = new Transaction(UUID.randomUUID());
        // Assume initial valid state setup for balance validation edge cases if needed
        // but for basic success, a fresh transaction is fine.
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd();
        }
        this.command.setAccountNumber("ACC-12345");
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd();
        }
        this.command.setAmount(new BigDecimal("100.00"));
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd();
        }
        this.command.setCurrency("USD");
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Execute the command on the aggregate
            transaction.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        // In a real aggregate, we'd check the uncommitted events list.
        // For this implementation, we check the internal state change matches the expectation.
        Assertions.assertTrue(transaction.isPosted(), "Transaction should be marked as posted");
        Assertions.assertEquals(command.getAccountNumber(), transaction.getAccountNumber());
        Assertions.assertEquals(command.getAmount(), transaction.getAmount());
        Assertions.assertEquals(command.getCurrency(), transaction.getCurrency());
    }

    // --- Negative Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.command = new PostDepositCmd();
        this.command.setAccountNumber("ACC-12345");
        this.command.setAmount(BigDecimal.ZERO); // Violation
        this.command.setCurrency("USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        this.transaction = new Transaction(UUID.randomUUID());
        // Manually set the transaction to posted state to simulate the invariant violation
        transaction.markAsPostedInternal(); 
        
        this.command = new PostDepositCmd();
        this.command.setAccountNumber("ACC-12345");
        this.command.setAmount(new BigDecimal("100.00"));
        this.command.setCurrency("USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        // Assuming a business rule where deposits cannot exceed 1,000,000 for this test
        this.transaction = new Transaction(UUID.randomUUID());
        this.command = new PostDepositCmd();
        this.command.setAccountNumber("ACC-12345");
        this.command.setAmount(new BigDecimal("999999999.00")); // Violates balance rule
        this.command.setCurrency("USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected a ValidationError to be thrown");
        Assertions.assertTrue(thrownException instanceof ValidationError, "Expected ValidationError");
    }
}