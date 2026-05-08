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

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Seed with session started event to pass basic validation
        aggregate.apply(new SessionStartedEvent(sessionId, "teller-1", Instant.now()));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT seed with SessionStartedEvent, implying no auth/session context
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Seed with an old timestamp (simulating timeout)
        Instant past = Instant.now().minus(Duration.ofMinutes(31));
        aggregate.apply(new SessionStartedEvent(sessionId, "teller-1", past));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_state() {
        // This represents an invalid context for the requested navigation
        // e.g. trying to navigate to a specific screen without prerequisites
        // modeled here by invalid input data for the specific command context.
        // We will enforce this via input validation in the command execution.
        String sessionId = "session-bad-state";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.apply(new SessionStartedEvent(sessionId, "teller-1", Instant.now()));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate setup
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the 'When' step command construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the 'When' step command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            String sessionId = aggregate.id();
            // Determine inputs based on the scenario context implicitly or explicitly
            // For simplicity, using valid defaults, relying on aggregate state to trigger rejections
            String menuId = "MAIN_MENU";
            String action = "ENTER";
            
            // If testing the bad state scenario, we might pass invalid context data
            if (sessionId.equals("session-bad-state")) {
                 // Simulate an invalid context request (e.g. target menu requires auth)
                 menuId = "SECURE_MENU";
            }

            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException or IllegalArgumentException based on implementation
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
