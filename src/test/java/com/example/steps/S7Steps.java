package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private Throwable thrownException;
    private List<DomainEvent> resultEvents;

    // Scenario 1: Success
    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // We construct a valid, active account with zero balance
        aggregate = new AccountAggregate("ACC-123", "ACC-123", BigDecimal.ZERO, "ACTIVE", "CHECKING");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // The account number is already set in the aggregate
        assertNotNull(aggregate.id());
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        Command cmd = new CloseAccountCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent, "Event should be AccountClosedEvent");
    }

    // Scenario 2: Balance violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        // Non-zero balance for a checking account (min is 0)
        aggregate = new AccountAggregate("ACC-456", "ACC-456", new BigDecimal("100.00"), "ACTIVE", "CHECKING");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        assertTrue(thrownException.getMessage().contains("balance"), "Error message should mention balance");
    }

    // Scenario 3: Status violation
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        // Account is already closed or dormant
        aggregate = new AccountAggregate("ACC-789", "ACC-789", BigDecimal.ZERO, "DORMANT", "CHECKING");
    }

    // Scenario 4: Immutability violation
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        // The aggregate ID is ACC-100, but we will attempt to close it using a different ID in the command
        aggregate = new AccountAggregate("ACC-100", "ACC-100", BigDecimal.ZERO, "ACTIVE", "CHECKING");
    }

}
