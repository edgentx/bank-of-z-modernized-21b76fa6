package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Scenario 1 Setup
    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        aggregate = new AccountAggregate("ACC-001");
        aggregate.setStatus(AccountStatus.ACTIVE);
        aggregate.setBalance(BigDecimal.valueOf(5000));
        aggregate.setAccountType("STANDARD");
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Account number is implicitly part of the aggregate ID
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // Status is part of the command
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            Command cmd = new UpdateAccountStatusCmd("ACC-001", AccountStatus.FROZEN);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals(AccountStatus.FROZEN, event.newStatus());
    }

    // Scenario 2: Minimum Balance
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        aggregate = new AccountAggregate("ACC-LOW");
        aggregate.setStatus(AccountStatus.ACTIVE); // Active is valid for checks
        aggregate.setBalance(BigDecimal.valueOf(10)); // Low balance
        aggregate.setAccountType("STUDENT"); // Type requiring 100 min
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("Account balance cannot drop below"));
    }

    // Scenario 3: Inactive Status
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        aggregate = new AccountAggregate("ACC-FROZEN");
        aggregate.setStatus(AccountStatus.FROZEN); // Not Active
        aggregate.setBalance(BigDecimal.valueOf(100));
    }

    // Scenario 4: Immutable Account Number
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableAccountNumber() {
        aggregate = new AccountAggregate("ACC-ORIG");
        aggregate.setStatus(AccountStatus.ACTIVE);
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.markNumberImmutable(); // Lock the number
    }

    @When("the UpdateAccountStatusCmd command is executed with mismatched account number")
    public void theUpdateAccountStatusCmdCommandIsExecutedWithMismatchedAccountNumber() {
        try {
            // Attempt to update using a command that implies a change (simulation)
            // The logic checks cmd.accountNumber vs aggregate.accountNumber
            Command cmd = new UpdateAccountStatusCmd("ACC-MODIFIED", AccountStatus.FROZEN);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
