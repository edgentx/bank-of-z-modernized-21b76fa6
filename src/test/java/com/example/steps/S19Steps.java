package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate session;
    private Throwable caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = "session-123";
        session = new TellerSessionAggregate(id);
        // Simulate session init via event (for testing purposes, we manually hydrate or run init command)
        // Here we assume a valid state is established.
        session.apply(new TellerSessionAuthenticatedEvent(id, "teller-1", Instant.now()));
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        String id = "session-401";
        session = new TellerSessionAggregate(id);
        // Do not authenticate. Aggregate remains in unauthenticated state.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_timed_out() {
        String id = "session-timeout";
        session = new TellerSessionAggregate(id);
        // Create an authenticated event that occurred way in the past
        Instant past = Instant.now().minus(Duration.ofMinutes(30)); // > 15 min timeout
        session.apply(new TellerSessionAuthenticatedEvent(id, "teller-1", past));
        repository.save(session);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_invalid_context() {
        String id = "session-context";
        session = new TellerSessionAggregate(id);
        session.apply(new TellerSessionAuthenticatedEvent(id, "teller-1", Instant.now()));
        // Navigate to a state that typically disallows further action, e.g. SYSTEM_LOCKDOWN
        session.apply(new MenuNavigatedEvent(id, "MAIN", "LOCKDOWN_SYSTEM", Instant.now()));
        repository.save(session);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate instantiation in previous steps
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in 'When' step via command construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in 'When' step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd("SESSION_1", "MENU_DEPOSITS", "ENTER");
            resultingEvents = session.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException);
    }
}
