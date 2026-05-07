package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private TransactionAggregate aggregate;
    private String aggregateId = "txn-123";
    private String accountNumber = "acct-456";
    private BigDecimal amount;
    private String currency = "USD";
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate(aggregateId);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // accountNumber defaults to "acct-456"
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("100.00");
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // currency defaults to "USD"
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        aggregate = new TransactionAggregate(aggregateId);
        this.amount = new BigDecimal("-50.00");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_Transaction_aggregate_that_violates_once_posted() {
        // Create and immediately 'post' a transaction to put it in a non-editable state
        aggregate = new TransactionAggregate(aggregateId);
        Command initialCmd = new PostWithdrawalCmd(aggregateId, accountNumber, new BigDecimal("10.00"), "USD");
        aggregate.execute(initialCmd);
        aggregate.markPosted(); // Mutates state to simulate posted status
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        aggregate = new TransactionAggregate(aggregateId);
        // Using a massive amount that implies insufficient funds logic (enforced by aggregate validation)
        this.amount = new BigDecimal("999999999.00");
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        Command cmd = new PostWithdrawalCmd(aggregateId, accountNumber, amount, currency);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof WithdrawalPostedEvent);
        
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) resultEvents.get(0);
        assertEquals("withdrawal.posted", event.type());
        assertEquals(aggregateId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Verify it's a domain exception (IllegalArgumentException or IllegalStateException)
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
