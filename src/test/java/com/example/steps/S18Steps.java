package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // ID format for TellerSession is often TELLER_ID + TERMINAL_ID or a UUID
        aggregate = new TellerSessionAggregate("TS-12345");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup, usually handled in the 'When' step via the Command payload
        // Kept empty to satisfy Gherkin syntax, data is passed in Command execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Using valid defaults for the success case
        StartSessionCmd cmd = new StartSessionCmd("TS-12345", "tell_user_01", "TERM-01");
        executeCommand(cmd);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        // We simulate a state where the user is NOT authenticated.
        // In a real app, this might be a flag on the aggregate or a separate Auth aggregate.
        // Here we pass a null or invalid teller token to the command to trigger the failure logic.
        aggregate = new TellerSessionAggregate("TS-999");
        // We will signal this violation by executing with a null/invalid Teller ID
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("TS-TIMEOUT");
        // We simulate this by passing a timestamp that is ancient in the command
        // Note: The Command usually carries the 'now' or the session start time
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("TS-NAV-ERR");
        // We simulate this by passing an invalid navigation state in the command
    }

    // We reuse the 'When' step, but the command data needs to be specific for the violation contexts.
    // Since Cucumber 'When' matches text, we can rely on the specific scenario state setup above.
    // However, to keep it simple, we might overload the execution in the @When if we had different text.
    // Since the text is identical, we check the aggregate ID or a flag to decide what payload to send.
    // A cleaner way for this exercise is to assume the step definition is context-aware, or we inject specific data.
    // To make this robust without complex context management, I'll modify the execution logic below to be smart or
    // assume the 'Given' prepared the 'Command' variable if I had one.
    
    // Re-defining the execution flow to be context-aware based on the Aggregate ID used in Given clauses.
    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_context_aware() {
        String id = aggregate.id();
        Command cmd;

        if (id.equals("TS-12345")) {
            // Happy path
            cmd = new StartSessionCmd(id, "teller_1", "term_1");
        } else if (id.equals("TS-999")) {
            // Violation: Auth
            cmd = new StartSessionCmd(id, null, "term_1"); // Null teller violates auth
        } else if (id.equals("TS-TIMEOUT")) {
            // Violation: Timeout (Simulated by passing an old timestamp if command supported it, or invalid token)
            // The command takes a token. An empty/expired token might trigger the logic if we were implementing auth checks.
            // For this POC, we'll use a specific marker string that the aggregate logic rejects.
            cmd = new StartSessionCmd(id, "EXPIRED_TOKEN", "term_1");
        } else if (id.equals("TS-NAV-ERR")) {
            // Violation: Nav State
            // Command takes a nav state. We pass an invalid one.
            cmd = new StartSessionCmd(id, "teller_1", "term_1", "INVALID_CONTEXT");
        } else {
            // Default fallback
            cmd = new StartSessionCmd(id, "teller_1", "term_1");
        }
        
        executeCommand(cmd);
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("session.started", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception but command succeeded");
        // Check it's a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
