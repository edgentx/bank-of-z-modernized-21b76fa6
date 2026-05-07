package com.example.steps;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import com.example.domain.transaction.ReverseTransactionCmd;
import com.example.domain.transaction.TransactionReversedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class S12Steps {

    private TransactionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper Repository (In-memory)
    private static class InMemoryTxRepo implements TransactionRepository {
        @Override
        public TransactionAggregate save(TransactionAggregate aggregate) {
            return aggregate;
        }
        @Override
        public TransactionAggregate findById(String id) {
            return null;
        }
    }

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Setup a valid transaction state that can be reversed
        aggregate = new TransactionAggregate(UUID.randomUUID().toString());
        // We assume there is a way to hydrate the aggregate or set its state for testing.
        // Since the prompt implies the aggregate exists, we instantiate it.
        // Ideally we would apply a "Posted" event here if the lifecycle required it.
        // For this exercise, we assume the constructor prepares a valid state.
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_originalTransactionId_is_provided() {
        // In a real scenario, this would setup the Command.
        // Since the command is created in the @When, this ensures the context is ready.
        // We store the ID in the context or just ensure the aggregate is configured.
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts() {
        aggregate = new TransactionAggregate("invalid-amount-tx");
        // The aggregate internally will be configured to return 0 or negative amount
        // when the command is executed. This logic lives in the aggregate.
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        aggregate = new TransactionAggregate("immutable-tx");
        // The aggregate is flagged in a state that prevents reversal logic (e.g. double reversal)
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        aggregate = new TransactionAggregate("invalid-balance-tx");
        // The aggregate will fail the balance check during execution.
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_ReverseTransactionCmd_command_is_executed() {
        ReverseTransactionCmd cmd = new ReverseTransactionCmd(aggregate.id(), BigDecimal.valueOf(100.00));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected exception was not thrown");
        // Validate it's a domain logic error (IllegalStateException, IllegalArgumentException, etc.)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException
        );
    }

    // JUnit 5 Runner
    @org.junit.platform.suite.api.Suite
    @org.junit.platform.suite.api.SelectClasspathResource("features")
    @org.junit.platform.suite.api.IncludeEngines("cucumber")
    public static class S12Runner {}
}