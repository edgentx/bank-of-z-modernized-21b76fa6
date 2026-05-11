package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "TELLER-42";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "SESSION-FAIL-AUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "TELLER-UNAUTH";
        this.terminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "SESSION-FAIL-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "TELLER-42";
        this.terminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        this.sessionId = "SESSION-FAIL-NAV";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "TELLER-42";
        this.terminalId = "TERM-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Determine context based on previous Givens (simple logic for test)
            boolean isAuthenticated = !sessionId.contains("FAIL-AUTH");
            int timeout = sessionId.contains("FAIL-TIMEOUT") ? 0 : 900; // 0 is invalid
            String navContext = sessionId.contains("FAIL-NAV") ? null : "HOME";

            Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, timeout, navContext);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
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
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect either IllegalStateException or IllegalArgumentException depending on the invariant
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}