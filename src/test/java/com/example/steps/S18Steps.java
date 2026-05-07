package com.example.steps;

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
    private StartSessionCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception domainException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // By default, we set up a valid state for success scenario
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("session-401");
        // Do NOT mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_timed_out() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        aggregate.simulateTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_invalid_context() {
        aggregate = new TellerSessionAggregate("session-nav-err");
        aggregate.markAuthenticated();
        aggregate.invalidateNavigationContext();
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Implicitly handled in When step construction, or stored if needed
        // For this implementation, we construct the command directly in the When step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Implicitly handled in When step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        command = new StartSessionCmd(aggregate.id(), "teller-101", "terminal-T01");
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            domainException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-101", event.tellerId());
        assertEquals("terminal-T01", event.terminalId());
        
        // Verify Aggregate State Mutation
        assertTrue(aggregate.isActive());
        assertEquals("teller-101", aggregate.getTellerId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(domainException);
        assertTrue(domainException instanceof IllegalStateException);
        assertNull(resultingEvents);
    }

    // --- Helper for test lifecycle if needed, though Cucumber handles new instances per scenario ---
}
