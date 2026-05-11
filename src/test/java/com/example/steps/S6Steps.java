package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate account;
    private UpdateAccountStatusCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup a standard account, ACTIVE, balance above minimum
        this.account = new AccountAggregate("ACC-123", "SAVINGS");
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Implicit in the command creation
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // Implicit in the command creation
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            // Create command to change to FROZEN
            this.command = new UpdateAccountStatusCmd("ACC-123", AccountAggregate.AccountStatus.FROZEN);
            this.resultEvents = account.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("ACC-123", event.aggregateId());
        assertEquals("FROZEN", event.newStatus());
    }

    // Scenario 2: Balance Violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // Create account with balance 50.00 (below min 100.00)
        // Assuming we can set state via constructor or reflection, or simulating a loaded aggregate
        // For this test, we use the all-args constructor to simulate a loaded state with low balance
        this.account = new AccountAggregate("ACC-LOW", "SAVINGS", new BigDecimal("50.00"), AccountAggregate.AccountStatus.ACTIVE);
    }

    // We reuse the When/Then from previous, but check for error
    // In Cucumber, we can reuse step definitions or write specific ones.
    // Let's write specific Then to be explicit.

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("Account balance cannot drop below the minimum required balance"));
    }

    // Scenario 3: Status Violation
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // Create account that is FROZEN
        this.account = new AccountAggregate("ACC-FRZ", "SAVINGS", new BigDecimal("200.00"), AccountAggregate.AccountStatus.FROZEN);
    }

    // Scenario 4: Immutable Account Number (Simulated)
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutability() {
        // This is a tricky scenario to setup purely via aggregate state without repository context.
        // However, the Gherkin implies the aggregate is "in" this state.
        // We can interpret this as the aggregate detecting a conflict.
        // Or, we might pass a command with a mismatching ID.
        // Let's assume the command tries to update "ACC-123" but the aggregate is "ACC-999".
        this.account = new AccountAggregate("ACC-999", "SAVINGS");
    }

    // Override When for Scenario 4 to trigger the ID mismatch logic if we implemented it in aggregate.
    // My aggregate implementation uses the command's accountNumber to find the aggregate (repository pattern).
    // But here we have the instance. 
    // If the aggregate holds the ID, and we execute a command meant for another ID, that's an invariant violation.
    // Let's add a check in the `execute` method: `if (!cmd.accountNumber().equals(this.id())) throw ...`
    // I will update the aggregate class to support this check.

    // Adding a specific When for Scenario 4 context would be cleaner, but I'll update the generic one or rely on setup.
    // For simplicity, let's assume the generic When works, but we pass a command with a DIFFERENT ID.
    @When("the UpdateAccountStatusCmd command is executed with mismatched ID")
    public void theUpdateAccountStatusCmdCommandIsExecutedWithMismatchedID() {
        try {
            // Account ID is ACC-999, command is for ACC-123
            this.command = new UpdateAccountStatusCmd("ACC-123", AccountAggregate.AccountStatus.FROZEN);
            this.resultEvents = account.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

}
