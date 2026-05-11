package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        // Seed with a login event to satisfy "Authenticated" invariant
        TellerSessionLoggedInEvent loginEvent = new TellerSessionLoggedInEvent(
            sessionId, 
            "teller-001", 
            "MAIN_MENU", 
            Instant.now()
        );
        aggregate = new TellerSessionAggregate(sessionId, loginEvent);
        
        // Reset last active time to now to ensure it's not timed out
        // (Accessing private state via a test setter or reconstituting logic would be ideal, 
        // but for this structure we assume the constructor handles hydration or state initialization)
        // Since we don't have a public setter, we rely on the aggregate being initialized freshly.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate ID setup
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Implicitly handled by command parameters in 'When'
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Implicitly handled by command parameters in 'When'
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), "CASH_WITHDRAWAL", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull("Expected no exception", capturedException);
        assertNotNull("Expected events to be emitted", resultEvents);
        assertFalse("Expected list of events not to be empty", resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create an aggregate without a prior login event (or simulate logout)
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // state remains initialized=false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        TellerSessionLoggedInEvent loginEvent = new TellerSessionLoggedInEvent(
            sessionId, 
            "teller-001", 
            "MAIN_MENU", 
            Instant.now().minus(Duration.ofHours(2)) // Very old
        );
        aggregate = new TellerSessionAggregate(sessionId, loginEvent);
        // In a real system we'd mock the clock, but here the system time is used. 
        // We assume the invariants are checked against the event timestamp.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        // Context: If the current screen is CASH_INQUIRY, but the command tries to use a function only valid in MAIN_MENU
        String sessionId = "session-bad-state";
        TellerSessionLoggedInEvent loginEvent = new TellerSessionLoggedInEvent(
            sessionId, 
            "teller-001", 
            "CASH_INQUIRY", // Start somewhere specific
            Instant.now()
        );
        aggregate = new TellerSessionAggregate(sessionId, loginEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", capturedException);
        // Verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        assertTrue(
            "Expected IllegalStateException or IllegalArgumentException", 
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException
        );
    }
}
