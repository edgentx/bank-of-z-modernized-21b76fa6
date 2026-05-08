package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private CloseAccountCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // Default valid state: Active, 0 balance
        account = new AccountAggregate("ACC-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        command = new CloseAccountCmd("ACC-123");
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance_rules() {
        // Setup an account with a non-zero balance that violates the "zero balance" requirement for closing
        // Using CHECKING type with balance > 0
        account = new AccountAggregate("ACC-999", new BigDecimal("50.00"), "ACTIVE", "CHECKING");
        command = new CloseAccountCmd("ACC-999");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status_rules() {
        // Setup a CLOSED account
        account = new AccountAggregate("ACC-888", BigDecimal.ZERO, "CLOSED", "CHECKING");
        command = new CloseAccountCmd("ACC-888");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability_rules() {
        // Valid account state
        account = new AccountAggregate("ACC-777", BigDecimal.ZERO, "ACTIVE", "CHECKING");
        // Command with mismatched ID simulating an immutability violation attempt (trying to close wrong ID on aggregate)
        command = new CloseAccountCmd("ACC-DIFFERENT");
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            resultEvents = account.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals("ACC-123", resultEvents.get(0).aggregateId());
        assertEquals("CLOSED", account.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verify it's an exception indicating rejection (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        assertNull(resultEvents); // No events should be emitted on rejection
    }
}