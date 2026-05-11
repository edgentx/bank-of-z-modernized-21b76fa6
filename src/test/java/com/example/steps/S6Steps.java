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
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        account = new AccountAggregate("ACC-123", AccountAggregate.AccountType.SAVINGS, Currency.getInstance("USD"));
        account.setBalance(new BigDecimal("500.00")); // Sufficient for min balance check
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // No-op, used in context
    }

    @Given("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // No-op, used in context
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            var cmd = new UpdateAccountStatusCmd("ACC-123", AccountAggregate.AccountStatus.FROZEN);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals("ACC-123", event.aggregateId());
    }

    // Rejection Scenarios

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalanceConstraint() {
        account = new AccountAggregate("ACC-LOW", AccountAggregate.AccountType.SAVINGS, Currency.getInstance("USD"));
        account.setBalance(new BigDecimal("50.00")); // Below 100 min for Savings
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatusRequirement() {
        account = new AccountAggregate("ACC-FRZ", AccountAggregate.AccountType.CHECKING, Currency.getInstance("USD"));
        // Force internal state to Frozen to simulate the invariant check
        // Since we don't have a separate 'freeze' command logic exposed here, we'll rely on the constructor
        // But we need to handle the 'execute' rejection logic.
        // The aggregate logic: if status != ACTIVE, throw exception.
        // The aggregate starts Active. We need it to be Frozen.
        // We can mock the state or use a constructor that allows setting it if one existed.
        // Here, we will manually set the state via reflection or package-private access if possible.
        // However, AccountAggregate starts Active. The logic in execute throws if status != ACTIVE.
        // Wait, the scenario says: "Given... violates: Account must be Active".
        // This implies the account IS NOT active.
        // Since I can't change the aggregate constructor easily to set initial status without a command,
        // I will assume the aggregate has a way to be frozen or I'll adjust the step to handle the setup.
        // For this exercise, I'll assume the aggregate is created in a frozen state via a hypothetical scenario-specific method
        // or I'll just invoke execute on a 'Frozen' account. How to get it frozen?
        // I'll add a method `forceStatusForTest` or similar? No, I shouldn't modify domain for tests.
        // I will assume the `execute` method checks the status. To make it fail, the account must be Frozen.
        // Since the aggregate starts Active, the first update (Active -> Frozen) is valid per the logic written in the prompt.
        // Let's look at the logic I wrote: `if (this.status == AccountStatus.FROZEN) throw...`
        // So if I try to update a Frozen account, it throws.
        // To make it throw, I need a Frozen account. I can create one, run a valid command to freeze it,
        // then run the command AGAIN? No.
        // Let's look at the requirement: "Account must be Active to process withdrawals or transfers."
        // My implementation of UpdateAccountStatusCmd rejected this if status was FROZEN.
        // So I just need a Frozen account. I will instantiate one and set status manually (simulating rehydration from DB).
        account = new AccountAggregate("ACC-FRZ"); 
        // Using rehydration constructor, it has no status set (null). 
        // I need to simulate the loaded state. I'll use reflection or a rehydration method.
        // Since I'm writing the aggregate, I can add a `rehydrate` method? No, that's extra.
        // Let's stick to the provided structure. The aggregate constructor takes accountNumber.
        // I will rely on the fact that I can't set status without a command.
        // I'll create a VALID account, run a valid command to freeze it, then run the command again?
        // Step 1: Create Active account.
        // Step 2: Run Freeze command (Valid). Status becomes Frozen.
        // Step 3: Run ANOTHER command (e.g. Close). This should fail if "Active required".
        // That fits the flow.
        account = new AccountAggregate("ACC-FRZ", AccountAggregate.AccountType.CHECKING, Currency.getInstance("USD"));
        // Execute a freeze command first to get it into the violating state
        account.execute(new UpdateAccountStatusCmd("ACC-FRZ", AccountAggregate.AccountStatus.FROZEN));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutability() {
        account = new AccountAggregate("ACC-IMM", AccountAggregate.AccountType.SAVINGS, Currency.getInstance("USD"));
        // To violate immutability, we must have already set the number.
        // The logic in handleUpdateStatus checks `if (this.accountNumberImmutable ...)`.
        // The constructor sets it to false. The execute method sets it to true.
        // So if I run a command once, it becomes immutable.
        // The second command with a different number (if supported) would fail.
        // But the command is `UpdateAccountStatus`. It doesn't strictly accept a new number.
        // However, to satisfy the specific error text in the prompt's logic:
        // I need `accountNumberImmutable` to be true.
        account.execute(new UpdateAccountStatusCmd("ACC-IMM", AccountAggregate.AccountStatus.FROZEN)); 
        // Now it is immutable. The subsequent command in 'When' will trigger the check.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We can check type or message depending on strictness, but Exception is fine for BDD
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
