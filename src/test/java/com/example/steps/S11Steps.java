package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class S11Steps {

    private static final String VALID_ACCOUNT = "ACC-001";
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("100.00");
    private static final Currency VALID_CURRENCY = Currency.getInstance("USD");
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000.00");

    private Account account;
    private Transaction transaction;
    private PostWithdrawalCmd command;
    private DomainError capturedError;
    private WithdrawalPostedEvent capturedEvent;

    // Setup / Given States
    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        this.account = new Account(VALID_ACCOUNT, INITIAL_BALANCE);
        this.transaction = new Transaction(account);
        this.capturedError = null;
        this.capturedEvent = null;
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateWithInvalidAmount() {
        this.account = new Account(VALID_ACCOUNT, INITIAL_BALANCE);
        this.transaction = new Transaction(account);
        this.capturedError = null;
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatIsAlreadyPosted() {
        this.account = new Account(VALID_ACCOUNT, INITIAL_BALANCE);
        this.transaction = new Transaction(account);
        
        // Simulate posting a transaction first
        PostWithdrawalCmd cmd = new PostWithdrawalCmd(VALID_ACCOUNT, VALID_AMOUNT, VALID_CURRENCY);
        this.transaction.execute(cmd); // Assumes valid for setup
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatCausesOverdraft() {
        // Account with 100 balance
        this.account = new Account(VALID_ACCOUNT, new BigDecimal("100.00")); 
        this.transaction = new Transaction(account);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Command will be constructed in 'When' using constants
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Command will be constructed in 'When'
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Command will be constructed in 'When'
    }

    // Actions
    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        // Determine context based on state
        BigDecimal amountToUse = VALID_AMOUNT;
        
        if (transaction.getPostedEvent() != null) {
            // Scenario: Already posted (trying to post again on same aggregate instance)
            // We keep amount valid to isolate the "already posted" error
            amountToUse = VALID_AMOUNT;
        } else if (account.getBalance().compareTo(new BigDecimal("200")) < 0) {
             // Scenario: Overdraft
             // Request 200 when account has 100 (plus logic in aggregate)
             amountToUse = new BigDecimal("200.00");
        }

        this.command = new PostWithdrawalCmd(VALID_ACCOUNT, amountToUse, VALID_CURRENCY);
        
        try {
            DomainEvent evt = this.transaction.execute(this.command);
            if (evt instanceof WithdrawalPostedEvent) {
                this.capturedEvent = (WithdrawalPostedEvent) evt;
            }
        } catch (DomainError e) {
            this.capturedError = e;
        }
    }

    // Outcomes
    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        Assertions.assertNotNull(capturedEvent, "Expected WithdrawalPostedEvent but was null");
        Assertions.assertEquals(command.getAccountNumber(), capturedEvent.accountNumber());
        Assertions.assertEquals(command.getAmount(), capturedEvent.amount());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedError, "Expected DomainError but command succeeded");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainErrorDuplicate() {
        // Cucumber allows duplicate Then steps, we just map to same validation
        Assertions.assertNotNull(capturedError, "Expected DomainError but command succeeded");
    }
}