package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private record TestContext(
            TellerSessionAggregate aggregate,
            List<DomainEvent> resultEvents,
            Exception caughtException
    ) {}

    private TestContext context;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "session-123";
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        
        // Simulate a previous valid state: Authenticated, Active, Last Activity now
        agg.markAuthenticated("teller-001"); 
        agg.markActive();
        agg.updateLastActivity(Instant.now());
        
        this.context = new TestContext(agg, null, null);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is implied by the aggregate ID in this context
        assertNotNull(context.aggregate().id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(context.aggregate().id());
            List<DomainEvent> events = context.aggregate().execute(cmd);
            this.context = new TestContext(context.aggregate(), events, null);
        } catch (Exception e) {
            this.context = new TestContext(context.aggregate(), context.resultEvents(), e);
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(context.caughtException(), "Should not have thrown exception");
        assertNotNull(context.resultEvents(), "Events list should not be null");
        assertEquals(1, context.resultEvents().size(), "Should emit exactly one event");
        assertTrue(context.resultEvents().get(0) instanceof SessionEndedEvent, "Event type mismatch");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = "session-no-auth";
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        // Intentionally NOT calling markAuthenticated(). Defaults to unauthenticated.
        this.context = new TestContext(agg, null, null);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = "session-timeout";
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        agg.markAuthenticated("teller-001");
        agg.markActive();
        // Simulate old activity (older than timeout)
        agg.updateLastActivity(Instant.now().minusSeconds(3600)); // 1 hour ago
        this.context = new TestContext(agg, null, null);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        String id = "session-bad-nav";
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        agg.markAuthenticated("teller-001");
        agg.markActive();
        agg.updateLastActivity(Instant.now());
        // Force invalid state via reflection or direct mutation if exposed, 
        // for this test we assume a helper exists or the state was corrupted
        agg.corruptNavigationStateForTest();
        this.context = new TestContext(agg, null, null);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(context.caughtException(), "Exception should have been thrown");
        // Verify it's a domain rule violation (IllegalStateException or similar)
        assertTrue(context.caughtException() instanceof IllegalStateException || 
                   context.caughtException() instanceof IllegalArgumentException);
    }
}
