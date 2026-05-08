package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private Exception capturedException;
    private List events;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // For a session to start, the teller must be authenticated.
        // We simulate this state as a precondition of the 'valid' aggregate for the success case.
        aggregate.markAsAuthenticated();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Valid ID prepared in the 'When' step construction or stored here
        // We'll construct the full command in the When step for simplicity
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Valid ID prepared in the 'When' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            String sessionId = UUID.randomUUID().toString();
            // If the aggregate was created in a previous step, use its ID, else create new
            if (aggregate != null) {
                sessionId = aggregate.id();
            } else {
                aggregate = new TellerSessionAggregate(sessionId);
            }

            command = new StartSessionCmd(
                sessionId,
                "TELLER-123", // Valid tellerId
                "TERM-456",    // Valid terminalId
                "MAIN_MENU"    // Valid context
            );
            events = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        Assertions.assertNotNull(events, "Events list should not be null");
        Assertions.assertEquals(1, events.size(), "Should have emitted exactly one event");
        Assertions.assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    // ---------------- NEGATIVE SCENARIOS ----------------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAsAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated();
        aggregate.markAsTimedOut(); // Sets last activity far in the past and active=true
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated();
        // Context validity check happens in the Command execution, not Aggregate state per se,
        // but we set up the command with invalid context in the specific step if needed,
        // or we assume the 'violation' implies the system state makes it impossible.
        // Given the Gherkin structure, we will assume the violation is triggered by the command execution
        // context being invalid. Since the command is constructed in the @When step for the positive flow,
        // for this specific negative flow, we might need to adjust the @When step behavior or assume
        // the violation refers to the aggregate's inability to handle the context.
        // However, the invariant check is: "Navigation state must accurately reflect...".
        // If the command context is null/blank, it throws IllegalArgumentException.
        // To make this specific step definition distinct and fit the Gherkin:
        // I will rely on the @When step to trigger the error based on the setup here if I can pass state,
        // but the current @When is shared.
        // Interpretation: The violation is inherent to the context of the operation.
        // I will leave the Aggregate clean, and the violation will be triggered by the specific scenario context
        // if I could inject the null context. Since the shared @When uses hardcoded valid values,
        // and Cucumber runs scenarios in isolation, I will assume the test passes valid context and this scenario
        // expects a failure for OTHER reasons or I need a specific When.
        // *Correction*: The scenarios are sequential or isolated. The @When is shared. 
        // I will verify the logic: The exception thrown for Invalid Context is an IllegalArgumentException, caught in the @When.
        // I will tweak the @When or this setup to ensure the command has bad context for THIS specific path.
        // BUT: I cannot change the @When signature easily without step params.
        // The previous steps use "valid TellerSession aggregate". 
        // I will interpret the "violates" step as setting up the Aggregate in a state that rejects the command.
        // But the Context check is on the Command, not the Aggregate state.
        // I will create a flag in the steps class to indicate "Next command should be invalid".
    }
    
    // Helper field to trigger invalid context for the last scenario
    private boolean nextCommandShouldBeInvalid = false;

    // Overriding the assumption for the violation step
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setup_invalid_navigation_context() {
        nextCommandShouldBeInvalid = true;
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated();
    }

    // Updating the shared @When to handle the flag for the negative case
    @When("the StartSessionCmd command is executed")
    public void execute_command_with_flag() {
        try {
            String sessionId = UUID.randomUUID().toString();
            if (aggregate != null) {
                sessionId = aggregate.id();
            } else {
                aggregate = new TellerSessionAggregate(sessionId);
            }
            
            String context = (nextCommandShouldBeInvalid) ? "" : "MAIN_MENU";
            nextCommandShouldBeInvalid = false; // reset

            command = new StartSessionCmd(
                sessionId,
                "TELLER-123",
                "TERM-456",
                context
            );
            events = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Domain errors manifest as RuntimeException, IllegalStateException, or IllegalArgumentException
        Assertions.assertTrue(capturedException instanceof RuntimeException, "Expected RuntimeException");
    }
}
