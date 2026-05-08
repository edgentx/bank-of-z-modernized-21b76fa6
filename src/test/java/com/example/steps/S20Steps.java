package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "TS-123";
        String tellerId = "T-99";
        Instant now = Instant.now();
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate creation/start of session for valid context
        // Note: In a real scenario, this would be applying a SessionStartedEvent
        aggregate.markStarted(tellerId, now);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate instantiation in previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "TS-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session that failed authentication or is in a pre-auth state
        // attempting to close an unauthenticated session might be allowed or denied depending on policy.
        // However, based on the prompt "EndSessionCmd rejected...", we treat this state as invalid for ending.
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "TS-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        Instant past = Instant.now().minus(Duration.ofMinutes(30)); // > 15 min timeout
        aggregate.markStarted("T-99", past);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "TS-NAV-ERR";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markStarted("T-99", Instant.now());
        aggregate.markNavigationInvalid();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect IllegalStateException for domain rule violations in this pattern
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof UnknownCommandException);
    }
}
