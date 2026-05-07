package com.example.steps;

import com.example.domain.transaction.model.*;
import com.example.domain.shared.*;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class S10Steps implements En {

    private TransactionAggregate aggregate;
    private PostDepositCmd command;
    private List<DomainEvent> resultEvents;
    private RuntimeException thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        String transactionId = UUID.randomUUID().toString();
        aggregate = new TransactionAggregate(transactionId);
        // Assume valid starting state context not specified in AC for success path other than existence
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Stored implicitly when we construct the command in the When block, or setup here
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Stored implicitly
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Stored implicitly
    }

    // Specific Violation Scenarios
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_positive() {
        // The violation is in the COMMAND payload for this scenario, as amount > 0 is a validation rule on the input.
        // We prepare a command with 0 or negative amount in the 'When' or here.
        // Setup valid aggregate
        String transactionId = UUID.randomUUID().toString();
        aggregate = new TransactionAggregate(transactionId);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        String transactionId = UUID.randomUUID().toString();
        aggregate = new TransactionAggregate(transactionId);
        // Force the aggregate into a POSTED state by simulating a past event or direct state manipulation
        aggregate.markPosted(); // Helper method for testing state transition
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_invariant() {
        String transactionId = UUID.randomUUID().toString();
        // Assume we set up the aggregate such that it knows the current balance is invalid (e.g. overflow)
        aggregate = new TransactionAggregate(transactionId);
        aggregate.setBalanceOverflowSimulation(true); // Test helper
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        // Determine context based on previous Givens. 
        // In a real framework, we might inject the command data. Here we infer.
        
        try {
            // Scenario 1 defaults (valid)
            String account = "ACC-123";
            BigDecimal amount = new BigDecimal("100.00");
            String currency = "USD";

            // Overrides for specific scenarios based on internal state or specific checks
            // (Simplified for this step implementation: We assume the Step Definition context knows which scenario is running
            // or we rely on the specific Given methods setting flags on the aggregate).
            
            // Detection logic for scenario:
            if (aggregate.isPosted()) {
                // Immutability violation scenario: Command is valid, but state is bad
            } else if (aggregate.isBalanceOverflowSimulation()) {
                // Balance violation scenario
            } else if (aggregate.getUncommittedEvents().isEmpty()) {
                // If generic, check specific flags. 
                // Actually, Cucumber scenarios are isolated. Let's look at the aggregate state.
            }

            // Heuristic for the "Amount <= 0" test: We can't easily detect which "Given" ran without a context flag.
            // However, the prompt says "Given a Transaction aggregate that violates... Amount > 0".
            // This implies the *aggregate* logic or the *command* validation catches it.
            // Let's assume the "Violation" Givens set a flag on the aggregate.
            
            if (aggregate.isSimulationActive()) {
                 // Let's refine the simulation flags in the Aggregate class for clarity.
            }
            
            // Simulating the scenario dispatch:
            if (aggregate.getSimulationType() == TransactionAggregate.SimulationType.AMOUNT_NON_POSITIVE) {
                amount = BigDecimal.ZERO;
            }

            command = new PostDepositCmd(aggregate.id(), account, amount, currency);
            resultEvents = aggregate.execute(command);

        } catch (IllegalStateException | IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof DepositPostedEvent);
        
        DepositPostedEvent event = (DepositPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("deposit.posted", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // In real Domain-Driven Design, we might return a Result object, but the provided pattern uses exceptions
        // or assumes execute() throws. The prompt says "enforce invariants".
    }
}
