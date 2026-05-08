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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Command will be constructed in the 'When' block using valid IDs
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Command will be constructed in the 'When' block
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Assume if not specifically set invalid, we use valid defaults
            if (command == null) {
                command = new StartSessionCmd("session-123", "teller-1", "term-A");
            }
            resultEvents = aggregate.execute(command);
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
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-A", event.terminalId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        command = new StartSessionCmd("session-bad-auth", "", "term-A"); // Blank tellerId
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setStatus(TellerSessionAggregate.Status.ACTIVE);
        // Set time to more than 30 minutes ago
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1801)); 
        command = new StartSessionCmd("session-timeout", "teller-1", "term-A");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        // Passing null or blank terminalId implies invalid navigation context
        command = new StartSessionCmd("session-bad-nav", "teller-1", ""); 
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check if it's an IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }
}
