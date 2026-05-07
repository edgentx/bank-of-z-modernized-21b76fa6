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
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in 'When' block construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in 'When' block construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We simulate the violation by passing false in the command, 
        // or we could set state on aggregate if the check relied on internal state. 
        // The S-18 logic checks the Command flag. The violation is sending a command with false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setActive(true);
        // Set last activity to 2 hours ago to simulate violation
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Logic is enforced via Command validation. Violation is passing invalid state in command.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Scenario-specific logic defaults
        boolean isAuthenticated = true; 
        String navState = "HOME";

        // Adjust parameters based on the 'Given' setup context 
        // (In a real framework we might use a scenario context, but here we inspect the aggregate ID or state)
        if ("session-auth-fail".equals(aggregate.id())) {
            isAuthenticated = false;
        } else if ("session-nav-fail".equals(aggregate.id())) {
            navState = "TRANSACTIONS"; // Invalid start state
        } 
        // Note: Timeout violation is triggered by existing aggregate state being Active + Expired.
        // The start command logic checks: if (isActive && expired) -> throw.
        // We don't need to change the command parameters for the timeout case, just the aggregate state setup above.

        StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1", isAuthenticated, navState);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("teller-1", event.tellerId());
        Assertions.assertEquals("terminal-1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Invariants result in IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || 
            thrownException instanceof IllegalArgumentException,
            "Expected domain error exception, got: " + thrownException.getClass().getSimpleName()
        );
    }
}