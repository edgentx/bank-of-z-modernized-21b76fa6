package com.example.steps;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private TransactionAggregate aggregate;
    private PostWithdrawalCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to create a valid aggregate default
    private TransactionAggregate createValidAggregate() {
        return new TransactionAggregate("txn-123");
    }

    // Scenario: Successfully execute PostWithdrawalCmd
    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        aggregate = createValidAggregate();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account is usually resolved or passed. Here we construct the command state.
        // Placeholder if we were injecting complex state, but for command, we handle in When.
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled in When
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled in When
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        try {
            // Defaulting to valid values for the happy path
            if (command == null) {
                command = new PostWithdrawalCmd("acct-456", new BigDecimal("100.00"), "USD");
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof WithdrawalPostedEvent);
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) resultEvents.get(0);
        assertEquals("withdrawal.posted", event.type());
        assertEquals("acct-456", event.accountNumber());
        assertEquals(0, new BigDecimal("100.00").compareTo(event.amount()));
        assertEquals("USD", event.currency());
    }

    // Scenario: Transaction amounts must be greater than zero
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount_gt_zero() {
        aggregate = createValidAggregate();
        command = new PostWithdrawalCmd("acct-456", new BigDecimal("-50.00"), "USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    // Scenario: Transactions cannot be altered or deleted once posted
    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_immutability() {
        aggregate = createValidAggregate();
        // We execute a valid command first to post it
        PostWithdrawalCmd initialCmd = new PostWithdrawalCmd("acct-456", new BigDecimal("100.00"), "USD");
        aggregate.execute(initialCmd);
        aggregate.markPosted(); // Simulate the aggregate being posted/finalized
        
        // Now try to execute another command on the same aggregate ID (or modify it)
        command = new PostWithdrawalCmd("acct-456", new BigDecimal("200.00"), "USD");
    }

    // Scenario: A transaction must result in a valid account balance
    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_valid_balance() {
        aggregate = createValidAggregate();
        // Assume account has 50 balance (simulated in aggregate for test)
        aggregate.setAvailableBalance(new BigDecimal("50.00"));
        
        // Try to withdraw 100
        command = new PostWithdrawalCmd("acct-456", new BigDecimal("100.00"), "USD");
    }

}
