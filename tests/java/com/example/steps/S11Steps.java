package com.example.steps;

import com.example.domain.PostWithdrawalCmd;
import com.example.domain.Transaction;
import com.example.domain.WithdrawalPosted;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;

public class S11Steps {

    private Transaction transaction;
    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;
    private Exception caughtException;
    private Object resultingEvent;

    // Setup for Happy Path
    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        // Initialize with 1000.00 USD balance to allow valid withdrawals
        this.transaction = new Transaction();
        transaction.setCurrentBalance(new BigDecimal("1000.00"));
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        this.accountNumber = "ACC-123-456";
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        this.amount = new BigDecimal("50.00");
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        this.currency = Currency.getInstance("USD");
    }

    // Setup for Negative Amount Constraint
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsMustBeGreaterThanZero() {
        this.transaction = new Transaction();
        transaction.setCurrentBalance(new BigDecimal("1000.00"));
        // We will trigger the violation by passing a negative amount in the next step
        // Setting the state variable to negative to indicate this scenario path
        this.amount = new BigDecimal("-100.00"); 
        this.accountNumber = "ACC-123";
        this.currency = Currency.getInstance("USD");
    }

    // Setup for Immutability Constraint (Already Posted)
    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesImmutability() {
        this.transaction = new Transaction();
        // Force the aggregate into a 'posted' state
        transaction.markAsPosted();
        this.accountNumber = "ACC-123";
        this.amount = new BigDecimal("10.00");
        this.currency = Currency.getInstance("USD");
    }

    // Setup for Valid Balance Constraint (Insufficient Funds)
    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidAccountBalance() {
        this.transaction = new Transaction();
        // Set balance to 0, any withdrawal will cause invalid balance
        transaction.setCurrentBalance(BigDecimal.ZERO);
        this.accountNumber = "ACC-123";
        this.amount = new BigDecimal("10.00");
        this.currency = Currency.getInstance("USD");
    }

    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        try {
            PostWithdrawalCmd cmd = new PostWithdrawalCmd(accountNumber, amount, currency);
            this.resultingEvent = transaction.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.caughtException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvent);
        Assertions.assertTrue(resultingEvent instanceof WithdrawalPosted);
        WithdrawalPosted event = (WithdrawalPosted) resultingEvent;
        Assertions.assertEquals(accountNumber, event.getAccountNumber());
        Assertions.assertEquals(amount, event.getAmount());
        Assertions.assertEquals(currency, event.getCurrency());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Verify it's a RuntimeException (Domain Logic Violation)
        Assertions.assertTrue(caughtException instanceof RuntimeException);
    }
}
