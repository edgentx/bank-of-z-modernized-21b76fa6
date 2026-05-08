package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate aggregate;
    private UpdateAccountStatusCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup a standard valid account
        aggregate = new AccountAggregate("ACC-123", "Active", new BigDecimal("500.00"), "Checking");
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled in the When step construction
    }

    @Given("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // Handled in the When step construction
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            // Default valid command construction
            String accNum = (aggregate != null) ? aggregate.id() : "ACC-123";
            command = new UpdateAccountStatusCmd(accNum, "Frozen");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.status.updated", resultEvents.get(0).type());
    }

    // --- Failure Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinBalance() {
        // Create a Savings account with balance below minimum (100)
        aggregate = new AccountAggregate("ACC-LOW", "Active", new BigDecimal("50.00"), "Savings");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // Create an account that is already Frozen/Closed
        aggregate = new AccountAggregate("ACC-FRZ", "Frozen", new BigDecimal("500.00"), "Checking");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableAccountNumber() {
        aggregate = new AccountAggregate("ACC-ORIG", "Active", new BigDecimal("100.00"), "Checking");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Verify it's not an UnknownCommandException, but a business logic violation
        assertFalse(thrownException instanceof UnknownCommandException);
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecutedForImmutableFailure() {
        try {
            // Scenario: Command attempts to target a different ID than the aggregate
            command = new UpdateAccountStatusCmd("ACC-FAKE", "Frozen"); // Mismatched ID
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecutedForStatusFailure() {
        try {
             // Scenario: Trying to change status while Frozen (based on logic in Aggregate)
             command = new UpdateAccountStatusCmd("ACC-FRZ", "Frozen");
             resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
    
    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecutedForBalanceFailure() {
        try {
             // Scenario: Low balance
             command = new UpdateAccountStatusCmd("ACC-LOW", "Frozen");
             resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
