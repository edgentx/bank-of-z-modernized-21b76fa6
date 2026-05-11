package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup: Valid Account with zero balance and Active status
        this.account = new AccountAggregate("ACC-123");
        // Seed state directly for test (bypassing OpenAccountCmd for simplicity in unit test)
        // In a real scenario, we might hydrate from events, but here we instantiate with valid state.
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // The account number is implicitly valid in the setup (ACC-123)
        // No specific action required unless passing a specific number to the command context.
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        this.account = new AccountAggregate("ACC-DEBT");
        // Setup: Balance is positive (e.g., 100.00)
        this.account.setBalance(BigDecimal.valueOf(100.00));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        this.account = new AccountAggregate("ACC-CLOSED");
        // Setup: Status is already CLOSED or FROZEN
        this.account.setStatus(com.example.domain.account.model.AccountAggregate.Status.CLOSED);
        // Ensure balance is zero for this specific violation test
        this.account.setBalance(BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesNumber() {
        // Simulating a scenario where the command/account ID mismatch or is invalid.
        // Since the aggregate is rooted by ID, we simulate an 'immutable' violation by attempting
        // to close an account with a mismatched ID context if the Command required it, or simply
        // relying on the aggregate's internal invariant check.
        // Here we create a valid aggregate, but we might execute a command that logically violates invariants.
        // For the purpose of this BDD, we treat the aggregate as having an invalid state.
        this.account = new AccountAggregate(""); // Empty ID violates uniqueness/format
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            Command cmd = new CloseAccountCmd(account.id());
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNull(resultEvents, "No events should be emitted on error");
        assertNotNull(thrownException, "An exception should be thrown");
        // Verify it's the specific domain error
        // The specific message depends on the invariant implementation, but usually IllegalArgumentException or IllegalStateException.
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
