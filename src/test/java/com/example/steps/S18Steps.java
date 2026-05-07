package com.example.steps;

import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 * Maps Gherkin scenarios to Java domain logic.
 */
public class S18Steps {

    private TellerSession session;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        session = new TellerSession("TS-S1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the When step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the When step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            cmd = new StartSessionCmd("T-01", "TM-05", true);
            resultingEvents = session.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not throw exception");
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("session.started", resultingEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        session = new TellerSession("TS-ERR-AUTH");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Exception must be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSession("TS-ERR-TIMEOUT");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        session = new TellerSession("TS-ERR-NAV");
    }
}
