package com.example.steps;

import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private StartSessionCmd cmd;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSession("SESSION-01");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("SESSION-02");
        // The aggregate is created in a state where it can't accept a start command
        // We'll rely on the aggregate's internal state or command properties to fail validation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("SESSION-03");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSession("SESSION-04");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in When clause
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in When clause
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Constructing the command with defaults, modifying based on scenario context would require more context objects
            // For the purpose of these steps, we assume the command structure supports the validation
            if (aggregate.id().equals("SESSION-02")) {
                // Violation: Teller not authenticated
                cmd = new StartSessionCmd("SESSION-02", "teller-1", "term-1", false, null, "INIT");
            } else if (aggregate.id().equals("SESSION-03")) {
                // Violation: Timeout config
                cmd = new StartSessionCmd("SESSION-03", "teller-1", "term-1", true, null, "INIT");
            } else if (aggregate.id().equals("SESSION-04")) {
                // Violation: Navigation state
                cmd = new StartSessionCmd("SESSION-04", "teller-1", "term-1", true, null, "INVALID");
            } else {
                // Valid Command
                cmd = new StartSessionCmd("SESSION-01", "teller-1", "term-1", true, java.time.Instant.now().plusSeconds(3600), "INIT");
            }
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(aggregate.uncommittedEvents());
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check for specific exceptions based on invariants
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
