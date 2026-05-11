package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String validTellerId = "TELLER_101";
    private String validTerminalId = "TERM_42";
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Scenario: Successfully execute StartSessionCmd ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_01");
        // Ensure it meets the pre-conditions for success
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentContext("LOGIN");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in When block via variable
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in When block via variable
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(validTellerId, event.tellerId());
        assertEquals(validTerminalId, event.terminalId());
    }

    // --- Scenario: Rejected - Auth ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION_02");
        // Do NOT mark authenticated
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // --- Scenario: Rejected - Timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_03");
        aggregate.markAuthenticated();
        // Set activity to 20 minutes ago (Assuming invariant is 15 mins)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200)); 
        aggregate.setCurrentContext("LOGIN");
    }

    // --- Scenario: Rejected - Navigation State ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_04");
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Set context to something invalid for starting a session (e.g. already in a transaction)
        aggregate.setCurrentContext("TX_IN_PROGRESS");
    }

}
