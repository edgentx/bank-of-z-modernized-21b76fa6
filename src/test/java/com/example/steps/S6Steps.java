package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<DomainEvent> events;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // Setup an aggregate with valid defaults
        account = new AccountAggregate("ACC-123");
        // Hydrate state (simulate reconstruction from events)
        account.hydrate(
            "ACC-123",
            AccountStatus.ACTIVE,
            new BigDecimal("1000.00"),
            "Checking",
            0
        );
        Assertions.assertNotNull(account);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Implicit in the aggregate ID used in 'a_valid_Account_aggregate'
        Assertions.assertEquals("ACC-123", account.id());
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // Implicit in the command construction in the 'When' step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            // Default: Active -> Frozen
            Command cmd = new UpdateAccountStatusCmd("ACC-123", AccountStatus.FROZEN);
            events = account.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("account.status.updated", events.get(0).type());
        Assertions.assertEquals("ACC-123", events.get(0).aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        // Create an account with low balance (e.g., $50) and type "Savings" (assume min $100)
        account = new AccountAggregate("ACC-LOW");
        account.hydrate(
            "ACC-LOW",
            AccountStatus.ACTIVE,
            new BigDecimal("50.00"),
            "Savings", // Assumed min balance > 50
            0
        );
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        // Create an account that is FROZEN
        account = new AccountAggregate("ACC-FRZ");
        account.hydrate(
            "ACC-FRZ",
            AccountStatus.FROZEN,
            new BigDecimal("1000.00"),
            "Checking",
            0
        );
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_unique_immutable_number() {
        // Create an aggregate, but pass a mismatching AccountNumber in the Command
        account = new AccountAggregate("ACC-ORIG");
        account.hydrate(
            "ACC-ORIG",
            AccountStatus.ACTIVE,
            new BigDecimal("500.00"),
            "Checking",
            0
        );
    }

    // Override When for the Immutable test case specifically
    @When("the UpdateAccountStatusCmd command is executed with mismatched ID")
    public void the_UpdateAccountStatusCmd_command_is_executed_with_mismatch() {
        try {
            // Try to update ACC-ORIG, but command says ACC-OTHER
            Command cmd = new UpdateAccountStatusCmd("ACC-OTHER", AccountStatus.CLOSED);
            events = account.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected domain error (exception), but none was thrown");
        // Verify specific message content based on the scenario
        // Note: In a real framework, we might catch specific Domain Exceptions.
        // Here we verify state didn't change (no events)
        Assertions.assertTrue(events == null || events.isEmpty());
    }

}
