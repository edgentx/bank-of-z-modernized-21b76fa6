package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.account.repository.AccountRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryAccountRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    // In-memory repository to manage aggregate state during tests
    private final AccountRepository repository = new InMemoryAccountRepository();
    private AccountAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Create a valid active account with a balance above minimum
        aggregate = new AccountAggregate("ACC-123");
        // Simulate a previous state via a constructor or setter, 
        // or by applying a prior event. For this test, we'll rely on the aggregate
        // having defaults or we'll update it directly (Test logic).
        // Ideally: aggregate.apply(new AccountOpenedEvent(...));
        // Assuming defaults or test-friendly setters for BDD context setup.
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Valid account number implied by the aggregate ID "ACC-123"
        assertNotNull(aggregate.id());
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // Status handled in the When block construction
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinBalance() {
        aggregate = new AccountAggregate("ACC-LOW-BAL");
        // Setup: Assume account is OPEN, balance is 0, minimum is 100.
        // We want to ensure the command handling checks this.
        // Since S-6 is about Status Update, the specific invariant implementation
        // depends on the business logic. Here we create the context for the test.
        // Note: Changing status to 'ACTIVE' might require a balance check.
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        aggregate = new AccountAggregate("ACC-INACTIVE");
        // The violation here is actually trying to change status when the current state
        // doesn't permit it, OR the command itself is attempting an invalid transition.
        // The Gherkin says "violates: ... must be in Active status to process..."
        // This implies we are checking the invariant inside the command handler.
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableId() {
        aggregate = new AccountAggregate("ACC-ORIG");
        // The command will attempt to pass a *different* account number 
        // than the aggregate ID to simulate a violation of immutability.
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            // We determine the command payload based on the scenario context.
            // In a real Cucumber setup, we might parse Examples table.
            // Here we infer based on the "Given":
            String targetId = aggregate.id();
            String newStatus = "FROZEN"; // Default valid transition for success case

            if (aggregate.id().equals("ACC-ORIG")) {
                targetId = "DIFFERENT-ID"; // Violate Immutability
            }

            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(targetId, newStatus, BigDecimal.ZERO);
            
            // Execute via repository pattern or directly on aggregate
            // pattern: aggregate.execute(cmd);
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("account.status.updated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Depending on implementation, this could be IllegalStateException, IllegalArgumentException, or a custom DomainException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
