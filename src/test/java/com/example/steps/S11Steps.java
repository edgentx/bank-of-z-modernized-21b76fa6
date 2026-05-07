package com.example.steps;

import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S11Steps {

    private TransactionAggregate aggregate;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        this.aggregate = new TransactionAggregate("txn-123");
        this.aggregate.setAccountNumber("acct-456");
        this.aggregate.setBalance(new BigDecimal("1000.00"));
        this.aggregate.setPosted(false);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.accountNumber = "acct-456";
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("100.00");
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.aggregate = new TransactionAggregate("txn-invalid-amount");
        this.aggregate.setAccountNumber("acct-456");
        this.aggregate.setBalance(new BigDecimal("1000.00"));
        this.aggregate.setPosted(false);
        this.accountNumber = "acct-456";
        this.amount = BigDecimal.ZERO; // Invalid
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_transaction_aggregate_that_violates_cannot_be_altered() {
        this.aggregate = new TransactionAggregate("txn-already-posted");
        this.aggregate.setAccountNumber("acct-456");
        this.aggregate.setBalance(new BigDecimal("1000.00"));
        this.aggregate.setPosted(true); // Already posted
        this.accountNumber = "acct-456";
        this.amount = new BigDecimal("100.00");
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_transaction_aggregate_that_violates_must_result_in_valid_balance() {
        this.aggregate = new TransactionAggregate("txn-overdraft");
        this.aggregate.setAccountNumber("acct-456");
        this.aggregate.setBalance(new BigDecimal("50.00")); // Low balance
        this.aggregate.setPosted(false);
        this.accountNumber = "acct-456";
        this.amount = new BigDecimal("100.00"); // Withdrawal > Balance
        this.currency = "USD";
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        Command cmd = new PostWithdrawalCmd(aggregate.id(), accountNumber, amount, currency);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Throwable t) {
            thrownException = t;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "One event should be emitted");
        Assertions.assertTrue(resultEvents.get(0) instanceof WithdrawalPostedEvent, "Event should be WithdrawalPostedEvent");

        WithdrawalPostedEvent event = (WithdrawalPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("withdrawal.posted", event.type());
        Assertions.assertEquals(accountNumber, event.accountNumber());
        Assertions.assertEquals(amount, event.amount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Should have thrown an exception");
        // In a real app, we might catch specific DomainException types. Here we just check RuntimeException/IllegalStateException/IllegalArgumentException
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
