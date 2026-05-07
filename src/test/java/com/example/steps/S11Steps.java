package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S11Steps {

    private Transaction aggregate;
    private S11Command command;
    private Exception caughtException;
    private S11Event resultingEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        // Create a fresh transaction that can accept a withdrawal
        aggregate = new Transaction(UUID.randomUUID());
        // Assume the aggregate starts with a balance that allows withdrawals for the "Success" case
        // Balance: 1000.00 USD
        // In a real scenario, this might be built via events, but for unit testing the logic:
        aggregate.loadStateForTest(new BigDecimal("1000.00"), "USD");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Placeholder: The command setup in 'When' will use the valid number
        // We track state here if specific numbers were needed, but defaults are fine for this feature.
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Placeholder: The command setup in 'When' will use the valid amount
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Placeholder: The command setup in 'When' will use the valid currency
    }

    // --- Negative Scenarios Given Steps ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount_must_be_gt_zero() {
        aggregate = new Transaction(UUID.randomUUID());
        aggregate.loadStateForTest(new BigDecimal("1000.00"), "USD");
        
        command = new S11Command.PostWithdrawalCmd(
            UUID.randomUUID(), 
            new BigDecimal("-50.00"), // Invalid amount
            "USD"
        );
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_altered_once_posted() {
        aggregate = new Transaction(UUID.randomUUID());
        aggregate.loadStateForTest(new BigDecimal("1000.00"), "USD");
        
        // Simulate that the transaction is already posted/committed
        aggregate.markAsPosted();
        
        // Now try to apply another command to it
        command = new S11Command.PostWithdrawalCmd(
            UUID.randomUUID(), 
            new BigDecimal("10.00"), 
            "USD"
        );
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_valid_account_balance() {
        // Aggregate has 100 USD, we try to withdraw 200 USD
        aggregate = new Transaction(UUID.randomUUID());
        aggregate.loadStateForTest(new BigDecimal("100.00"), "USD");
        
        command = new S11Command.PostWithdrawalCmd(
            UUID.randomUUID(), 
            new BigDecimal("200.00"), // Overdraft
            "USD"
        );
    }

    // --- Actions ---

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        // If the specific scenario hasn't set a command yet (success case), set it up now.
        if (command == null) {
            command = new S11Command.PostWithdrawalCmd(
                UUID.randomUUID(), 
                new BigDecimal("100.00"), 
                "USD"
            );
        }

        try {
            resultingEvent = aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNotNull(resultingEvent, "Expected a valid event, but got null");
        Assertions.assertTrue(resultingEvent instanceof S11Event.WithdrawalPosted);
        S11Event.WithdrawalPosted event = (S11Event.WithdrawalPosted) resultingEvent;
        
        // Basic validation of event content
        Assertions.assertEquals(new BigDecimal("100.00"), event.amount());
        Assertions.assertEquals("USD", event.currency());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception to be thrown, but none was");
        // We check for RuntimeException or specific domain exceptions. 
        // Since we threw IllegalArgumentException/IllegalStateException in the domain, this holds.
        Assertions.assertTrue(caughtException instanceof RuntimeException);
    }
}
