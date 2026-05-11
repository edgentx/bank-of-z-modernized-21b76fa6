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

public class S7Steps {

    private AccountAggregate aggregate;
    private String validAccountNumber;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        validAccountNumber = "ACC-" + System.currentTimeMillis();
        // Hydrate to a valid active state
        aggregate.hydrate(validAccountNumber, BigDecimal.ZERO, AccountAggregate.AccountStatus.ACTIVE, AccountAggregate.AccountType.CHECKING);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // validAccountNumber is already set in the previous step
        assertNotNull(validAccountNumber);
    }

    // --- Scenario 1: Success ---

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        executeCommandWithCurrentState();
    }

    private void executeCommandWithCurrentState() {
        try {
            Command cmd = new CloseAccountCmd(aggregate.id(), aggregate.getAccountNumber());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        assertEquals("account.closed", resultEvents.get(0).type());
    }

    // --- Scenario 2: Balance Violation ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        // Setup with non-zero balance
        aggregate.hydrate("ACC-NONZERO", new BigDecimal("100.00"), AccountAggregate.AccountStatus.ACTIVE, AccountAggregate.AccountType.CHECKING);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception but command succeeded");
        // Depending on implementation, could be IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // --- Scenario 3: Status Violation ---

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        // Setup with Closed status (already closed)
        aggregate.hydrate("ACC-CLOSED", BigDecimal.ZERO, AccountAggregate.AccountStatus.CLOSED, AccountAggregate.AccountType.CHECKING);
    }

    // --- Scenario 4: Immutability/Uniqueness Violation ---

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueness() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        // Setup with a specific number
        String realNumber = "ACC-ORIG";
        aggregate.hydrate(realNumber, BigDecimal.ZERO, AccountAggregate.AccountStatus.ACTIVE, AccountAggregate.AccountType.CHECKING);
        
        // We simulate the violation by attempting to close with a DIFFERENT account number 
        // than the one assigned to the aggregate, implying a mutation attempt or mismatch.
        // We override the execution logic in the @When step for this specific scenario context.
    }

    // We need a custom When for Scenario 4 because the violation logic is specific (passing wrong number in cmd)
    @When("the CloseAccountCmd command is executed with mismatched number")
    public void theCloseAccountCmdCommandIsExecutedWithMismatch() {
        try {
            // Intentionally using a different account number in the command than the aggregate holds
            Command cmd = new CloseAccountCmd(aggregate.id(), "ACC-FAKE-MUTATED");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

}
