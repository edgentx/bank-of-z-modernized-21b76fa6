package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup a clean account with zero balance, active status, and valid immutable number
        String validId = "ACC-12345";
        account = new AccountAggregate(validId);
        
        // Initialize the aggregate to a valid open state via internal reflection or public setters if available.
        // For this test harness, we simulate a valid state directly.
        // Since execute is the only entry point in the pattern, we might need a constructor that allows hydration 
        // or a factory method. Here we assume we can manually set fields for the 'Given' part of a unit test.
        account.setAccountNumber(validId); // Immutable set once
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.ACTIVE);
        account.setMinimumBalance(BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Implicitly handled by the aggregate setup, but the command carries the number for validation
    }

    @And("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        account = new AccountAggregate("ACC-DEBT");
        account.setAccountNumber("ACC-DEBT");
        account.setBalance(new BigDecimal("50.00"));
        account.setMinimumBalance(new BigDecimal("100.00"));
        account.setStatus(AccountAggregate.Status.ACTIVE);
    }

    @And("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        account = new AccountAggregate("ACC-CLOSED");
        account.setAccountNumber("ACC-CLOSED");
        account.setBalance(BigDecimal.ZERO);
        account.setMinimumBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.CLOSED); // Not active
    }

    @And("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        account = new AccountAggregate("ORIGINAL-ID");
        account.setAccountNumber("ORIGINAL-ID");
        account.setBalance(BigDecimal.ZERO);
        account.setMinimumBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.ACTIVE);
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        Command cmd;
        
        if (account.getAccountNumber().equals("ORIGINAL-ID")) {
            // Scenario: Trying to close with a DIFFERENT account number than the aggregate holds
            cmd = new CloseAccountCmd("DIFFERENT-ID");
        } else {
            // Standard Close command matching the aggregate ID
            cmd = new CloseAccountCmd(account.getAccountNumber());
        }

        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Result events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof AccountClosedEvent, "Event should be AccountClosedEvent");
        Assertions.assertEquals("AccountClosed", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        // Verify it's a domain invariant violation (usually IllegalArgumentException or IllegalStateException)
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Exception should be a domain error"
        );
    }
}
