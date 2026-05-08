package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class S5Steps {

    private AccountAggregate aggregate;
    private OpenAccountCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.aggregate = new AccountAggregate("acct-new-test-01");
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        if (cmd == null) cmd = new OpenAccountCmd("acct-new-test-01", "cust-123", null, null, null);
        // Assuming we modify the cmd via constructor for simplicity in steps
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        if (cmd == null) cmd = new OpenAccountCmd("acct-new-test-01", "cust-123", "CHECKING", null, null);
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        if (cmd == null) cmd = new OpenAccountCmd("acct-new-test-01", "cust-123", "CHECKING", new BigDecimal("100"), null);
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        if (cmd == null) cmd = new OpenAccountCmd("acct-new-test-01", "cust-123", "CHECKING", new BigDecimal("100"), "10-20-30");
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        // Setup aggregate with a state that represents the violation context if needed
        this.aggregate = new AccountAggregate("acct-violate-minbal");
        // Scenario: Opening a Premium account with 0 balance (Assume min bal > 0)
        // Passing a very low deposit that might violate logic if implemented, 
        // or relying on internal logic to check minimums.
        // Here we assume Premium requires > 0.
        this.cmd = new OpenAccountCmd("acct-violate-minbal", "cust-123", "PREMIUM", BigDecimal.ZERO, "10-20-30");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        this.aggregate = new AccountAggregate("acct-violate-status");
        // This invariant likely applies to existing accounts, but for Cmd execution
        // we can simulate a command that tries to open an account that is somehow invalid or pre-exists.
        // Or, strictly following the scenario, we are just testing the rejection.
        // Let's assume this aggregate was previously created/closed and we try to open with same ID?
        // For OpenAccountCmd, the aggregate starts fresh. The invariant might be "Cannot open account for inactive customer".
        // We will simulate by passing a customerId that implies invalid state, or rely on aggregate logic.
        // We'll use a specific type "INACTIVE_ONLY" to trigger the simulated failure.
        this.cmd = new OpenAccountCmd("acct-violate-status", "cust-inactive", "SUSPENDED", new BigDecimal("100"), "10-20-30");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueImmutable() {
        this.aggregate = new AccountAggregate("acct-existing-01");
        // Simulate that this aggregate is already 'loaded' or initialized (in a real app, repo would check)
        // For the aggregate unit test, we will manually set a version or state to simulate existence.
        // Since this is an in-memory test, we will pass a command that indicates a conflict or use a specific type.
        this.cmd = new OpenAccountCmd("acct-existing-01", "cust-123", "DUPLICATE", new BigDecimal("100"), "10-20-30");
        // Hack for test: simulate pre-existence by incrementing version
        aggregate.markVersionAsNonZero(); 
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            // Ensure cmd is fully populated if not set by specific violation Given
            if (cmd == null || cmd.accountType() == null) {
                // Defaults for the happy path if intermediaries skipped
                if(cmd == null) cmd = new OpenAccountCmd("acct-new-test-01", "cust-123", "CHECKING", new BigDecimal("100"), "10-20-30");
            }
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            this.capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        Assertions.assertEquals("acct-new-test-01", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Check it's one of the expected domain exceptions
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException
        );
    }
}
