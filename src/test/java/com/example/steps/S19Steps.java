package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create an authenticated, active session
        aggregate = new TellerSessionAggregate("session-1");
        // Simulate authentication by setting internal state (bypassing cmd for setup)
        // In a real repo, we might load from events, but for unit steps, we assume hydrated state.
        aggregate.markAuthenticated("teller-123"); 
        aggregate.updateLastActivity(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Context placeholder - used in When
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context placeholder - used in When
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd("session-1", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(thrownException, "Should not throw exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // Scenario 2: Auth Rejection
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-2");
        // Do NOT authenticate. Teller ID is null/unset.
        aggregate.updateLastActivity(Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    // Scenario 3: Timeout Rejection
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.markAuthenticated("teller-123");
        // Simulate old activity time (e.g., 30 minutes ago)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(30)));
    }

    // Scenario 4: Context Rejection
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.markAuthenticated("teller-123");
        aggregate.updateLastActivity(Instant.now());
        // Simulate a state where the user is locked or in an invalid screen context
        aggregate.setCurrentContext("LOCKED_SCREEN");
    }
}
