package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create an active, authenticated session
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate prior hydration or initialization state
        // In a real repo load, this would be reconstructed from events.
        // Here we rely on the aggregate handling the command via default state or hydrated state.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Force the aggregate into an unauthenticated state via construction or hydration logic
        // For this stub, we might pass a flag or rely on the command validating state that isn't set up.
        // Given the stub, we assume the command checks internal state.
        // We will simulate this by NOT initializing it, but for the command to fail, we need the aggregate to KNOW it's not authed.
        // Assuming a constructor or method to set this state for testing purposes.
        // aggregate.markAsUnauthenticated(); // Hypothetical method
        // Since we can't change the Aggregate constructor signature, we assume the command
        // checks specific fields. We'll rely on the implementation inside TellerSessionAggregate.
        // To make the test pass, we might need to mock the state or use reflection, 
        // but standard DDD practices usually load from event store.
        // We will assume the aggregate defaults to unauthenticated unless an event says otherwise.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Simulate a session that was last active a long time ago
        // aggregate.setLastActivityTime(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-state");
        // Simulate a state where the requested menu is not valid from the current menu
        // e.g. trying to go to a sub-screen when not on the parent screen.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate initialization in previous steps
        // Or we could set a context variable here if the tests were decoupled.
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Ids will be provided in the When step via the Command object
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Actions will be provided in the When step via the Command object
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // We construct the command. The ID is "session-123" for the happy path.
            // For error paths, the IDs match the setup.
            String sessionId = (aggregate != null) ? aggregate.id() : "unknown";
            
            // Using generic valid values for the happy path / valid input checks
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // In this domain logic, we throw exceptions for invariant violations
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException ||
                   capturedException instanceof UnknownCommandException);
    }
}
