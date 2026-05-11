package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private UpdateAccountStatusCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup a valid account with sufficient balance
        account = new AccountAggregate("ACC-123", AccountType.SAVINGS, BigDecimal.valueOf(500));
        Assertions.assertNotNull(account);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinBalance() {
        // Balance is 0, Min is 100 for Savings
        account = new AccountAggregate("ACC-LOW", AccountType.SAVINGS, BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // Create a valid account, then manually set it to CLOSED to violate the invariant
        account = new AccountAggregate("ACC-CLOSED", AccountType.CHECKING, BigDecimal.valueOf(100));
        // Using reflection or a test-specific method to force state for the sake of the scenario "Given"
        // Since aggregate doesn't expose a setStatus, we assume the invariant is about the *transition*
        // or we mock a scenario where the account is already closed.
        // In this specific BDD context, we assume we are testing the invariant check.
        // To simulate the violation cleanly, we'll pretend the account is Frozen.
        // Note: The step says "Given an account that violates...".
        // I will use a helper to mutate state for testing purposes if needed, or rely on the logic:
        // If I try to update a CLOSED account to FROZEN, it should fail.
        // Actually, let's assume the AccountStatusUpdatedEvent is the only way to change state.
        // So we need an account that is NOT active. Let's make it FROZEN.
        account = new AccountAggregate("ACC-FROZEN", AccountType.CHECKING, BigDecimal.valueOf(100));
        // Force status update via package-private or constructor hack if available.
        // Since I control the domain, I'll update the aggregate to be FROZEN by issuing a command that works.
        account.execute(new UpdateAccountStatusCmd("ACC-FROZEN", AccountStatus.FROZEN));
        account.clearEvents(); // Clear setup events
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutable() {
        account = new AccountAggregate("ACC-IMMUTABLE", AccountType.SAVINGS, BigDecimal.valueOf(100));
        account.markImmutable(); // Flag it as immutable to simulate the violation condition
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // The account number is usually part of the aggregate or command.
        // We will construct the command using the aggregate's ID.
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // We will construct the command with a valid status (e.g. FROZEN or CLOSED depending on context)
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            // For the happy path, we freeze it.
            AccountStatus targetStatus = AccountStatus.FROZEN;
            // For the balance violation test, we try to close it.
            if (account.id().equals("ACC-LOW")) {
                targetStatus = AccountStatus.CLOSED;
            }
            // For the active status violation test (account is already FROZEN), try to close it (not allowed unless active).
            if (account.id().equals("ACC-FROZEN")) {
                targetStatus = AccountStatus.CLOSED;
            }
            // For the immutable test
            if (account.id().equals("ACC-IMMUTABLE")) {
                targetStatus = AccountStatus.CLOSED;
            }

            cmd = new UpdateAccountStatusCmd(account.id(), targetStatus);
            resultingEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof AccountStatusUpdatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain error (exception)");
        // In Java, we usually use RuntimeExceptions or specific DomainExceptions.
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
