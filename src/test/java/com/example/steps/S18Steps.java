package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uinav.model.SessionStartedEvent;
import com.example.domain.uinav.model.StartSessionCmd;
import com.example.domain.uinav.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Simulate a fresh aggregate loaded from repo
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.hydrate(StartSessionCmd.State.AUTHENTICATED); // Assume authenticated for basic validation
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Data setup handled in the When step or stored in context if needed
        // For this aggregate pattern, the command carries the ID.
    }

    @Given("a valid terminalId is provided")
    public void a valid_terminal_id_is_provided() {
        // Data setup handled in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "term-456");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-1", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        aggregate.hydrate(StartSessionCmd.State.UNAUTHENTICATED);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // Simulate that the last activity was way in the past
        aggregate.hydrate(StartSessionCmd.State.AUTHENTICATED);
        aggregate.setLastActivityTime(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.hydrate(StartSessionCmd.State.AUTHENTICATED);
        // Simulate an invalid context (e.g. drain mode or locked terminal)
        aggregate.setOperationalContext("INVALID_CONTEXT");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect an IllegalStateException, IllegalArgumentException, or a custom DomainException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

}
