package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a valid, active session
        String sessionId = "TS-123";
        String tellerId = "T-01";
        // Simulating a previous start session event to set state
        // In a real repository, we'd hydrate from events, but for unit steps we construct state.
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrating state directly for test setup
        // Using a test-visible or reflection method to set internal state would be ideal, 
        // but we'll assume a helper or constructor that sets up an 'ACTIVE' session for the happy path.
        // For this aggregate, we'll assume a constructor or factory that creates an active session.
        // Or we can simply assume the aggregate is created and we invoke a Start command first (not part of S-20).
        // To keep S-20 isolated, we will use a specific test constructor or package-private setup if available.
        // Let's assume a test constructor that simulates a hydrated active session.
        
        // Simulating hydration:
        this.aggregate.markAsHydrated(tellerId, Instant.now().minusSeconds(60), 
                                      TellerSession.SessionState.ACTIVE, "MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The aggregate ID is already set to "TS-123"
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "TS-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session where authentication failed or is null
        this.aggregate.markAsHydrated(null, Instant.now().minusSeconds(10), 
                                      TellerSession.SessionState.ACTIVE, "LOGIN_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session last active a very long time ago (e.g., 31 minutes)
        Instant oldTimestamp = Instant.now().minus(Duration.ofMinutes(31));
        this.aggregate.markAsHydrated("T-01", oldTimestamp, 
                                      TellerSession.SessionState.ACTIVE, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "TS-NAV-ERR";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session in an invalid/unknown state (e.g., null)
        this.aggregate.markAsHydrated("T-01", Instant.now().minusSeconds(10), 
                                      null, "UNKNOWN");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
        Assertions.assertEquals("session.ended", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Checking for standard Java exceptions or a specific DomainException if defined.
        // The prompt asks for a domain error, often an IllegalStateException or IllegalArgumentException.
        Assertions.assertTrue(caughtException instanceof IllegalStateException || 
                              caughtException instanceof IllegalArgumentException);
    }
}
