package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction transaction;
    private TransactionRepository repository;
    private PostDepositCmd cmd;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Setup a valid transaction context
        UUID transactionId = UUID.randomUUID();
        // Assuming valid setup doesn't involve any violated invariants immediately
        transaction = new Transaction(transactionId, TransactionState.PENDING);
        repository = new InMemoryTransactionRepository();
        repository.save(transaction);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account number will be set in the command
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Amount will be set in the command
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Currency will be set in the command
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // If command wasn't initialized specifically for negative tests in previous steps, use defaults
            if (cmd == null) {
                cmd = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
            }
            transaction.execute(cmd);
        } catch (DomainException e) {
            this.thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(transaction.getUncommittedEvents());
        Assertions.assertFalse(transaction.getUncommittedEvents().isEmpty());
        Assertions.assertTrue(transaction.getUncommittedEvents().get(0) instanceof DepositPostedEvent);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_must_be_greater_than_zero() {
        UUID transactionId = UUID.randomUUID();
        transaction = new Transaction(transactionId, TransactionState.PENDING);
        cmd = new PostDepositCmd("ACC-123", new BigDecimal("-50.00"), "USD"); // Invalid amount
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_no_alteration_once_posted() {
        UUID transactionId = UUID.randomUUID();
        transaction = new Transaction(transactionId, TransactionState.POSTED); // Already posted
        cmd = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_balance() {
        UUID transactionId = UUID.randomUUID();
        // We can mock the repository behavior inside the aggregate for validation logic
        transaction = new Transaction(transactionId, TransactionState.PENDING) {
            @Override
            protected void validateBalance(PostDepositCmd cmd) {
                // Simulate a balance check failure
                throw new DomainException("Transaction would result in invalid account balance.");
            }
        };
        cmd = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof DomainException);
    }

    // Inner classes for Test Support (In-Memory implementations)

    public static class InMemoryTransactionRepository implements TransactionRepository {
        // Simple in-memory map logic if needed for retrieval, though we hold direct ref in steps
        @Override
        public void save(Transaction transaction) {
            // No-op for in-memory step definition usage
        }
    }
}
