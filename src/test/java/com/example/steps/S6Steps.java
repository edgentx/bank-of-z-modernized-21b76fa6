package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // State helpers
    private String accountNumber;
    private AccountAggregate.AccountStatus newStatus;
    private AccountAggregate.AccountStatus currentStatus;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.accountNumber = "ACC-001";
        this.account = new AccountAggregate(accountNumber);
        // Default hydrated state for a valid account
        account.hydrate(
            AccountAggregate.AccountStatus.ACTIVE, 
            new BigDecimal("500.00"), 
            Currency.getInstance("USD"), 
            AccountAggregate.AccountType.SAVINGS
        );
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // accountNumber is implicitly set in the Given step or command
        this.accountNumber = "ACC-001";
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        this.newStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            // Note: We execute on the aggregate instance we hydrated.
            // The command contains the ID to verify immutability/invariant.
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(accountNumber, newStatus);
            resultEvents = account.execute(cmd);
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
        Assertions.assertEquals("account.status.updated", event.type());
        Assertions.assertEquals(accountNumber, event.aggregateId());
        Assertions.assertEquals(newStatus, event.newStatus());
    }

    // Scenario 2: Balance violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        this.accountNumber = "ACC-LOW-BAL";
        this.account = new AccountAggregate(accountNumber);
        // Savings account with $50 balance. Min is $100.
        account.hydrate(
            AccountAggregate.AccountStatus.ACTIVE, 
            new BigDecimal("50.00"), 
            Currency.getInstance("USD"), 
            AccountAggregate.AccountType.SAVINGS
        );
        this.newStatus = AccountAggregate.AccountStatus.FROZEN; // Triggering a check
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // In real code, we might use a custom DomainException, but IllegalStateException or IllegalArgumentException works for this exercise
        // The prompt says "rejected with a domain error"
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Scenario 3: Active status violation
    // Interpretation: Trying to change status when the account is NOT active (e.g. Frozen) should be rejected, 
    // OR trying to change from Active to something else is rejected. 
    // Given the phrasing "Account must be in Active status to process...", and the rejection requirement,
    // I will setup a FROZEN account and try to update it (maybe to close it), expecting rejection.
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        this.accountNumber = "ACC-FROZEN";
        this.account = new AccountAggregate(accountNumber);
        account.hydrate(
            AccountAggregate.AccountStatus.FROZEN, 
            new BigDecimal("500.00"), 
            Currency.getInstance("USD"), 
            AccountAggregate.AccountType.SAVINGS
        );
        // Trying to change status again
        this.newStatus = AccountAggregate.AccountStatus.CLOSED;
    }

    // Scenario 4: Immutable number
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        this.accountNumber = "ACC-001"; // The aggregate ID
        this.account = new AccountAggregate(accountNumber);
        account.hydrate(
            AccountAggregate.AccountStatus.ACTIVE, 
            new BigDecimal("100.00"), 
            Currency.getInstance("USD"), 
            AccountAggregate.AccountType.CHECKING
        );
        // Attempting to change the account number via the command payload (simulating the invariant violation)
        this.newStatus = AccountAggregate.AccountStatus.FROZEN;
        // The 'violation' context implies we send a command with a DIFFERENT number than the aggregate ID
        this.accountNumber = "ACC-999"; // Command number differs from Aggregate ID
    }

}
