package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String aggregateId = "teller-123";
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    // Command state builders
    private boolean isAuthenticated = true;
    private int timeoutInSeconds = 900;
    private String navigationContext = "MAIN_MENU";
    private String sessionId = "session-abc";
    private String tellerId = "teller-01";
    private String terminalId = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(aggregateId);
        isAuthenticated = true;
        timeoutInSeconds = 900;
        navigationContext = "MAIN_MENU";
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        tellerId = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        terminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate(aggregateId);
        isAuthenticated = false; // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate(aggregateId);
        timeoutInSeconds = -1; // Violation: 0 or negative
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate(aggregateId);
        navigationContext = ""; // Violation: blank
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, timeoutInSeconds, navigationContext);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregateId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertTrue(event.occurredAt().isBefore(Instant.now()) || event.occurredAt().equals(Instant.now()));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on exact implementation, could be IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
