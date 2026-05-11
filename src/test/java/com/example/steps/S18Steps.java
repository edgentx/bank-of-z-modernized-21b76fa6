package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermaintenance.model.TellerSessionAggregate;
import com.example.domain.tellermaintenance.model.cmd.StartSessionCmd;
import com.example.domain.tellermaintenance.model.event.SessionStartedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context loaded in the When block
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context loaded in the When block
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // In this domain logic, authentication is likely implied by the presence of a valid tellerId 
        // or checked externally. We simulate a violation by passing a blank tellerId in the command.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout_config() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // We construct the command here based on the context provided by the "Given" steps.
            // For the successful path, we use valid data.
            // For the violation paths, the aggregate state doesn't prevent execution,
            // but the command construction will reflect the violation (e.g. nulls/invalids) 
            // to trigger the domain logic invariants.
            
            // Note: Since Cucumber scenarios are isolated, we determine the 'scenario type' by checking aggregate ID
            // or just assume valid data if we are in the success scenario. 
            // A cleaner way in real code is scenario context, but here we use heuristic.
            
            if (aggregate.id().equals("session-auth-fail")) {
                cmd = new StartSessionCmd(aggregate.id(), "", "term-1", 3600, "MAIN_MENU"); // Violation: blank tellerId
            } else if (aggregate.id().equals("session-timeout-fail")) {
                cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", 0, "MAIN_MENU"); // Violation: 0 timeout
            } else if (aggregate.id().equals("session-nav-fail")) {
                cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", 3600, ""); // Violation: blank nav context
            } else {
                // Default success case
                cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", 3600, "MAIN_MENU");
            }
            
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected domain error (IllegalArgumentException or IllegalStateException), got: " + capturedException.getClass().getSimpleName()
        );
    }
}
