package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "TS-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a live session: Authenticated, Active, Last Activity recently
        this.aggregate.hydrate("Teller-01", Instant.now(), Instant.now().minusSeconds(60), true, "HOME_SCREEN");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        this.sessionId = "TS-UNAUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Not authenticated
        this.aggregate.hydrate(null, Instant.now(), Instant.now().minusSeconds(60), true, "HOME_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "TS-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Violation: Last Activity was 2 hours ago (config is usually 15-30 mins)
        Instant oldActivity = Instant.now().minus(Duration.ofHours(2));
        this.aggregate.hydrate("Teller-01", Instant.now(), oldActivity, true, "HOME_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        this.sessionId = "TS-NAVERR";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Violation: State is inconsistent (Active but navigating away without clear transition)
        // In this specific domain logic, we simulate a state where termination is blocked due to dirty state
        this.aggregate.hydrate("Teller-01", Instant.now(), Instant.now().minusSeconds(60), true, "PENDING_TRANSACTION");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionEndedEvent.TYPE, resultEvents.get(0).type());
        assertEquals(sessionId, resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception but command succeeded");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
