package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private DomainException domainException;
    private DepositPostedEvent resultEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        // Create a standard valid transaction
        transaction = new Transaction(UUID.randomUUID(), "ACC-123", new Money(BigDecimal.ZERO, "USD"));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd("ACC-123", new Money(BigDecimal.TEN, "USD"));
        }
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd("ACC-123", new Money(BigDecimal.TEN, "USD"));
        }
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd("ACC-123", new Money(BigDecimal.TEN, "USD"));
        }
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_with_invalid_amount() {
        transaction = new Transaction(UUID.randomUUID(), "ACC-123", new Money(BigDecimal.ZERO, "USD"));
        command = new PostDepositCmd("ACC-123", new Money(BigDecimal.ZERO, "USD")); // Or negative
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_is_already_posted() {
        transaction = new Transaction(UUID.randomUUID(), "ACC-123", new Money(BigDecimal.ZERO, "USD"));
        // Mark as posted
        transaction.markAsPosted(); 
        command = new PostDepositCmd("ACC-123", new Money(BigDecimal.TEN, "USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_with_invalid_balance_logic() {
        // Setup a scenario where adding funds would break an invariant
        // For example, an aggregate with a strict cap
        transaction = new Transaction(UUID.randomUUID(), "ACC-CAPPED", new Money(BigDecimal.valueOf(9999), "USD"));
        command = new PostDepositCmd("ACC-CAPPED", new Money(BigDecimal.TEN, "USD"));
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        try {
            // Assuming a generic execute method or specific method call
            resultEvent = transaction.execute(command);
        } catch (DomainException e) {
            domainException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(resultEvent);
        Assertions.assertNotNull(resultEvent.getTransactionId());
        Assertions.assertNotNull(resultEvent.getAccountNumber());
        Assertions.assertNotNull(resultEvent.getAmount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(domainException);
        Assertions.assertTrue(domainException.getMessage().length() > 0);
    }
}
