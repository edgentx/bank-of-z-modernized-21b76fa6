package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // State helpers for the 'valid' scenario
    private String validTellerId = "TELLER_001";
    private String validTerminalId = "TERM_A";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a fresh aggregate in the repository
        aggregate = repository.save(new TellerSessionAggregate("SESSION_1"));
        // Simulating a context where authentication is implicitly valid for the happy path
        // via the command payload or internal state not explicitly modeled in stubs
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the 'When' step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId, true, "HOME", 0L);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = repository.save(new TellerSessionAggregate("SESSION_2"));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = repository.save(new TellerSessionAggregate("SESSION_3"));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = repository.save(new TellerSessionAggregate("SESSION_4"));
    }

    @When("the StartSessionCmd command is executed")
    public void the_invalid_start_session_cmd_command_is_executed() {
        try {
            // Determine which violation we are testing based on the aggregate ID suffix or context
            String id = aggregate.id();
            Command cmd;

            if (id.endsWith("2")) {
                // Violation: Auth
                cmd = new StartSessionCmd(id, validTellerId, validTerminalId, false, "HOME", 0L);
            } else if (id.endsWith("3")) {
                // Violation: Timeout (simulated via timestamp check or explicit flag if modeled)
                // Assuming command carries timestamp context for validation
                cmd = new StartSessionCmd(id, validTellerId, validTerminalId, true, "HOME", 1L); // 1L implies invalid context
            } else {
                // Violation: Nav State
                cmd = new StartSessionCmd(id, validTellerId, validTerminalId, true, "", 0L); // Empty nav state
            }

            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verify it's a domain logic exception
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
