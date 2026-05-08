package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    // State for the scenario
    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Standard Constants
    private static final String VALID_TELLER_ID = "TELLER_01";
    private static final String VALID_TERMINAL_ID = "TERM_42";
    private static final String SESSION_ID = "SESSION_123";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Default to a valid state for success scenario
        aggregate.markAuthenticated();
        aggregate.markValidNavigationContext();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.markStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.markInvalidNavigationContext();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the 'When' clause by constructing the command with valid ID
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the 'When' clause by constructing the command with valid ID
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // We assume valid IDs for the command payload unless specified otherwise.
        // The violations are in the AGGREGATE state (preconditions), not necessarily the command payload format.
        command = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(SESSION_ID, startedEvent.aggregateId());
        assertEquals(VALID_TELLER_ID, startedEvent.tellerId());
        assertEquals(VALID_TERMINAL_ID, startedEvent.terminalId());
        
        // Also ensure no exception was thrown
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException for domain invariant violation");
        
        // Ensure no events were emitted on failure
        assertNull(resultEvents, "Expected no events when command is rejected");
    }
}
