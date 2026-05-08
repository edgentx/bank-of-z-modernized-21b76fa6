package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate account;
    private OpenAccountCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1: Success
    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        account = new AccountAggregate("acc-123");
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Command construction happens in the When clause or accumulates here
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        // Default valid command for positive path
        if (command == null) {
            command = new OpenAccountCmd(
                "acc-123",
                "cust-001",
                AccountAggregate.AccountType.CHECKING,
                new BigDecimal("500.00"),
                "10-20-30"
            );
        }
        try {
            resultEvents = account.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.opened", resultEvents.get(0).type());
    }

    // Scenario 2: Minimum Balance
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        account = new AccountAggregate("acc-low-balance");
        // Create a command that triggers the minimum balance check (e.g. SAVINGS with < 100 deposit)
        command = new OpenAccountCmd(
            "acc-low-balance",
            "cust-001",
            AccountAggregate.AccountType.SAVINGS,
            new BigDecimal("50.00"), // Below 100 min
            "10-20-30"
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // Scenario 3: Status
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // Setup account already opened
        account = new AccountAggregate("acc-status-test");
        // Open it once
        OpenAccountCmd firstCmd = new OpenAccountCmd(
            "acc-status-test",
            "cust-002",
            AccountAggregate.AccountType.CHECKING,
            BigDecimal.ZERO,
            "10-20-30"
        );
        account.execute(firstCmd);
        
        // Try to open it again (violating immutability/active status logic for this command)
        command = new OpenAccountCmd(
            "acc-status-test",
            "cust-003", // trying to change customer/state
            AccountAggregate.AccountType.SAVINGS,
            BigDecimal.ZERO,
            "10-20-30"
        );
    }

    // Scenario 4: Immutable Number
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        // This scenario overlaps with the "Active" violation in the current aggregate design
        // because the aggregate forbids re-execution of OpenAccountCmd entirely.
        // We reuse the setup.
        account = new AccountAggregate("acc-immutable-test");
        OpenAccountCmd firstCmd = new OpenAccountCmd(
            "acc-immutable-test",
            "cust-004",
            AccountAggregate.AccountType.CHECKING,
            BigDecimal.ZERO,
            "10-20-30"
        );
        account.execute(firstCmd);

        command = new OpenAccountCmd(
            "acc-immutable-test",
            "cust-004",
            AccountAggregate.AccountType.CHECKING,
            BigDecimal.ZERO,
            "10-20-30"
        );
    }
}
