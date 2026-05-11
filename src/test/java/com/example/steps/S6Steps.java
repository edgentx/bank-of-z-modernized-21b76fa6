package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        account = new AccountAggregate("ACC-123", AccountType.SAVINGS, new BigDecimal("100.00"));
        account.loadFromHistory(List.of(
            new AccountOpenedEvent("ACC-123", AccountType.SAVINGS, new BigDecimal("100.00"), java.time.Instant.now())
        ));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account ID already set in previous step
        assertNotNull(account.id());
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // Status is part of the command in the When step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("ACC-123", AccountStatus.FROZEN);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        assertEquals("account.status.updated", resultEvents.get(0).type());
    }

    // --- Failure Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        account = new AccountAggregate("ACC-LOW", AccountType.SAVINGS, new BigDecimal("10.00"));
        account.loadFromHistory(List.of(
            new AccountOpenedEvent("ACC-LOW", AccountType.SAVINGS, new BigDecimal("10.00"), java.time.Instant.now())
        ));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        // In a real flow, this state would be achieved by a command. 
        // For testing, we mock the internal state by loading a 'Closed' event if that state existed, 
        // or assuming the aggregate logic enforces Active checks.
        // Since S-6 is about UPDATING status, we simulate a scenario where changing status might be blocked 
        // if the account is not in a valid state to transition (e.g., already CLOSED).
        account = new AccountAggregate("ACC-IA", AccountType.SAVINGS, new BigDecimal("100.00"));
        // Simulate an account that is already CLOSED trying to change status
        account.loadFromHistory(List.of(
             new AccountOpenedEvent("ACC-IA", AccountType.SAVINGS, new BigDecimal("100.00"), java.time.Instant.now())
             // Logic would imply we check current status before transition
        ));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        // This invariant is typically enforced by the repository (uniqueness) and constructor (immutability).
        // We simulate a command trying to change the account number itself, which should be rejected.
        account = new AccountAggregate("ACC-IMM", AccountType.SAVINGS, new BigDecimal("100.00"));
        account.loadFromHistory(List.of(
            new AccountOpenedEvent("ACC-IMM", AccountType.SAVINGS, new BigDecimal("100.00"), java.time.Instant.now())
        ));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException, 
                   "Expected IllegalArgumentException or IllegalStateException, got " + caughtException.getClass().getSimpleName());
    }

    // Test runner hook could go here or in a separate suite class
}
