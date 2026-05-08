package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-1");
        this.aggregate.markAuthenticated(); // Pre-authenticate for success scenario
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-1");
        // Do not mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-1");
        this.aggregate.markAuthenticated();
        // Force the aggregate into a timed out state (logic handled inside aggregate for test)
        this.aggregate.forceTimedOutState();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        this.aggregate = new TellerSessionAggregate("session-1");
        this.aggregate.markAuthenticated();
        // Force invalid nav state
        this.aggregate.forceInvalidNavigationState();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // TellerId is part of the Command construction in 'When'
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // TerminalId is part of the Command construction in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd("session-1", "teller-123", "terminal-456");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("teller-123", event.tellerId());
        Assertions.assertEquals("terminal-456", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // We expect IllegalStateException for invariant violations
        Assertions.assertTrue(caughtException instanceof IllegalStateException || 
                              caughtException instanceof IllegalArgumentException,
                              "Expected domain violation exception, got: " + caughtException.getClass());
    }
}
