package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute PostWithdrawalCmd
    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        aggregate = new TransactionAggregate("TXN-123", "ACC-456");
        aggregate.setAvailableBalance(new BigDecimal("1000.00"));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number set in aggregate construction
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Amount will be provided in the command execution step
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Currency will be provided in the command execution step
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        var cmd = new PostWithdrawalCmd("ACC-456", new BigDecimal("50.00"), "USD");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(WithdrawalPostedEvent.class, resultEvents.get(0).getClass());
    }

    // Scenario: PostWithdrawalCmd rejected — Transaction amounts must be greater than zero.
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount_positive() {
        aggregate = new TransactionAggregate("TXN-INVALID-AMT", "ACC-789");
        aggregate.setAvailableBalance(new BigDecimal("100.00"));
    }

    @When("the PostWithdrawalCmd command is executed for amount zero")
    public void the_post_withdrawal_cmd_command_is_executed_for_zero_amount() {
        var cmd = new PostWithdrawalCmd("ACC-789", BigDecimal.ZERO, "USD");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    // Scenario: PostWithdrawalCmd rejected — Transactions cannot be altered or deleted once posted.
    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted.")
    public void a_transaction_aggregate_that_violates_already_posted() {
        aggregate = new TransactionAggregate("TXN-ALREADY-POSTED", "ACC-101");
        aggregate.setPosted(true);
    }

    // Scenario: PostWithdrawalCmd rejected — A transaction must result in a valid account balance.
    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance.")
    public void a_transaction_aggregate_that_violates_insufficient_funds() {
        aggregate = new TransactionAggregate("TXN-NSF", "ACC-202");
        aggregate.setAvailableBalance(new BigDecimal("10.00"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException ||
            caughtException instanceof UnknownCommandException,
            "Expected domain error (IllegalArgument/IllegalState/UnknownCommand), got: " + caughtException.getClass().getSimpleName()
        );
    }
}
