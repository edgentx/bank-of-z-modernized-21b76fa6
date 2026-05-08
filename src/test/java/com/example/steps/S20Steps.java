package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
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
    private Exception capturedException;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticatedTeller("teller-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate construction in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("sess-invalid-auth");
        aggregate.setUnauthenticated(); // No teller ID set
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("sess-timeout");
        aggregate.setAuthenticatedTeller("teller-01");
        // Set last activity to 20 minutes ago (Assume configured timeout is 15m)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("sess-bad-nav");
        aggregate.setAuthenticatedTeller("teller-01");
        // Corrupt the navigation state context to null to simulate violation
        aggregate.setCurrentContext(null);
    }

    // --- When Steps ---

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
            // Save to repo to emulate persistence flow
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Then Steps ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);

        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We verify it is an IllegalStateException, which represents our domain invariant violation here
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // --- Mock Repository for Testing ---

    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            // No-op for in-memory test
            return aggregate;
        }

        @Override
        public TellerSessionAggregate findById(String id) {
            return null;
        }
    }
}
