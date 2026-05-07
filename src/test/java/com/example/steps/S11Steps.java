package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class S11Steps {

    private Transaction transaction;
    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;
    private BigDecimal initialBalance;
    
    private S11Event resultEvent;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        UUID transactionId = UUID.randomUUID();
        String accountNumber = "ACC-123456";
        // Setup: Account has sufficient funds for positive flow
        this.initialBalance = new BigDecimal("1000.00");
        this.accountNumber = accountNumber;
        this.amount = new BigDecimal("100.00");
        this.currency = Currency.getInstance("USD");
        
        this.transaction = new Transaction(transactionId, accountNumber, initialBalance, TransactionStatus.PENDING);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Provided in setup, ensures context variable is set
        Assertions.assertNotNull(this.accountNumber);
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Provided in setup
        Assertions.assertNotNull(this.amount);
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Provided in setup
        Assertions.assertNotNull(this.currency);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        UUID transactionId = UUID.randomUUID();
        String accountNumber = "ACC-ERROR-AMT";
        this.initialBalance = new BigDecimal("1000.00");
        this.accountNumber = accountNumber;
        this.amount = new BigDecimal("-50.00"); // Negative amount
        this.currency = Currency.getInstance("USD");
        
        this.transaction = new Transaction(transactionId, accountNumber, initialBalance, TransactionStatus.PENDING);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        UUID transactionId = UUID.randomUUID();
        String accountNumber = "ACC-ALREADY-POSTED";
        this.initialBalance = new BigDecimal("1000.00");
        this.accountNumber = accountNumber;
        this.amount = new BigDecimal("10.00");
        this.currency = Currency.getInstance("USD");
        
        // Create transaction that is already POSTED
        this.transaction = new Transaction(transactionId, accountNumber, initialBalance, TransactionStatus.POSTED);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        UUID transactionId = UUID.randomUUID();
        String accountNumber = "ACC-NO-FUNDS";
        this.initialBalance = new BigDecimal("50.00"); // Low balance
        this.accountNumber = accountNumber;
        this.amount = new BigDecimal("100.00"); // Overdraft attempt
        this.currency = Currency.getInstance("USD");
        
        this.transaction = new Transaction(transactionId, accountNumber, initialBalance, TransactionStatus.PENDING);
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        S11Command cmd = new S11Command(transaction.getId(), accountNumber, amount, currency);
        try {
            resultEvent = (S11Event) transaction.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNotNull(resultEvent, "Event should not be null");
        Assertions.assertEquals("withdrawal.posted", resultEvent.getType());
        Assertions.assertEquals(accountNumber, resultEvent.getAccountNumber());
        Assertions.assertEquals(amount, resultEvent.getAmount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Exception should have been thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || 
                              thrownException instanceof IllegalStateException);
    }
}
