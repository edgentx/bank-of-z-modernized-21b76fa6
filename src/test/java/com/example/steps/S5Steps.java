package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate accountAggregate;
    private OpenAccountCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Create a fresh aggregate in NONE state ready to be opened
        this.accountAggregate = new AccountAggregate("test-account-id");
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Data setup handled in When step or context
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Data setup handled in When step
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Data setup handled in When step
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Data setup handled in When step
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        try {
            // Default valid data for positive scenario
            if (command == null) {
                command = new OpenAccountCmd(
                        "customer-123",
                        "STANDARD",
                        new BigDecimal("500.00"),
                        "10-20-30",
                        "GB123XYZ"
                );
            }
            resultingEvents = accountAggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof AccountOpenedEvent);
        assertEquals("account.opened", resultingEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        this.accountAggregate = new AccountAggregate("test-balance-violate");
        this.command = new OpenAccountCmd(
                "customer-123",
                "PREMIER", // Requires high balance
                new BigDecimal("10.00"), // Too low
                "10-20-30",
                "GB999"
        );
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        // In this codebase, openAccount throws if not in NONE state.
        // We simulate the constraint by opening it once, then trying to open it again (or any mutating operation).
        // The prompt asks to test OpenAccountCmd specifically.
        this.accountAggregate = new AccountAggregate("test-status-violate");
        
        // Open the account first, setting state to ACTIVE
        OpenAccountCmd firstCmd = new OpenAccountCmd("c1", "STANDARD", BigDecimal.TEN, "s1", "n1");
        accountAggregate.execute(firstCmd);
        
        // Prepare the second command which should fail
        this.command = new OpenAccountCmd("c2", "STANDARD", BigDecimal.TEN, "s2", "n2");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        // Simulating a duplicate account number violation if the command contained a duplicate
        // Or passing an invalid number format
        this.accountAggregate = new AccountAggregate("test-immutable-violate");
        
        // Passing null or blank account number triggers the validation check
        this.command = new OpenAccountCmd(
                "customer-123",
                "STANDARD",
                new BigDecimal("100.00"),
                "10-20-30",
                "" // Invalid/Empty
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // Verify it's either IllegalArgumentException or IllegalStateException (Domain Errors)
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
