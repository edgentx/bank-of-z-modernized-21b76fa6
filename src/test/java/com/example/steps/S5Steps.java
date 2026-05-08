package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private OpenAccountCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Using a fixed ID for testing determinism, though in prod it might be generated
        aggregate = new AccountAggregate("acct-123");
    }

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Stored implicitly when building the command in the 'When' step or here
    }

    @And("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Stored implicitly
    }

    @And("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Stored implicitly
    }

    @And("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Stored implicitly
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        // Default valid values for happy path
        if (cmd == null) {
            cmd = new OpenAccountCmd("acct-123", "customer-1", "CURRENT", new BigDecimal("500"), "10-20-30");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.opened", resultEvents.get(0).type());
        assertEquals("ACTIVE", aggregate.getStatus());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("acct-low");
        // Setting up a command that will violate the rule
        // CURRENT account requires min 100. Let's try 50.
        cmd = new OpenAccountCmd("acct-low", "customer-1", "CURRENT", new BigDecimal("50"), "10-20-30");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // This rule for "Opening" implies you can't open an account that is already ACTIVE.
        aggregate = new AccountAggregate("acct-active");
        aggregate.activate(); // Manually force it to active to simulate opening an existing account
        cmd = new OpenAccountCmd("acct-active", "customer-1", "CURRENT", new BigDecimal("500"), "10-20-30");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("acct-immutable");
        // Manually mark immutable to simulate the state where the number is set
        aggregate.markAccountNumberImmutable();
        cmd = new OpenAccountCmd("acct-immutable", "customer-1", "CURRENT", new BigDecimal("500"), "10-20-30");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // In this implementation, we use IllegalStateException to transport domain errors
        assertTrue(thrownException instanceof IllegalStateException);
    }
}