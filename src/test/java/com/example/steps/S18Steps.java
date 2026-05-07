package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "sess-123";
    private String tellerId;
    private String terminalId;
    private boolean isAuthenticated;
    private String navigationState;
    private Instant lastHeartbeat;
    
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = true;
        this.tellerId = "teller-001";
        this.terminalId = "term-A";
        this.navigationState = "HOME";
        this.lastHeartbeat = Instant.now();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = false; // Violation
        this.tellerId = "teller-001";
        this.terminalId = "term-A";
        this.navigationState = "HOME";
        this.lastHeartbeat = Instant.now();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = true;
        this.tellerId = "teller-001";
        this.terminalId = "term-A";
        this.navigationState = "HOME";
        // Simulate stale heartbeat
        this.lastHeartbeat = Instant.now().minusSeconds(3600); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.isAuthenticated = true;
        this.tellerId = "teller-001";
        this.terminalId = "term-A";
        this.navigationState = null; // Violating valid navigation state context
        this.lastHeartbeat = Instant.now();
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in Given setup
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in Given setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, navigationState, lastHeartbeat);
        try {
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
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
