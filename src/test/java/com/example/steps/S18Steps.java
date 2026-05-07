package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Cucumber Glue Code for S-18 Teller Session features.
 * Mapping the Gherkin scenarios to the Java Domain Model.
 */
public class S18Steps {

    private TellerSession session;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Using the constructor that sets up valid defaults
        session = new TellerSession("TEST_SESSION_1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context set in the 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context set in the 'When' step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd("TELLER_01", "TERM_01");
        try {
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "No events were returned");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // Scenario 2: Auth Rejection
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        session = new TellerSession("TEST_SESSION_2", false, true, true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // Scenario 3: Timeout Rejection
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSession("TEST_SESSION_3", true, false, true);
    }

    // Scenario 4: Navigation State Rejection
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        session = new TellerSession("TEST_SESSION_4", true, true, false);
    }
}
