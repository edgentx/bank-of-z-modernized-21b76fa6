package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> result;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        account = new AccountAggregate("ACC-123");
        // Hydrate to a valid state
        account.hydrate(new BigDecimal("500.00"), UpdateAccountStatusCmd.AccountStatus.ACTIVE, "CHECKING");
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled by aggregate construction
    }

    @Given("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // Handled in When step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("ACC-123", UpdateAccountStatusCmd.AccountStatus.FROZEN);
            result = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) result.get(0);
        assertEquals(UpdateAccountStatusCmd.AccountStatus.ACTIVE, event.oldStatus());
        assertEquals(UpdateAccountStatusCmd.AccountStatus.FROZEN, event.newStatus());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        account = new AccountAggregate("ACC-LOW");
        // Set balance low (e.g. 50) for Savings (min 100)
        account.hydrate(new BigDecimal("50.00"), UpdateAccountStatusCmd.AccountStatus.ACTIVE, "SAVINGS");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        account = new AccountAggregate("ACC-FROZEN");
        // Set status to FROZEN. The logic in execute checks if status != ACTIVE and throws.
        account.hydrate(new BigDecimal("500.00"), UpdateAccountStatusCmd.AccountStatus.FROZEN, "CHECKING");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableAccountNumber() {
        account = new AccountAggregate("ACC-MUTABLE");
        account.hydrate(new BigDecimal("100.00"), UpdateAccountStatusCmd.AccountStatus.ACTIVE, "CHECKING");
        // Force the aggregate into a state where account number is considered mutable (a violation)
        account.setAccountNumberMutable(true);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect an IllegalStateException or a custom DomainException
        assertTrue(caughtException instanceof IllegalStateException);
    }

    @When("the UpdateAccountStatusCmd command is executed for rejection")
    public void theUpdateAccountStatusCmdCommandIsExecutedForRejection() {
        // Reuse the same When logic, but the state of the aggregate determines the outcome
        theUpdateAccountStatusCmdCommandIsExecuted();
    }
}
