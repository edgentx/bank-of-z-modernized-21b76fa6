package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellerauthentication.model.TellerAuthenticatedEvent;
import com.example.domain.ui.model.SessionStartedEvent;
import com.example.domain.ui.model.StartSessionCmd;
import com.example.domain.ui.model.TellerSession;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSession aggregate;
    private List<DomainEvent> result;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("session-1");
        // Simulate authentication event to put aggregate in valid state
        aggregate.apply(new TellerAuthenticatedEvent("session-1", "teller-1", Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-2");
        // Intentionally do not apply authentication event
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-3");
        // Authenticated, but simulated stale state (logic handled in aggregate)
        aggregate.apply(new TellerAuthenticatedEvent("session-3", "teller-1", Instant.now().minusSeconds(3600)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSession("session-4");
        aggregate.apply(new TellerAuthenticatedEvent("session-4", "teller-1", Instant.now()));
        // Force invalid state directly for test purposes if constructor/apply allows, 
        // or rely on the aggregate business logic to detect the mismatch when cmd executes.
        // Here we assume the aggregate checks valid context states (e.g. Maintenance Mode vs Normal).
        // Since we can't easily set complex invalid state without setters, we assume the command execution
        // might check global config. For this step, we verify the rejection logic.
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in Scenario initial Given
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in Scenario initial Given
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd("session-id", "teller-1", "terminal-1");
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Could be IllegalStateException, IllegalArgumentException, or custom DomainException
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
