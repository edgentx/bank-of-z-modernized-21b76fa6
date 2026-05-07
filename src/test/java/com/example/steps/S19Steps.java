package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermenu.model.NavigateMenuCmd;
import com.example.domain.tellermenu.model.TellerSessionAggregate;
import com.example.domain.tellermenu.model.MenuNavigatedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate initialization to valid state (authenticated, active)
        aggregate.initialize("teller-456", Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled implicitly by the aggregate creation in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When step construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Do NOT initialize (not authenticated)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Initialize with a timestamp that is definitely expired
        aggregate.initialize("teller-timeout", Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.initialize("teller-nav", Instant.now());
        // Manually corrupt internal state for testing purposes
        // In a real scenario, this might be set via a command sequence that results in an invalid state,
        // or the command might attempt to navigate to a screen that is unreachable from the current one.
        // For this unit test, we assume the aggregate exposes a way to mark itself contextually invalid
        // or we pass a command that is contextually invalid.
        aggregate.markContextInconsistent();
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Construct a valid command structure
            // Note: The "violation" for state context is handled by the internal state of the aggregate,
            // not necessarily the command parameters themselves, though the command could be bad too.
            NavigateMenuCmd cmd = new NavigateMenuCmd("MAIN_MENU_01", "SELECT");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(MenuNavigatedEvent.class, resultEvents.get(0).getClass());
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        assertEquals(0, aggregate.uncommittedEvents().size());
    }
}
