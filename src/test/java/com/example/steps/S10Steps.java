package com.example.steps;

import com.example.domain.PostDepositCmd;
import com.example.domain.Transaction;
import com.example.domain.DepostedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private Exception caughtException;
    private Object resultEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.transaction = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Default valid setup, will be overridden in specific negative scenarios if needed
        if (this.command == null) {
            this.command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
        }
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Default valid setup
        if (this.command != null) {
            this.command = new PostDepositCmd(this.command.accountNumber(), new BigDecimal("100.00"), this.command.currency());
        }
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Default valid setup
        if (this.command != null) {
            this.command = new PostDepositCmd(this.command.accountNumber(), this.command.amount(), "USD");
        }
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("-50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        UUID id = UUID.randomUUID();
        this.transaction = new Transaction(id);
        // Simulate existing posted state by manually setting internal status flag (via package-private or reflection for test)
        // Assuming a method or constructor to simulate state for test purposes
        this.transaction.markAsPosted(); 
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_must_result_in_valid_balance() {
        this.transaction = new Transaction(UUID.randomUUID());
        // We can't easily check "valid balance" without an Account aggregate, 
        // so we assume the Transaction validates against an injected limit or external state.
        // For this test, we will assume the aggregate throws an error if amount exceeds MAX_ALLOWED.
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("9999999999.00"), "USD");
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            this.resultEvent = transaction.execute(this.command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(resultEvent);
        Assertions.assertTrue(resultEvent instanceof DepostedEvent);
        DepostedEvent event = (DepostedEvent) resultEvent;
        Assertions.assertEquals("ACC-123", event.accountNumber());
        Assertions.assertEquals(0, event.amount().compareTo(new BigDecimal("100.00")));
        Assertions.assertEquals("USD", event.currency());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || 
                              caughtException instanceof IllegalStateException);
    }
}