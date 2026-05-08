package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Account number: 12345, Type: CHECKING, Balance: 0.00
        account = new AccountAggregate("12345", AccountAggregate.AccountType.CHECKING, BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Implicitly handled by the aggregate initialization in previous step
        // or we could verify the command creation here.
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd("12345");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException.getMessage());
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);

        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        assertEquals("account.closed", event.type());
        assertEquals("12345", event.aggregateId());
        assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // Create account with non-zero balance
        account = new AccountAggregate("67890", AccountAggregate.AccountType.SAVINGS, new BigDecimal("100.50"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // Create account and manually close it to set status to CLOSED
        account = new AccountAggregate("11111", AccountAggregate.AccountType.CHECKING, BigDecimal.ZERO);
        // Force closed state for test setup
        CloseAccountCmd init = new CloseAccountCmd("11111");
        account.execute(init); 
        // Verify it is closed
        assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutability() {
        // Scenario simulation: Aggregate initialized with ID 'A', but command targets 'B'
        account = new AccountAggregate("ACC-A", AccountAggregate.AccountType.CHECKING, BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a domain error exception");
        // Check it's a state or illegal argument exception, typical for domain invariant violations
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecutedWithMismatch() {
        try {
            // Using a mismatched account number to simulate the immutability check violation
            // The aggregate is 'ACC-A', but command targets 'ACC-B'
            CloseAccountCmd cmd = new CloseAccountCmd("ACC-B");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}