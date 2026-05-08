package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in When step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in When step construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.markUnauthenticated(); // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markInactive(); // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.markNavigationInvalid(); // Violate invariant
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Construct a generally valid command, context comes from Aggregate state
            command = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1");
            resultEvents = aggregate.execute(command);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verify it's the correct type of exception (IllegalStateException for invariants)
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
