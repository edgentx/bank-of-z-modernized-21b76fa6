package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Throwable caughtException;
    private DomainEvent resultEvent;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Setup context that implies authentication failed, handled by specific test logic
        this.aggregate = new TellerSessionAggregate("session-123", false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Simulate timeout
        this.aggregate = new TellerSessionAggregate("session-123", true, true); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Simulate bad nav state
        this.aggregate = new TellerSessionAggregate("session-123", true, false, false);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd("session-123", "teller-42", "terminal-T1");
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                this.resultEvent = events.get(0);
            } else {
                this.resultEvent = null;
            }
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvent, "Expected an event to be emitted");
        Assertions.assertEquals("teller.session.started", resultEvent.type());
        Assertions.assertTrue(resultEvent instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvent;
        Assertions.assertEquals("teller-42", event.tellerId());
        Assertions.assertEquals("terminal-T1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Checking it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
