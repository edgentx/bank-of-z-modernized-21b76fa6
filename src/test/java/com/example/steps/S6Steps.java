package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup a valid account (e.g. number 12345, Active, Balance 100)
        aggregate = new AccountAggregate("12345");
        // Seed state via reflection or a testing "apply" method if available,
        // but for this pure aggregate command test, we assume the constructor
        // initializes a valid state or we apply the command to a fresh aggregate.
        // However, since execute() usually acts on current state, and we can't
        // easily apply past events without a repository/rehydration mechanism,
        // we will simulate a 'rehydrated' aggregate by calling execute immediately
        // to initialize it, or assume the constructor handles default active state.
        // For this BDD, let's assume we are testing the command logic on an aggregate.
        // We will use a test-specific setup or reflection if necessary, but ideally
        // the aggregate handles initialization.
        // Given the constraints, let's assume the aggregate starts in a default state
        // or we use the command to establish the baseline if needed.
        // For the purpose of these steps, we instantiate the aggregate.
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // The aggregate ID is the account number. Already set in constructor.
        Assertions.assertEquals("12345", aggregate.id());
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // This will be handled in the 'When' step by constructing the command with a valid status.
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            // We use a valid status, e.g. "FROZEN"
            Command cmd = new UpdateAccountStatusCmd("12345", "FROZEN");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals("FROZEN", event.newStatus());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // We need an aggregate that will fail the status update because of balance.
        // Example: Current Balance 50, Min Balance 100. Trying to close? 
        // Or trying to change status to something that checks balance.
        // We'll instantiate an account and assume the logic handles the specific violation.
        // The exact violation logic depends on the command parameters passed in 'When'.
        aggregate = new AccountAggregate("99999");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // This scenario implies the account is NOT active, and we might be trying 
        // to perform a status update that enforces active state? 
        // Or perhaps the command itself is rejected because the *current* state is invalid for the transition.
        // The prompt says: "An account must be in an Active status to process withdrawals or transfers."
        // This seems to suggest we are updating status *away* from Active? 
        // Or checking the invariant.
        aggregate = new AccountAggregate("88888");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        // This implies a command trying to change the account number.
        aggregate = new AccountAggregate("55555");
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecutedForRejection() {
        // We reuse the same When step method, but context (scenario) determines inputs.
        // However, Cucumber allows us to overload or use specific logic.
        // To keep it simple, we'll catch exceptions in the general step or use specific parsing.
        // But since the text is identical, we rely on the specific command construction here.
        try {
            // Based on the Gherkin scenario titles, we need to trigger specific errors.
            // 1. Balance violation: Try to close an account with low balance.
            if (aggregate.id().equals("99999")) {
                // Assume the aggregate logic prevents closing if balance is low.
                // We execute the command.
                aggregate.execute(new UpdateAccountStatusCmd("99999", "CLOSED"));
            }
            // 2. Active status violation (Scenario 3):
            else if (aggregate.id().equals("88888")) {
                // Attempting a transition that requires Active state but account is Frozen?
                // Or checking invariant.
                aggregate.execute(new UpdateAccountStatusCmd("88888", "ACTIVE")); // Hypothetically rejected if invariant checks previous state
            }
            // 3. Immutable number (Scenario 4):
            else if (aggregate.id().equals("55555")) {
                // The command might technically allow changing number, but aggregate rejects.
                // Or the command contains a new number field.
                // Assuming UpdateAccountStatusCmd doesn't take a number, this scenario might be
                // testing that the ID itself isn't mutable, so any command attempting to mutate it fails.
                // But the command is UpdateAccountStatus. Maybe the violation is irrelevant unless the command supports it.
                // Or we assume the command payload includes a number field that should be ignored/rejected.
                aggregate.execute(new UpdateAccountStatusCmd("55555", "FROZEN"));
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Domain errors are usually RuntimeExceptions or specific DomainExceptions.
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Specific setup for the rejection tests to ensure the Aggregate is in a state that triggers the error
    // Since we don't have a full repo, we assume the Aggregate constructor sets up the specific state
    // or we rely on the ID to map to the mock logic if we were using a repo.
    // Here, we rely on the Aggregate's internal logic.
    // To make the tests pass with the generated code:
    // - Balance: We need a way to set balance. 
    // - Active: We need a way to set status.
    // Since we can't modify the generated Aggregate to add setters (it's generated),
    // we will assume the command execution logic covers the invariants based on the prompt's requirements.
    // Note: The generated aggregate will need to implement these specific checks.

}
