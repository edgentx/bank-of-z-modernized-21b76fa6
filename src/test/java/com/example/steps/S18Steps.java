package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Placeholder for context setup if needed
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Placeholder for context setup if needed
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated(); // Intentionally leave unauthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsTimedOut(); // Intentionally set to timed out state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a corrupted navigation state (e.g., mismatched transaction ID)
        // For testing purposes, we might not expose a direct setter, but assuming a testing seam
        aggregate.corruptNavigationState();
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        caughtException = null;
        try {
            // We assume for the "happy path" scenarios the aggregate is implicitly authenticated
            // by the setup or the command constructor.
            // For the specific rejection scenario, the aggregate state was pre-set above.
            
            // If aggregate is currently "clean", assume valid auth details
            boolean simulateValidAuth = !aggregate.isTimedOut() && !aggregate.isNavigationCorrupted();
            
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(), 
                "tell-123", 
                "term-456"
            );
            
            if (simulateValidAuth) {
                aggregate.markAuthenticated(); // Simulate valid auth for success path
            }
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException || 
                              caughtException instanceof IllegalArgumentException);
    }
}
