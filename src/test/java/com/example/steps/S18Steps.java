package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSession aggregate;
    private List<DomainEvent> result;
    private Exception thrownException;

    // Scenario 1: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("session-123");
        // Ensure default valid state for success
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentState("INIT");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the 'When' step via command creation
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the 'When' step via command creation
    }

    // Scenario 2, 3, 4 Setups for specific violations
    @Given("A TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSession("session-123");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setCurrentState("INIT");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("A TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-123");
        aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago (Assuming 15 min timeout in aggregate)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200));
        aggregate.setCurrentState("INIT");
    }

    @Given("A TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav() {
        aggregate = new TellerSession("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setCurrentState("TXN_IN_PROGRESS"); // Invalid state for starting a session
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("teller-1", "term-1", "valid-token");
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) result.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
