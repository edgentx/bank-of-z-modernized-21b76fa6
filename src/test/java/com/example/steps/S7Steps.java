package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Setup a standard account with zero balance (valid for closing)
        account = new AccountAggregate("ACC-123");
        account.apply(new AccountOpenedEvent("ACC-123", "Active", BigDecimal.ZERO, "Standard", java.time.Instant.now()));
        this.thrownException = null;
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Implicit in the construction of the command in the When step
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_non_zero_balance() {
        // Setup account with balance > 0 (violates close requirement)
        account = new AccountAggregate("ACC-999");
        account.apply(new AccountOpenedEvent("ACC-999", "Active", new BigDecimal("100.00"), "Standard", java.time.Instant.now()));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_is_not_active() {
        // Setup account that is already closed or dormant
        account = new AccountAggregate("ACC-888");
        account.apply(new AccountOpenedEvent("ACC-888", "Dormant", BigDecimal.ZERO, "Standard", java.time.Instant.now()));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_with_invalid_id() {
        // Simulate an account with a null/invalid ID as per requirement text
        account = new AccountAggregate(null);
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            Command cmd = new CloseAccountCmd(account.id());
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Depending on implementation, could be IllegalStateException, IllegalArgumentException, etc.
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException || thrownException instanceof NullPointerException);
    }

    // Helper Events for State Setup (Stubs for testing)
    record AccountOpenedEvent(String aggregateId, String status, BigDecimal balance, String type, java.time.Instant occurredAt) implements DomainEvent {
        @Override public String type() { return "account.opened"; }
        @Override public String aggregateId() { return aggregateId; }
        @Override public java.time.Instant occurredAt() { return occurredAt; }
    }
}