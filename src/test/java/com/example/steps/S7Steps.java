package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    public void a_valid_Account_aggregate() {
        // Setup a valid account with zero balance and ACTIVE status
        account = new AccountAggregate("ACC-123");
        // Simulate opening the account to get it into a valid state
        account.execute(new OpenAccountCmd("ACC-123", "ACC-123", "John Doe"));
        // Clear uncommitted events from setup to isolate test
        account.clearEvents();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account ID is set in the "Given a valid Account aggregate" step
        assertNotNull(account.id());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        account = new AccountAggregate("ACC-HIGH-BAL");
        // Open account
        account.execute(new OpenAccountCmd("ACC-HIGH-BAL", "ACC-HIGH-BAL", "Jane Doe"));
        // Simulate a deposit to give it a balance (Cannot close if balance != 0)
        // Note: The requirement says balance must be zero. So having a balance > 0 violates the close invariant.
        account.execute(new AccountCreditCommand("ACC-HIGH-BAL", new BigDecimal("100.00"))); // Hypothetical cmd to change balance
        account.clearEvents();
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        account = new AccountAggregate("ACC-CLOSED");
        // Open account
        account.execute(new OpenAccountCmd("ACC-CLOSED", "ACC-CLOSED", "John Smith"));
        // Close it immediately
        account.execute(new CloseAccountCmd("ACC-CLOSED"));
        account.clearEvents();
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_account_number_immutability() {
        // This scenario covers the logic where the command's AccountID doesn't match the Aggregate's ID
        // or the aggregate is not yet initialized/opened.
        account = new AccountAggregate("ACC-ORPHAN");
        // Do not open it, effectively making it invalid for operations
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            // We use the aggregate's ID for the command, except in the immutability violation case
            String cmdId = account.id().equals("ACC-ORPHAN") ? "INVALID-ID" : account.id();
            Command cmd = new CloseAccountCmd(cmdId);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertEquals("account.closed", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // Typically IllegalArgumentException or IllegalStateException
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
