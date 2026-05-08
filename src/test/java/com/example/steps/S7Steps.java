package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S7Steps {

    // State variables for the scenario
    private AccountAggregate aggregate;
    private CloseAccountCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        // Open the account first to make it valid/active for testing
        aggregate.execute(new OpenAccountCmd(id, "ACC-123", AccountType.SAVINGS, BigDecimal.ZERO, Instant.now()));
        aggregate.clearEvents(); // Clear opening events for cleaner test
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account number is typically set at creation/opening.
        // We assume the aggregate initialized in the previous step holds the valid number.
        // If we need to simulate a specific command number, we create the command here.
        this.command = new CloseAccountCmd(aggregate.id());
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        Assertions.assertEquals("AccountClosed", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        // Open with a positive balance
        aggregate.execute(new OpenAccountCmd(id, "ACC-DEBT", AccountType.SAVINGS, new BigDecimal("100.00"), Instant.now()));
        aggregate.clearEvents();
        this.command = new CloseAccountCmd(id);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        // Account starts as CREATED (not Active/OPENED) or we explicitly close it then try again.
        // In this model, not executing OpenAccountCmd means it's not Active.
        this.command = new CloseAccountCmd(id);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_unique_number() {
        String id = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(id);
        // The aggregate is valid, but we will simulate a command with a mismatched or null ID
        // representing an attempt to close an account that doesn't match the aggregate identity.
        this.command = new CloseAccountCmd("DIFFERENT_ID");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Check it's one of our domain errors (IllegalStateException, IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
