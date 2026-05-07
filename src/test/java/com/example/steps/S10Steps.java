package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Suite wrapper for Cucumber features.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "glue", value = "com.example.steps")
public class S10Steps {

    // State container for the scenario
    static class TestState {
        Transaction aggregate;
        PostDepositCommand command;
        DomainError thrownError;
        List<DomainEvent> resultingEvents;
    }

    private final TestState state = new TestState();

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        state.aggregate = new Transaction();
        state.aggregate.setId("txn-123");
        state.aggregate.setAccountNumber("ACC-001");
        state.aggregate.setPosted(false); // Not yet posted
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        if (state.command == null) state.command = new PostDepositCommand();
        state.command.setAccountNumber("ACC-001");
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (state.command == null) state.command = new PostDepositCommand();
        state.command.setAmount(new BigDecimal("100.00"));
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (state.command == null) state.command = new PostDepositCommand();
        state.command.setCurrency(Currency.getInstance("USD"));
    }

    // --- Violation Scenarios (Given) ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        a_valid_Transaction_aggregate();
        if (state.command == null) state.command = new PostDepositCommand();
        state.command.setAmount(BigDecimal.ZERO);
        a_valid_accountNumber_is_provided();
        a_valid_currency_is_provided();
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_cannot_be_altered_once_posted() {
        a_valid_Transaction_aggregate();
        state.aggregate.setPosted(true); // Already posted
        a_valid_accountNumber_is_provided();
        a_valid_amount_is_provided();
        a_valid_currency_is_provided();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_must_result_in_valid_balance() {
        a_valid_Transaction_aggregate();
        // Simulate a validation check that would fail if logic was implemented
        // For this test, we'll pass a flag or check within the command/aggregate
        if (state.command == null) state.command = new PostDepositCommand();
        state.command.setAmount(new BigDecimal("-50.00")); // Logic might check this or balance limits
        a_valid_accountNumber_is_provided();
        a_valid_currency_is_provided();
    }

    // --- Actions (When) ---

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Execute the command pattern on the aggregate
            state.resultingEvents = state.aggregate.execute(state.command);
        } catch (DomainError e) {
            state.thrownError = e;
        }
    }

    // --- Outcomes (Then) ---

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(state.resultingEvents, "Events list should not be null");
        assertFalse(state.resultingEvents.isEmpty(), "At least one event should be emitted");
        assertTrue(state.resultingEvents.get(0) instanceof DepositPostedEvent, "First event should be DepositPostedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(state.thrownError, "Expected a DomainError to be thrown");
        assertNull(state.resultingEvents || state.resultingEvents.isEmpty(), "No events should be emitted on failure");
    }
}
