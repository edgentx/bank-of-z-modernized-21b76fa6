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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Simulate previous initialization to make it valid
        // In a real scenario, we would execute a StartSessionCmd.
        // For this test, we manually set the state to valid.
        aggregate.markAuthenticated("USER-123");
        aggregate.updateLastActivity();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        // State remains null/empty (not authenticated)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        aggregate.markAuthenticated("USER-123");
        // Simulate a session that was active long ago
        aggregate.setLastActivityTime(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-NAV-ERR");
        aggregate.markAuthenticated("USER-123");
        aggregate.updateLastActivity();
        // Force the aggregate into a state where navigation is inconsistent
        // (e.g. marked in a transaction that doesn't exist)
        aggregate.forceNavigationState("TX-999");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The aggregate is already initialized with ID in the Given steps
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
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
        assertNotNull(caughtException);
        // We expect either IllegalStateException or IllegalArgumentException depending on the invariant violated
        assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }
}
