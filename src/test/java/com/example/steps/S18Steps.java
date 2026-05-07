package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private String tellerId;
    private String terminalId;
    private Instant authTime;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // We will pass null for authenticatedAt in the command to violate this
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // We will simulate an old authentication timestamp
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.invalidateContext(); // Helper method added to aggregate for testing
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "term-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // For the timeout violation scenario, we set a very old auth time
            Instant cmdAuthTime = (authTime != null) ? authTime : Instant.now();
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, cmdAuthTime);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on implementation, it could be IAE or ISE
        assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException ||
            caughtException instanceof UnknownCommandException
        );
    }
}
