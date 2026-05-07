package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import com.example.domain.transaction.command.ReverseTransactionCmd;
import com.example.domain.transaction.event.TransactionReversedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;
import java.util.HashMap;

public class S12Steps {

    // Simple in-memory repository for BDD testing
    static class InMemoryTransactionRepository implements TransactionRepository {
        private final HashMap<String, TransactionAggregate> store = new HashMap<>();

        @Override
        public TransactionAggregate load(String id) {
            return store.get(id);
        }

        @Override
        public void save(TransactionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }

        public void add(TransactionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }
    }

    private final InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    private TransactionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private final String transactionId = UUID.randomUUID().toString();
    private final String originalTransactionId = UUID.randomUUID().toString();

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Create a fresh aggregate in a valid state
        aggregate = new TransactionAggregate(transactionId);
        // Assume a constructor or helper that sets up a valid state for reversal
        // We assume the aggregate is in a state that allows reversal (e.g. posted)
        aggregate.markPosted(); // Helper to simulate state
        repository.add(aggregate);
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_originalTransactionId_is_provided() {
        // The ID is already generated in the field
        Assertions.assertNotNull(originalTransactionId);
    }

    // --- Scenario 2: Amounts > 0 ---
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts() {
        aggregate = new TransactionAggregate(transactionId);
        // Setup: The reversal logic might depend on the original amount.
        // We mock the state such that the amount check fails (e.g. zero amount on original)
        aggregate.setAmount(BigDecimal.ZERO); 
        repository.add(aggregate);
    }

    // --- Scenario 3: Altering/Deleting ---
    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        aggregate = new TransactionAggregate(transactionId);
        // If the aggregate is already in a state that implies it cannot be reversed
        // Or if the command attempts to modify the existing transaction directly instead of creating a mirror.
        // Given the Command "ReverseTransaction", the violation might be if we try to reverse an already reversed transaction
        aggregate.markReversed(); 
        repository.add(aggregate);
    }

    // --- Scenario 4: Valid Account Balance ---
    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance() {
        aggregate = new TransactionAggregate(transactionId);
        // Simulate a state where reversal would cause an overdraft or invalid state
        aggregate.markWouldCauseInvalidBalance();
        repository.add(aggregate);
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_ReverseTransactionCmd_command_is_executed() {
        try {
            Command cmd = new ReverseTransactionCmd(transactionId, originalTransactionId);
            // Reload to ensure clean state from repository if needed, though we hold reference
            aggregate = repository.load(transactionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no error, but got: " + caughtException.getMessage());
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception, but command succeeded.");
        // Verify it's a specific domain error or IllegalStateException/IllegalArgumentException
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
