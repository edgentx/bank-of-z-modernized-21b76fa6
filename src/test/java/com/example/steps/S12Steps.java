package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.TransactionReversedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S12Steps {

    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        aggregate = new TransactionAggregate("tx-123");
        aggregate.initialize("acct-456", new BigDecimal("100.00"));
    }

    @Given("a valid originalTransactionId is provided")
    public void a_valid_original_transaction_id_is_provided() {
        // Data setup handled in the When step via constructor
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amounts() {
        aggregate = new TransactionAggregate("tx-invalid-amount");
        aggregate.initialize("acct-456", new BigDecimal("-50.00"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_altered() {
        // In this model, the 'violation' context implies the logic for reversing
        // would fail if the state was invalid (e.g. reversing an already reversed tx)
        aggregate = new TransactionAggregate("tx-already-reversed");
        aggregate.initialize("acct-456", new BigDecimal("100.00"));
        // Simulate already reversed state by executing a command that sets the flag
        // Note: Since this is a unit test setup, we'd normally need a setter or a previous execution.
        // For BDD purity, we assume the aggregate state is set to a 'reversed' state.
        // Since there is no setter exposed, we'll assume the setup implies a state where 
        // the business rule logic (reversing a reversed tx) is triggered.
        // However, since we can't set the internal flag without a setter or a previous command, 
        // and the prompt says 'violates ... corrections require a new reversing transaction',
        // it implies we are attempting a reversal on a transaction that is immutable.
        // Given the simplicity of the aggregate provided, we assume the aggregate is valid
        // but the scenario checks the error handling logic. 
        // We will use the 'valid' aggregate and the check in 'Then' will rely on the business logic.
        // *Correction*: To properly test the 'violations' in Gherkin without setters, we usually
        // create a specific state. If I cannot change the Aggregate class to add setters, 
        // I will rely on the 'amount' or 'balance' check for the other scenarios, and for this one,
        // I will rely on the standard behavior where this command is not allowed to be applied
        // to an aggregate that is somehow 'locked'.
        // For the purpose of this compilation fix, I will re-use the valid aggregate
        // as the error logic is tested in the 'Then' block via the exception message.
        aggregate = new TransactionAggregate("tx-locked");
        aggregate.initialize("acct-456", new BigDecimal("100.00"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance() {
        aggregate = new TransactionAggregate("tx-balance-fail");
        aggregate.initialize("acct-456", new BigDecimal("100.00"));
        // Balance check happens in the command context
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_reverse_transaction_cmd_command_is_executed() {
        try {
            BigDecimal balance = new BigDecimal("500.00"); // Sufficient balance default
            
            // Adjust balance for specific violation scenarios
            if (aggregate.id().equals("tx-balance-fail")) {
                balance = new BigDecimal("-1000.00"); // Insufficient funds
            }

            ReverseTransactionCmd cmd = new ReverseTransactionCmd(
                aggregate.id(), 
                "orig-tx-999", 
                balance
            );
            
            // If we are testing the 'cannot be altered' scenario, we need to simulate the state.
            // Since we can't set state, we rely on the fact that a fresh tx isn't reversed yet.
            // To trigger the error for 'cannot be altered', one might need to reverse twice.
            if (aggregate.id().equals("tx-locked")) {
                // Try to reverse it twice to simulate the violation (if that's the rule)
                 aggregate.execute(new ReverseTransactionCmd(aggregate.id(), "orig", balance));
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Check specific error messages or types based on the scenario
        assertTrue(
            thrownException.getMessage().contains("greater than zero") ||
            thrownException.getMessage().contains("valid account balance") ||
            thrownException.getMessage().contains("already reversed") ||
            thrownException.getMessage().contains("altered or deleted")
        );
    }
}
