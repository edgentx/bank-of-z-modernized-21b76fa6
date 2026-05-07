package com.example.steps;

import com.example.domain.PostWithdrawalCmd;
import com.example.domain.Transaction;
import com.example.domain.WithdrawalPostedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S11Steps {

    private Transaction transaction;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private Exception thrownException;
    private WithdrawalPostedEvent resultEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.transaction = new Transaction(UUID.randomUUID());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_positive() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.amount = new BigDecimal("-100"); // Invalid amount
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_is_already_posted() {
        UUID transactionId = UUID.randomUUID();
        this.transaction = new Transaction(transactionId);
        
        // Mark as posted internally (simulating a previously posted state)
        this.transaction.markAsPosted(); 
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_constraint() {
        this.transaction = new Transaction(UUID.randomUUID());
        // Simulate a constraint violation setup. 
        // For this scenario, we assume the aggregate needs context about the account balance.
        // We will pass a specific flag or context to the command to trigger this failure.
        this.transaction.setSimulateInsufficientFunds(true);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        this.accountNumber = "ACC-123-456";
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (this.amount == null) {
            this.amount = new BigDecimal("100.00");
        }
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = "USD";
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        // Construct command. If amount wasn't set by step (happy path default), check null.
        if (this.amount == null) {
            this.amount = new BigDecimal("100.00");
        }
        if (this.currency == null) {
            this.currency = "USD";
        }
        if (this.accountNumber == null) {
            this.accountNumber = "ACC-000";
        }

        PostWithdrawalCmd cmd = new PostWithdrawalCmd(this.accountNumber, this.amount, this.currency);
        
        try {
            this.resultEvent = this.transaction.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNotNull(this.resultEvent, "Expected event to be emitted");
        Assertions.assertEquals(this.amount, this.resultEvent.getAmount());
        Assertions.assertEquals(this.currency, this.resultEvent.getCurrency());
        Assertions.assertEquals(this.accountNumber, this.resultEvent.getAccountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(this.thrownException, "Expected a domain exception to be thrown");
        // Ideally catch specific custom exception, e.g., DomainException
        Assertions.assertTrue(this.thrownException instanceof IllegalStateException || 
                              this.thrownException instanceof IllegalArgumentException,
                              "Exception should be a domain violation");
    }
}
