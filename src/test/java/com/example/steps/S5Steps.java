package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryAccountRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private OpenAccountCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("acc-123");
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // customerId is typically part of the command construction in When step
        // Placeholder to indicate context setup
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Placeholder
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Placeholder
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Placeholder
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        // Default valid values for success scenario
        command = new OpenAccountCmd(
            "acc-123",
            "cust-001",
            "CURRENT",
            new BigDecimal("600.00"),
            "10-20-30"
        );
        executeCommand();
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) resultingEvents.get(0);
        assertEquals("account.opened", event.type());
        assertEquals("acc-123", event.aggregateId());
        assertEquals("cust-001", event.customerId());
        assertEquals("CURRENT", event.accountType());
        assertEquals(new BigDecimal("600.00"), event.initialDeposit());
        assertEquals("10-20-30", event.sortCode());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance_requirement() {
        aggregate = new AccountAggregate("acc-low-balance");
    }

    @When("the OpenAccountCmd command is executed with low deposit")
    public void the_command_is_executed_with_low_deposit() {
        // Current Account min is 500.00 in aggregate logic
        command = new OpenAccountCmd(
            "acc-low-balance",
            "cust-002",
            "CURRENT",
            new BigDecimal("50.00"), // Too low
            "10-20-30"
        );
        executeCommand();
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // Create an account and manually set it to ACTIVE to simulate the violation condition
        aggregate = new AccountAggregate("acc-already-active");
        // We use reflection or mutation logic (if available) to bypass command execution for setup,
        // or we simply test the logic inside execute.
        // Since the aggregate is created PENDING, we assume we need to force it ACTIVE to test the invariant.
        // However, execute logic is what we are testing. 
        // To effectively test the scenario "Account must be Active to process X", we normally need a command like "Withdraw".
        // Since the prompt asks for OpenAccountCmd to be rejected by this invariant, we simulate a 'zombie' account.
        // We will call execute twice. The first time succeeds, the second time should fail.
        
        // First open: Success
        OpenAccountCmd firstCmd = new OpenAccountCmd("acc-already-active", "cust-003", "CURRENT", new BigDecimal("600"), "10-20-30");
        aggregate.execute(firstCmd); 
        // Aggregate is now ACTIVE internally.
    }

    @When("the OpenAccountCmd command is executed on active account")
    public void the_command_is_executed_on_active_account() {
        command = new OpenAccountCmd("acc-already-active", "cust-003", "CURRENT", new BigDecimal("600"), "10-20-30");
        executeCommand();
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_unique_account_number() {
        // Similar to above, open once, try to open again. The aggregate already has a number.
        aggregate = new AccountAggregate("acc-has-number");
        OpenAccountCmd firstCmd = new OpenAccountCmd("acc-has-number", "cust-004", "CURRENT", new BigDecimal("600"), "10-20-30");
        aggregate.execute(firstCmd);
    }

    @When("the OpenAccountCmd command is executed again")
    public void the_open_account_cmd_command_is_executed_again() {
        command = new OpenAccountCmd("acc-has-number", "cust-004", "SAVINGS", new BigDecimal("100"), "10-20-30");
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Usually an IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    private void executeCommand() {
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}