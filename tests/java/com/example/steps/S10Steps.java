package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private DepositPostedEvent resultEvent;
    private DomainError capturedError;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        transaction = new Transaction("txn-123", "acc-456", BigDecimal.ZERO, Currency.getInstance("USD"));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account is set in aggregate constructor, ensuring it exists for the command context
        assertNotNull(transaction);
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Context setup for command
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Context setup for command
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        // Execute the happy path command
        command = new PostDepositCmd(transaction.getId(), "acc-456", new BigDecimal("100.00"), Currency.getInstance("USD"));
        try {
            resultEvent = transaction.execute(command);
        } catch (DomainError e) {
            capturedError = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(resultEvent);
        assertEquals("acc-456", resultEvent.accountNumber());
        assertEquals(new BigDecimal("100.00"), resultEvent.amount());
        assertEquals("USD", resultEvent.currency().getCurrencyCode());
        assertEquals("txn-123", resultEvent.transactionId());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        transaction = new Transaction("txn-bad-amount", "acc-456", BigDecimal.ZERO, Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        // Create a transaction that is already marked as posted
        transaction = new Transaction("txn-already-posted", "acc-456", BigDecimal.ZERO, Currency.getInstance("USD"));
        // Manually set internal state to simulate posted status for this test scenario
        // In a real scenario, this might happen via a previous event application not shown here
        try {
            transaction.markAsPosted(); // Helper method to set state for testing invariant
        } catch (Exception e) {
            // If helper doesn't exist, we assume the Transaction constructor creates a fresh one,
            // but for this step, we need it in a 'posted' state.
            // We will implement a mechanism in Transaction to support this test setup.
        }
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_account_balance_validation() {
        // Simulate an account that would overflow or go invalid (e.g. max limit)
        transaction = new Transaction("txn-balance-limit", "acc-456", new BigDecimal("999999999"), Currency.getInstance("USD"));
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed_failure_scenarios() {
        // Re-using the execution logic, the specific 'Given' setup determines the outcome
        command = new PostDepositCmd(transaction.getId(), "acc-456", new BigDecimal("100.00"), Currency.getInstance("USD"));
        try {
            transaction.execute(command);
        } catch (DomainError e) {
            capturedError = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedError, "Expected a DomainError to be thrown");
        assertTrue(capturedError.getMessage().length() > 0, "Error message should not be empty");
    }
}
