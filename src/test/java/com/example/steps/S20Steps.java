package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate session;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        session = new TellerSessionAggregate(id);
        // Bootstrap the session to an active state (simulating StartSessionCmd)
        var startCmd = new StartSessionCmd(id, "teller_001", "TERMINAL_01", Instant.now());
        session.execute(startCmd);
        session.clearEvents();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate ID initialization
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // To violate 'authenticated to initiate', the session is in a state where no valid auth occurred.
        // We create a session but do not execute StartSessionCmd (which establishes auth).
        String id = UUID.randomUUID().toString();
        session = new TellerSessionAggregate(id);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        session = new TellerSessionAggregate(id);
        // Start session in the distant past to ensure timeout check fails
        Instant past = Instant.now().minus(Duration.ofHours(2));
        var startCmd = new StartSessionCmd(id, "teller_001", "TERMINAL_01", past);
        session.execute(startCmd);
        session.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String id = UUID.randomUUID().toString();
        session = new TellerSessionAggregate(id);
        // Start session normally
        var startCmd = new StartSessionCmd(id, "teller_001", "TERMINAL_01", Instant.now());
        session.execute(startCmd);
        session.clearEvents();
        // Force a state inconsistency: set session to ended locally without event (simulating external corruption/messaging failure)
        // This makes the 'ended' check fail or the state invalid for ending again
        try {
            // In a real test, we might rely on a specific 'CorruptState' command or reflection.
            // Here we simulate by invoking EndSessionCmd logic logic or simply double-ending if allowed by aggregate internal state.
            // To strictly satisfy "Navigation state... reflect operational context":
            // If we interpret 'ended' as a state, and we try to end again, it might be rejected if the state is already ended.
            // Let's assume the aggregate protects against ending an already ended session.
            session.execute(new EndSessionCmd(id));
            session.clearEvents();
        } catch (Exception e) {
            // If the first end fails, the scenario setup is tricky. 
            // Let's assume the aggregate has a specific flag for 'Navigation State Mismatch'.
            // For this test, we will assume the aggregate simply disallows ending an already ended session.
        }
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(session.id());
            session.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        var events = session.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        Assertions.assertTrue(events.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error (exception), but none was thrown");
    }
}
