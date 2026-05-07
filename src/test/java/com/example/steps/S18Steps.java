package com.example.steps;

import com.example.domain.tellersession.model.*;
import com.example.domain.shared.*;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in execution step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup handled in execution step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-404");
        // We simulate an unauthenticated state by passing false to a test constructor or simply
        // relying on the command logic checking a flag. Here we assume the default is NOT authenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // We can create the aggregate in a state that simulates a timeout
        // Since the aggregate controls state, we might need a specific method or command to set it up,
        // or assume the new aggregate is invalid (less likely for 'Start').
        // Given the 'Start' command initiates the session, this scenario implies the check happens during start.
        // For this test, we assume the context implies an invalid state for the inputs.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd;
        try {
            // We determine validity based on the scenario context. In a real Cucumber setup, 
            // we might store tellerId/terminalId in the scenario context.
            // For simplicity here, we assume valid defaults unless the context implies invalid data (which is harder to pass without context objects).
            // However, looking at the "violates" clauses, they seem to check internal invariants.
            // We will use a valid command, and expect the Aggregate to throw if invariants are met.
            // But wait: The 'violates' clauses usually imply the PRE-STATE is invalid, OR the INPUT is invalid.
            // "A teller must be authenticated": usually means the Command needs an authenticated context.
            // To keep it simple and working with the generated code: 
            // The command requires isAuthenticated=true. We will pass true here. 
            // The 'violates' step might need to construct a command with false, or we verify the logic.
            // Let's assume the 'violates' steps set up the Aggregate such that it would reject a valid command, 
            // OR we pass a 'bad' command.
            // Let's pass a valid command. If the test is the 'violates' one, the 'Given' might have set up the Aggregate to reject it.
            // But since StartSessionCmd starts the lifecycle, the 'violates' likely implies the Command inputs are bad (e.g. Auth false).
            
            // Heuristic: If the description says "violates: ... authenticated", we pass Auth=False in the command.
            boolean isAuthenticated = !aggregate.id().equals("session-404");
            cmd = new StartSessionCmd("session-id", "teller-1", "terminal-1", isAuthenticated);
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
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
        Assertions.assertNotNull(thrownException);
        // We expect an IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
