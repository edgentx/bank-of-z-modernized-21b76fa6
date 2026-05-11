package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private Throwable thrownException;
    private Iterable<DomainEvent> resultEvents;

    // Common State
    private String validTellerId = "TELLER-001";
    private String validTerminalId = "TERM-A";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION-INIT-01");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("SESSION-AUTH-FAIL");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        // Simulate an already active session
        this.aggregate = new TellerSessionAggregate("SESSION-NAV-ERR");
        // Manually activating to simulate bad state before command execution
        // (In real app, this would be loaded from repo in active state)
        try {
            StartSessionCmd setupCmd = new StartSessionCmd("SESSION-NAV-ERR", "t1", "term1", true, false);
            aggregate.execute(setupCmd);
        } catch (Exception e) {
            // ignore setup failures
        }
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // defaults are fine
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // defaults are fine
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Determine scenario context based on aggregate ID to set cmd flags appropriately
            boolean isAuthenticated = !aggregate.id().equals("SESSION-AUTH-FAIL");
            boolean isTimedOut = aggregate.id().equals("SESSION-TIMEOUT");
            
            this.command = new StartSessionCmd(
                aggregate.id(), 
                validTellerId, 
                validTerminalId, 
                isAuthenticated, 
                isTimedOut
            );
            
            this.resultEvents = aggregate.execute(command);
        } catch (Throwable t) {
            this.thrownException = t;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        DomainEvent event = resultEvents.iterator().next();
        assertTrue(event instanceof SessionStartedEvent);
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session.started", started.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
