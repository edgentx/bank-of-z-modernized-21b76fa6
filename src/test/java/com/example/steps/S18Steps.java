package com.example.steps;

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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentTellerId;
    private String currentTerminalId;
    private boolean isAuthenticated;
    private String navContext;
    private Instant requestTimestamp;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        isAuthenticated = true;
        navContext = "HOME";
        requestTimestamp = Instant.now();
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.currentTellerId = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.currentTerminalId = "term-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                currentTellerId,
                currentTerminalId,
                isAuthenticated,
                navContext,
                requestTimestamp
            );
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
        assertEquals("session.started", event.type());
        assertEquals("teller-01", event.getTellerId());
        assertEquals("term-01", event.getTerminalId());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        isAuthenticated = false; // Not authenticated
        navContext = "HOME";
        requestTimestamp = Instant.now();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        isAuthenticated = true;
        navContext = "HOME";
        // Simulate a timestamp way in the past to trigger timeout logic
        requestTimestamp = Instant.now().minusSeconds(3600); // 1 hour ago
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        isAuthenticated = true;
        navContext = "TRANSACTIONS"; // Invalid starting context for StartSession
        requestTimestamp = Instant.now();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
    }

}