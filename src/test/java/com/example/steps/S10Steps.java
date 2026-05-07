package com.example.steps;

import com.example.domain.PostDepositCmd;
import com.example.domain.Transaction;
import com.example.domain.DepositPosted;
import com.example.domain.TransactionError;
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
    private DepositPosted event;
    private TransactionError error;

    // Scenario: Successfully execute PostDepositCmd
    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        transaction = new Transaction(UUID.randomUUID());
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        if (command == null) command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (command == null) command = new PostDepositCmd();
        command.setAmount(new BigDecimal("100.00"));
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (command == null) command = new PostDepositCmd();
        command.setCurrency("USD");
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        try {
            event = transaction.execute(command);
        } catch (TransactionError e) {
            this.error = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(event, "Expected DepositPosted event, but got null or error");
        Assertions.assertNull(error, "Expected no error, but got: " + error.getMessage());
        Assertions.assertEquals("ACC-12345", event.getAccountNumber());
        Assertions.assertEquals(0, new BigDecimal("100.00").compareTo(event.getAmount()));
        Assertions.assertEquals("USD", event.getCurrency());
    }

    // Scenario: PostDepositCmd rejected — Transaction amounts must be greater than zero.
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount_must_be_greater_than_zero() {
        transaction = new Transaction(UUID.randomUUID());
        command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
        command.setAmount(BigDecimal.ZERO); // Violation
        command.setCurrency("USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(error, "Expected domain error, but command succeeded");
        Assertions.assertNull(event, "Expected no event, but got: " + event);
    }

    // Scenario: PostDepositCmd rejected — Transactions cannot be altered or deleted once posted
    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_locked_state() {
        transaction = new Transaction(UUID.randomUUID());
        // Force transaction into a posted state (simulated)
        transaction.markAsPosted(); 
        command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
        command.setAmount(new BigDecimal("50.00"));
        command.setCurrency("USD");
    }

    // Scenario: PostDepositCmd rejected — A transaction must result in a valid account balance
    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_valid_balance() {
        transaction = new Transaction(UUID.randomUUID());
        // Simulate an account balance constraint violation (e.g., Max Balance)
        transaction.setMaximumBalance(new BigDecimal("1000.00"));
        
        command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
        command.setAmount(new BigDecimal("999999.00")); // Exceeds max
        command.setCurrency("USD");
    }
}
