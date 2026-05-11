package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S5Steps {

    private AccountAggregate account;
    private Throwable thrownException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("acct-123");
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // State stored in context via 'account' handle, fields applied in execution
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // State stored in context via 'account' handle, fields applied in execution
    }

    @Given("a valid initialDeposit is provided")
    public void a valid_initial_deposit_is_provided() {
        // State stored in context via 'account' handle, fields applied in execution
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // State stored in context via 'account' handle, fields applied in execution
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        try {
            // Using valid data for the happy path
            OpenAccountCmd cmd = new OpenAccountCmd(
                "acct-123",
                "customer-001",
                "CHECKING",
                new BigDecimal("100.00"),
                "123456"
            );
            resultingEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof AccountOpenedEvent);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        account = new AccountAggregate("acct-fail-balance");
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed_failure_path() {
        try {
            // Scenario Logic: Initial deposit is below minimum
            OpenAccountCmd cmd = new OpenAccountCmd(
                "acct-fail-balance",
                "customer-001",
                "PREMIUM", // Assume PREMIER requires 500
                new BigDecimal("10.00"), // Violation
                "123456"
            );
            resultingEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Verify it's a domain validation error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        account = new AccountAggregate("acct-status-violation");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // This invariant is usually enforced by the repository or ID generation strategy.
        // In the aggregate, we simulate a command trying to 're-open' or overwrite.
        account = new AccountAggregate("acct-duplicate");
        // We simulate the aggregate being in a state where the ID is already taken/active
        OpenAccountCmd cmd = new OpenAccountCmd("acct-duplicate", "c1", "SAVINGS", new BigDecimal("100"), "000000");
        account.execute(cmd); // Open it once
        // Now the state is OPEN. Attempting to Open again (or if logic checked uniqueness) would fail.
    }
}
