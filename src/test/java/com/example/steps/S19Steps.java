package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // Simulate creation event/logic to make it valid
        // In a real scenario, we might hydrate it from an event stream.
        // For this test, we rely on the aggregate constructor initializing a valid state.
        // We assume a valid teller is authenticated implicitly if not specified.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate constructor in the Given step above
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When step by constructing the command
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step by constructing the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-1", "MAIN_MENU", "SELECT");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("menu.navigated", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        aggregate.markAsUnauthenticated(); // Setup method to simulate invariant violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.markAsTimedOut(); // Setup method to simulate invariant violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.markAsNavigationInvalid(); // Setup method to simulate invariant violation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected a domain error exception, but command succeeded.");
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided_ctx() {
        // Placeholder for context specific setup if needed
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided_ctx() {
        // Placeholder
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided_ctx() {
        // Placeholder
    }

}
