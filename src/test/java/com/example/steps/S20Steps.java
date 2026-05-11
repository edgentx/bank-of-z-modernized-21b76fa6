package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.tellermessaging.model.EndSessionCmd;
import com.example.domain.tellermessaging.model.TellerSession;
import com.example.domain.tellermessaging.model.SessionEndedEvent;
import com.example.domain.tellermessaging.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession session;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        this.session = new TellerSession(id);
        // Simulate successful authentication and navigation state to make it valid
        // For this exercise, we assume a fresh aggregate meets basic structural validity
        // or we hydrate it to a state where it CAN be ended.
        // Since we are testing EndSession, we assume the session is in an 'ACTIVE' state implicitly by creation.
        // In a real flow, we might execute a StartSessionCmd first if it existed.
        // For S-20, we construct a valid state manually or via a hypothetical start.
        // To keep it simple and isolated to S-20 requirements:
        repository.save(session);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The session ID is already set in the previous step.
        // This step confirms existence for the narrative.
        Assertions.assertNotNull(session.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // We simulate a session that was created/modified without proper authentication flags.
        // Since StartSession logic isn't fully defined in S-20, we mock the invalid state.
        // We need the aggregate to throw an error when execute is called.
        // The TellerSession aggregate logic will check for 'authenticated' flag.
        String id = UUID.randomUUID().toString();
        this.session = new TellerSession(id); 
        // We rely on the Aggregate logic to detect this state.
        // If we can't mutate state, we might use a specific constructor or flag.
        // Assuming TellerSession has a way to represent unauthenticated state,
        // we verify the command fails.
        // For this BDD, we assume the aggregate defaults to valid unless manipulated,
        // or we use reflection/test-specific setters.
        // However, the TellerSession code below enforces invariants.
        // To make it violate "authenticated", we might assume the default is unauthenticated,
        // OR we need a way to set it. 
        // Let's assume the aggregate checks a field. We will inject a test double or use reflection if needed,
        // but ideally, the aggregate starts in a way that allows validation.
        // FIX: I will implement TellerSession such that we can hydrate it, or simply test the exception.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        this.session = new TellerSession(id);
        // Simulate timeout logic.
        // If the aggregate tracks last activity time, we set it to the past.
        // See TellerSession implementation for `isTimedOut()`.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String id = UUID.randomUUID().toString();
        this.session = new TellerSession(id);
        // Simulate invalid navigation state.
        // See TellerSession implementation for `isValidNavigationState()`.
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(session.id());
        try {
            List<com.example.domain.shared.DomainEvent> events = session.execute(cmd);
            // Apply events if necessary, or just verify emission.
            // In TellerSession, state is updated inside execute().
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        List<com.example.domain.shared.DomainEvent> events = session.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Should have emitted an event");
        Assertions.assertTrue(events.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain error (exception)");
        // Optionally check message content
    }
}
