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

    private AccountAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        aggregate = new AccountAggregate(
            "ACC-123", 
            AccountAggregate.AccountType.SAVINGS, 
            new BigDecimal("500.00")
        );
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Account number is set in the aggregate creation above
        // We assume the command uses this same number.
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // Status is provided in the command execution step below
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            var cmd = new UpdateAccountStatusCmd("ACC-123", AccountAggregate.AccountStatus.FROZEN);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
    }

    // --- Error Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // Minimum balance for SAVINGS is 100.00 in our mock logic.
        // We create an account with 50.00.
        aggregate = new AccountAggregate(
            "ACC-LOW-BAL", 
            AccountAggregate.AccountType.SAVINGS, 
            new BigDecimal("50.00")
        );
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // Create a FROZEN account. The business rule in our aggregate 
        // prevents re-activating a frozen account directly (mocked logic for the requirement).
        aggregate = new AccountAggregate(
            "ACC-FROZEN", 
            AccountAggregate.AccountType.CHECKING, 
            new BigDecimal("100.00")
        );
        // Force it to frozen to simulate the state violation
        aggregate.execute(new UpdateAccountStatusCmd("ACC-FROZEN", AccountAggregate.AccountStatus.FROZEN));
        aggregate.clearEvents(); // Clear setup events
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutability() {
        // We simulate this by trying to pass a DIFFERENT account number in the command 
        // than what the aggregate holds.
        aggregate = new AccountAggregate(
            "ACC-ORIG", 
            AccountAggregate.AccountType.CHECKING, 
            new BigDecimal("0.00")
        );
    }

    // Re-use the same When step

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Specific overrides for the different violation types to ensure test isolation
    
    @When("the UpdateAccountStatusCmd command is executed with mismatched ID")
    public void theUpdateAccountStatusCmdCommandIsExecutedWithMismatchedId() {
        try {
            // Intentionally use wrong ID
            var cmd = new UpdateAccountStatusCmd("ACC-WRONG", AccountAggregate.AccountStatus.FROZEN);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the UpdateAccountStatusCmd command is executed to activate frozen")
    public void theUpdateAccountStatusCmdCommandIsExecutedToActivateFrozen() {
        try {
             // Try to activate the frozen account created in the Given step
            var cmd = new UpdateAccountStatusCmd("ACC-FROZEN", AccountAggregate.AccountStatus.ACTIVE);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

}
