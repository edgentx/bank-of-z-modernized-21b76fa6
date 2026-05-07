package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private Command cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // A new aggregate is valid for starting a session
        aggregate = new TellerSession("teller-1", "terminal-1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create an aggregate instance but attempt to start with missing/invalid auth data context
        // In this domain logic, authentication is passed or implied valid via cmd context
        // We simulate a failure case by providing invalid logic or state if aggregate state was tracked.
        // Since StartSessionCmd carries the intent, we will catch this in the validation logic.
        aggregate = new TellerSession("teller-1", "terminal-1");
        // We will simulate the violation via command content in the 'When' step or a specific invalid setup if supported.
        // For this scenario, the aggregate is valid, but the Command/Context might not be.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout_config() {
        aggregate = new TellerSession("teller-1", "terminal-1");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession("teller-1", "terminal-1");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // TellerId is typically part of the command or aggregate context.
        // Implicitly handled in 'When' step construction.
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // TerminalId is typically part of the command or aggregate context.
        // Implicitly handled in 'When' step construction.
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Scenario specific logic based on the Given state
        if (aggregate.getId().equals("fail-auth")) {
             // Logic to trigger auth failure if needed, though usually this is about the cmd content.
             // For now, we assume standard execution, relying on the aggregate to throw if logic demands.
        }
        
        // Default valid command construction
        Instant timeout = Instant.now().plus(Duration.ofHours(8));
        String state = "IDLE";
        
        // Mapping scenario descriptions to command data to trigger failures
        if (aggregate.toString().contains("violates: A teller must be authenticated")) {
             // Assuming authentication is handled externally, this might be a precondition check.
             // We'll simulate a command that fails validation if the aggregate requires it, 
             // or we rely on the specific 'Given' to set up the aggregate in a way that fails.
             // However, the simplest way to force failure is passing invalid data if the aggregate validates.
             // Or, since StartSessionCmd *initiates* the session, the violation implies the command/request is invalid.
             // Let's assume the 'Given' sets up the Aggregate, and we construct a cmd accordingly.
             // Actually, the error says "A teller must be authenticated". If this is a command pre-condition,
             // we might throw if cmd.tellerId is null. But the Cmd has it.
             // Let's simulate a missing authenticated context.
             // Since we can't change the aggregate constructor (it's standard), we pass a specific flag or data
             // that the aggregate logic rejects. 
             // Looking at the aggregate implementation: it throws if authenticated is false.
             cmd = new StartSessionCmd("teller-1", "terminal-1", timeout, state, false);
        } else if (aggregate.toString().contains("violates: Sessions must timeout")) {
             // Pass a timeout in the past or null to trigger invariant violation
             cmd = new StartSessionCmd("teller-1", "terminal-1", Instant.now().minusSeconds(60), state, true);
        } else if (aggregate.toString().contains("violates: Navigation state")) {
             // Pass an invalid state (e.g. null or empty)
             cmd = new StartSessionCmd("teller-1", "terminal-1", timeout, null, true);
        } else {
             // Success case / standard valid case
             cmd = new StartSessionCmd(aggregate.getId(), "terminal-1", timeout, state, true);
        }

        try {
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
        assertNull(caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check for specific exceptions based on the scenario
        if (caughtException.getMessage().contains("authenticated")) {
            assertTrue(caughtException instanceof IllegalStateException);
            assertTrue(caughtException.getMessage().contains("authenticated"));
        } else if (caughtException.getMessage().contains("timeout")) {
             assertTrue(caughtException instanceof IllegalArgumentException);
        } else if (caughtException.getMessage().contains("state")) {
             assertTrue(caughtException instanceof IllegalArgumentException);
        }
    }
}
