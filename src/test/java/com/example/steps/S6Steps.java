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

/**
 * Cucumber Steps for S-6: UpdateAccountStatusCmd.
 */
public class S6Steps {

    private AccountAggregate account;
    private String providedAccountNumber;
    private String providedNewStatus;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup a standard valid account for the happy path
        account = new AccountAggregate("acc-123");
        account.setAccountNumber("ACCT-001");
        account.setAccountType("Standard");
        account.setBalance(new BigDecimal("500.00"));
        account.setStatus("Active");
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        providedAccountNumber = "ACCT-001";
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        providedNewStatus = "Frozen";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            Command cmd = new UpdateAccountStatusCmd(providedAccountNumber, providedNewStatus);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent, "Event type mismatch");
        
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals("Active", event.oldStatus());
        assertEquals("Frozen", event.newStatus());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalanceRule() {
        account = new AccountAggregate("acc-violation-balance");
        account.setAccountNumber("ACCT-002");
        account.setAccountType("Premium"); // Min balance usually 1000
        account.setBalance(new BigDecimal("50.00")); // Violates min balance
        account.setStatus("Active");
        providedAccountNumber = "ACCT-002";
        providedNewStatus = "Frozen"; // Triggering the check
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException, 
            "Expected domain error (IllegalStateException or IllegalArgumentException)");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatusRule() {
        account = new AccountAggregate("acc-violation-status");
        account.setAccountNumber("ACCT-003");
        account.setAccountType("Standard");
        account.setBalance(new BigDecimal("200.00"));
        account.setStatus("Frozen"); // Not Active
        providedAccountNumber = "ACCT-003";
        providedNewStatus = "Closed";
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumberRule() {
        account = new AccountAggregate("acc-violation-immutable");
        account.setAccountNumber("ACCT-SET-IN-STONE");
        account.setStatus("Active");
        // User provides a different number than what is in the aggregate
        providedAccountNumber = "ACCT-TAMPERED"; 
        providedNewStatus = "Active";
    }

}
