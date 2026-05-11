package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.aggregate = new AccountAggregate("acc-123");
        this.caughtException = null;
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Context setup, usually handled in command construction
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Context setup
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Context setup
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Context setup
    }

    // Negative Scenario Givens
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        // Simulate a negative deposit command for the scenario
        this.aggregate = new AccountAggregate("acc-fail-1");
        this.command = new OpenAccountCmd("acc-fail-1", "cust-1", "SAVINGS", new BigDecimal("-100"), "00-00-00");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        // To simulate this violation for 'Open', we try to open an already active account.
        // First, open it manually to set state to ACTIVE
        this.aggregate = new AccountAggregate("acc-fail-2");
        // Manually setting state or executing a valid open command first
        this.aggregate.execute(new OpenAccountCmd("acc-fail-2", "cust-2", "CHECKING", BigDecimal.ZERO, "00-00-00"));
        // Now create the command that will fail (trying to open again)
        this.command = new OpenAccountCmd("acc-fail-2", "cust-2", "CHECKING", BigDecimal.TEN, "00-00-00");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_number_immutability() {
        // Similar to status violation, reusing an ID violates uniqueness.
        this.aggregate = new AccountAggregate("acc-fail-3");
        this.aggregate.execute(new OpenAccountCmd("acc-fail-3", "cust-3", "SAVINGS", BigDecimal.ZERO, "00-00-00"));
        this.command = new OpenAccountCmd("acc-fail-3", "cust-diff", "SAVINGS", BigDecimal.ZERO, "00-00-00");
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        if (this.command == null) {
            // Build valid command if not set by negative Given
            this.command = new OpenAccountCmd("acc-123", "cust-456", "SAVINGS", new BigDecimal("100.00"), "10-20-30");
        }
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("account.opened", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Checking if it's a RuntimeException (IllegalStateException/IllegalArgumentException)
        assertTrue(caughtException instanceof RuntimeException);
    }
}
