package com.example.steps;

import com.example.domain.PostDepositCmd;
import com.example.domain.Transaction;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private Transaction aggregate;
    private PostDepositCmd cmd;
    private List<Object> resultingEvents;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        // Valid aggregate starting state: ID matches, balance is 0 (or 100)
        UUID txnId = UUID.randomUUID();
        this.aggregate = new Transaction(txnId, BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in context of command construction below, but we can store it if needed.
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled in command construction
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled in command construction
    }

    // --- Scenario 1: Success ---

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        if (cmd == null) {
            // Default command construction for the happy path if pre-conditions didn't set it
            cmd = new PostDepositCmd(aggregate.getId(), "ACC-123", new BigDecimal("100.00"), "USD");
        }
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultingEvents.get(0) instanceof com.example.domain.DepositPosted,
                "Event should be instance of DepositPosted");
    }

    // --- Scenario 2: Amount <= 0 ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount_gt_zero() {
        // Setup valid aggregate
        UUID txnId = UUID.randomUUID();
        this.aggregate = new Transaction(txnId, BigDecimal.ZERO);
        // The violation is in the command, but the Gherkin says "Given a Transaction aggregate that violates".
        // Interpreting this as: Prepare a command with invalid amount for this aggregate.
        this.cmd = new PostDepositCmd(txnId, "ACC-123", BigDecimal.ZERO, "USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof Transaction.DomainException, "Expected DomainException");
        assertTrue(caughtException.getMessage().contains("greater than zero"));
    }

    // --- Scenario 3: Already Posted ---

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_alteration() {
        UUID txnId = UUID.randomUUID();
        this.aggregate = new Transaction(txnId, BigDecimal.ZERO);
        // Force the aggregate into a POSTED state
        aggregate.markAsPosted();
        // Create a valid command for the ID, but it should be rejected
        this.cmd = new PostDepositCmd(txnId, "ACC-123", new BigDecimal("50.00"), "USD");
    }

    // Re-use When/Then from above (they are generic)

    // --- Scenario 4: Invalid Balance ---

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance_validation() {
        UUID txnId = UUID.randomUUID();
        // Start with a negative balance that a deposit would normally fix, 
        // but let's assume the validation logic rejects any transaction resulting in < 0.
        // Or simpler: Start with 0, and try to post a NEGATIVE deposit (which is covered by scenario 2).
        // Let's do: Start with -100. Try to add -200 -> -300 (Fail).
        // Or: Start with -100. Deposit allowed? Only if >= 0.
        // Let's try: Balance is -50. Deposit is -20 (Allowed by scenario 2 check? No).
        // Let's try: Balance is -100. Deposit is +10. Result -90. Validation rejects if < 0.
        this.aggregate = new Transaction(txnId, new BigDecimal("-100.00"));
        this.cmd = new PostDepositCmd(txnId, "ACC-123", new BigDecimal("10.00"), "USD");
    }

}
