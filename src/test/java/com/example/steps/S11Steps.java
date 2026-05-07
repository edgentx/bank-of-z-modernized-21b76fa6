package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class S11Steps {

    private Transaction transaction;
    private PostWithdrawalCmd cmd;
    private WithdrawalPostedEvent lastEvent;
    private Exception domainException;

    // Helper to create a fresh, valid transaction
    private void createValidTransaction() {
        // Assuming an account has existing funds to allow withdrawal
        transaction = new Transaction();
        // Seed state to satisfy "valid account balance" check for the success case
        // We simulate a current balance via the aggregate's internal state mechanism
        transaction.setAccountNumber("ACC-001");
        transaction.setCurrentBalance(new BigDecimal("1000.00").setScale(2));
        transaction.setPosted(false);
    }

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        createValidTransaction();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        if (transaction == null) createValidTransaction();
        // The command will use this account number
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Amount is set in the When step or stored in context
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Currency is set in the When step or stored in context
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        createValidTransaction();
        // Violation: Amount <= 0
        cmd = new PostWithdrawalCmd("ACC-001", new BigDecimal("-10.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        createValidTransaction();
        transaction.markAsPosted(); // Make it immutable
        // Attempting to command a posted transaction
        cmd = new PostWithdrawalCmd("ACC-001", new BigDecimal("10.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        createValidTransaction();
        // Violation: Withdrawal amount exceeds current balance (assuming strict invariant)
        transaction.setCurrentBalance(new BigDecimal("5.00").setScale(2));
        cmd = new PostWithdrawalCmd("ACC-001", new BigDecimal("10.00"), "USD");
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        if (cmd == null) {
            // If not set in a specific Given, assume valid inputs for the happy path
            cmd = new PostWithdrawalCmd("ACC-001", new BigDecimal("50.00"), "USD");
        }
        try {
            lastEvent = transaction.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            domainException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(lastEvent, "Expected an event to be emitted");
        assertEquals("ACC-001", lastEvent.getAccountNumber());
        assertEquals(new BigDecimal("50.00").setScale(2), lastEvent.getAmount());
        assertEquals("USD", lastEvent.getCurrency());
        assertNotNull(lastEvent.getTransactionId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(domainException, "Expected a domain exception to be thrown");
        // Optionally verify the message content based on specific invariants
    }
}
