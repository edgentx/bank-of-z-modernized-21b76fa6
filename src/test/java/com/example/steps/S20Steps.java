package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario: Successfully execute EndSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "TS-12345";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure authenticated
        aggregate.activate(); // Ensure active
        aggregate.setNavigationState("MAIN_MENU"); // Ensure valid state
        aggregate.markInactiveFor(Duration.ofMinutes(1)); // Ensure not timed out
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the constructor of the aggregate above
        assertNotNull(aggregate.id());
    }

    // Scenario: EndSessionCmd rejected — A teller must be authenticated to initiate a session.
    @Given("A TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "TS-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.activate(); // Active but NOT authenticated
        aggregate.setNavigationState("MAIN_MENU");
        // Do NOT call markAuthenticated()
    }

    // Scenario: EndSessionCmd rejected — Sessions must timeout after a configured period of inactivity.
    @Given("A TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "TS-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.activate();
        // Set inactivity to > 15 minutes (threshold defined in Aggregate)
        aggregate.markInactiveFor(Duration.ofMinutes(20));
        aggregate.setNavigationState("MAIN_MENU");
    }

    // Scenario: EndSessionCmd rejected — Navigation state must accurately reflect the current operational context.
    @Given("A TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "TS-NAV-ERR";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.activate();
        aggregate.markInactiveFor(Duration.ofMinutes(1));
        // Explicitly corrupt the navigation state (normally internal, but we simulate a violation via test setup)
        aggregate.setNavigationState(""); // Blank state
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent);
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals(aggregate.id(), endedEvent.aggregateId());
        assertFalse(aggregate.isActive(), "Session should be terminated");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }

}
