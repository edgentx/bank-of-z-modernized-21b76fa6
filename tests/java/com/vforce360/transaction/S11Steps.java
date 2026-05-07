package com.vforce360.transaction;

import com.vforce360.transaction.domain.model.Transaction;
import com.vforce360.transaction.domain.model.TransactionStatus;
import com.vforce360.transaction.domain.model.AccountBalanceLimit;
import com.vforce360.transaction.application.command.PostWithdrawalCmd;
import com.vforce360.transaction.domain.event.WithdrawalPostedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;

public class S11Steps {

    private Transaction transaction;
    private PostWithdrawalCmd command;
    private Exception thrownException;
    private Object resultingEvent;

    // Helper to create a standard valid transaction
    private Transaction createValidTransaction() {
        return new Transaction("acc-123", new BigDecimal("100.00"), Currency.getInstance("USD"));
    }

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        transaction = createValidTransaction();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in command creation below
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled in command creation below
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled in command creation below
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        try {
            // Setup command with defaults unless modified in 'Given' violations
            if (command == null) {
                command = new PostWithdrawalCmd("acc-123", new BigDecimal("50.00"), Currency.getInstance("USD"));
            }
            resultingEvent = transaction.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultingEvent, "Should have emitted an event");
        Assertions.assertTrue(resultingEvent instanceof WithdrawalPostedEvent, "Event should be WithdrawalPostedEvent");
    }

    // ---- Scenarios for Rejections ----

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        transaction = createValidTransaction();
        command = new PostWithdrawalCmd("acc-123", new BigDecimal("-10.00"), Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        transaction = createValidTransaction();
        // Programmatically put the transaction in a POSTED state to simulate the invariant violation condition
        // (Reflection or package-private access would be used in real setup, here we assume the domain allows construction of a Posted transaction for testing purposes)
        // For TDD, we will assume we can create a transaction that is already posted.
        transaction.markPosted(); 
        command = new PostWithdrawalCmd("acc-123", new BigDecimal("10.00"), Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_transaction_aggregate_that_violates_valid_account_balance() {
        // Current balance is 100.00. If we try to withdraw 200.00, balance becomes -100.00.
        // Assuming the invariant is balance >= 0.
        transaction = createValidTransaction(); 
        command = new PostWithdrawalCmd("acc-123", new BigDecimal("200.00"), Currency.getInstance("USD"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Exception should have been thrown");
        // In a real app we might check for a custom DomainException type
    }
}
