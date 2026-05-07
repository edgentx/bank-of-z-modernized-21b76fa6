package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markNavigationContextInvalid();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // We assume valid IDs unless the specific violation context requires otherwise,
            // but the violations here are mostly state-based (auth flag, time, nav state).
            // For the 'Authentication' failure, we pass isAuthenticated=false.
            boolean isAuthenticated = true;
            
            // Check if this is the auth-fail scenario by inspecting the aggregate state or a flag
            // However, simpler is to pass the command that triggers the failure.
            // Scenario 2 explicitly requires failure.
            // Since we can't easily pass scenario info between steps without shared state complexity,
            // we'll assume the command defaults to authenticated=true, and if we are testing the auth fail,
            // we construct a command with authenticated=false.
            
            // Heuristic: if the aggregate is not set up for timeout or nav fail, and we want a fail, it's auth.
            // But `StartSessionCmd` takes the auth flag.
            // Let's look at the Gherkin: "Given... violates: auth... When... executed".
            // We need to dispatch the correct command.
            
            // Simple approach: check aggregate ID or type hint? No.
            // We will just default to authenticated=true. The Auth Failure test needs a specific command.
            // Let's assume standard execution first.
            Command cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", isAuthenticated);
            
            // Refinement for specific Scenarios:
            // The aggregate setup for 'auth-fail' was generic. The violation is strictly in the Command's flag.
            // The aggregate setup for 'timeout' and 'nav' modified the aggregate state.
            // So if the aggregate is NOT modified for timeout/nav, we might be in the auth fail scenario?
            // Or we can try-catch block logic.
            
            // Let's stick to the command construction:
            // If the aggregate is the one from "violates: ... auth", we should send authenticated=false.
            // We can detect this by a flag or just checking a specific logic.
            // To keep it simple and working:
            // The generic Given for auth-fail doesn't modify state. The others do (markExpired, markNavInvalid).
            // So if the aggregate is NOT marked expired/invalid, and we want a fail, it MUST be auth.
            // But wait, the "Success" scenario also doesn't modify state.
            // 
            // Resolution: I will assume the test setup controls the flow.
            // If I want to test Auth Fail, I must pass `false`.
            // If I want to test Success, I pass `true`.
            // Let's assume `isAuthenticated = true` by default.
            // How to distinguish? 
            // We can look at the aggregate ID string used in the Given steps.
            if (aggregate.id().equals("session-auth-fail")) {
                isAuthenticated = false;
            }

            cmd = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", isAuthenticated);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // The specific exception types in the Aggregate are IllegalStateException
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
