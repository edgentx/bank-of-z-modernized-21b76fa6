package com.example.steps;

import com.example.domain.account.command.CloseAccountCmd;
import com.example.domain.account.event.AccountClosedEvent;
import com.example.domain.account.model.AccountAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Givens ---

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // Standard account with 0 balance (ready to close)
        aggregate = new AccountAggregate("123456", BigDecimal.ZERO, "CHECKING");
        // Ensure active state
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Implicitly handled by the aggregate setup in the previous step,
        // assuming the command is constructed using the aggregate's ID.
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        // Setup a valid aggregate, but give it a balance > 0
        aggregate = new AccountAggregate("123456", BigDecimal.valueOf(100.00), "CHECKING");
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        // Setup a valid aggregate, but set status to CLOSED or FROZEN
        aggregate = new AccountAggregate("123456", BigDecimal.ZERO, "CHECKING");
        aggregate.setStatus(AccountAggregate.AccountStatus.FROZEN);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        // We simulate this by creating an aggregate with one ID,
        // but executing a command with a different ID.
        aggregate = new AccountAggregate("ACTUAL_ID", BigDecimal.ZERO, "CHECKING");
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    // --- Whens ---

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            // We derive the command ID from the aggregate ID, EXCEPT for the immutability violation test
            String cmdId;
            if ("ACTUAL_ID".equals(aggregate.id())) {
                // For the violation test, we send a DIFFERENT ID to trigger the mismatch error
                cmdId = "DIFFERENT_ID";
            } else {
                cmdId = aggregate.id();
            }

            CloseAccountCmd cmd = new CloseAccountCmd(cmdId);
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    // --- Thens ---

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null (likely an exception occurred).");
        assertEquals(1, resultEvents.size(), "Expected exactly one event.");
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent, "Expected AccountClosedEvent.");

        // Verify Aggregate State Changed
        assertEquals(AccountAggregate.AccountStatus.CLOSED, aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but command succeeded.");
        // In this domain, invariant violations are modeled as IllegalStateExceptions
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException, got " + capturedException.getClass().getSimpleName());
    }
}
