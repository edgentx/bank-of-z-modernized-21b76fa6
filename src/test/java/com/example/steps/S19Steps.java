package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // Initialize state to valid (authenticated, active)
        aggregate.apply(new TellerSessionAuthenticatedEvent(id, "teller123", Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by aggregate initialization, valid UUID.
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // No-op, valid value will be used in When clause
    }

    @Given("a valid action is provided")
    public void a valid_action_is_provided() {
        // No-op, valid value will be used in When clause
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MainMenu", "Enter");
            this.resultEvents = aggregate.execute(cmd);
        } catch (DomainException | IllegalStateException | IllegalArgumentException e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // Intentionally do NOT authenticate. State is NONE.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // Authenticate with a timestamp that is too old (simulating timeout)
        Instant past = Instant.now().minus(Duration.ofMinutes(31));
        aggregate.apply(new TellerSessionAuthenticatedEvent(id, "teller123", past));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // Authenticate
        aggregate.apply(new TellerSessionAuthenticatedEvent(id, "teller123", Instant.now()));
        // Navigate to a transaction screen (e.g., Deposit)
        aggregate.execute(new NavigateMenuCmd(id, "DepositScreen", "Enter"));
        aggregate.apply(new MenuNavigatedEvent(id, "DepositScreen", "Enter", Instant.now()));
        // Clear the simulated context so the next navigation fails context check
        aggregate.clearContextForTest();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error, but none was thrown");
        // We accept IllegalStateException or IllegalArgumentException as domain errors in this implementation
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}
