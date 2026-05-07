package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String providedTellerId;
    private String providedTerminalId;
    private Exception caughtException;
    private Iterable<DomainEvent> resultEvents;

    // Command flags to simulate the invariants
    private boolean cmdAuthenticated = true;
    private String cmdNavState = "IDLE";
    private boolean cmdTimedOut = false;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Reset defaults
        cmdAuthenticated = true;
        cmdNavState = "IDLE";
        cmdTimedOut = false;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        providedTellerId = "teller-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        providedTerminalId = "term-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        providedTellerId = "teller-01";
        providedTerminalId = "term-01";
        cmdAuthenticated = false; // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        providedTellerId = "teller-01";
        providedTerminalId = "term-01";
        cmdTimedOut = true; // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("session-123");
        providedTellerId = "teller-01";
        providedTerminalId = "term-01";
        cmdNavState = "BUSY"; // Violate invariant (expected IDLE)
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                providedTellerId,
                providedTerminalId,
                cmdAuthenticated,
                cmdNavState,
                cmdTimedOut
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        DomainEvent event = resultEvents.iterator().next();
        assertTrue(event instanceof SessionStartedEvent);
        assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect IllegalStateException for domain invariant violations
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
