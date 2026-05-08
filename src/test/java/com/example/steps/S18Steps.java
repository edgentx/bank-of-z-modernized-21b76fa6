package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Constants for valid input
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_3270_01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        assertNull(capturedException);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in 'When' step execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup handled in 'When' step execution
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION_UNAUTH");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // To simulate this violation during 'Start', we mimic an aggregate
        // that believes it is already active (and thus potentially stale).
        // In a real repo, this would be loaded from state.
        aggregate = new TellerSessionAggregate("SESSION_TIMEOUT") {
            // Overriding getState to simulate an Active state that triggers the timeout check
            // This is a test-specific hack to force the logic path without complex persistence.
            // Alternatively, we could execute a start, then try to start again if the logic allowed,
            // but the logic says if Active -> throw timeout.
            // We'll rely on the aggregate being fresh, but the Command being constructed such that 
            // internal state is manipulated (not possible with final fields in this design).
            // Instead, we will construct the aggregate in a way that implies it is already Active.
        };
        // Actually, the simplest way is to NOT mock the aggregate internals, but to verify
        // that IF the aggregate considers itself Active, it fails.
        // Since TellerSessionAggregate sets state to NONE in constructor, we can't easily set it to ACTIVE
        // without a setter or a constructor overload. 
        // Re-reading the scenario: "Given a TellerSession aggregate that violates..." 
        // implies the Aggregate State *itself* is the violation source? 
        // Or the inputs? Usually Aggregate State.
        // For this implementation, we will assume the aggregate is effectively Active/Stale.
        // I will add a mechanism to force state if needed, but standard constructor is NONE.
        // Let's assume the violation is triggered by attempting to start on an active session.
        aggregate = new TellerSessionAggregate("SESSION_ALREADY_ACTIVE") {
            @Override
            public com.example.domain.uimodel.model.TellerSessionAggregate.State getState() {
                return State.ACTIVE; // Simulating that it is already active
            }
        };
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_BAD_NAV");
        aggregate.markNavigationStateInvalid();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Construct command based on scenario context
            // By default we assume valid input, specific violations handled in aggregate state
            boolean isAuthenticated = true;
            
            // Check if we are in the "unauthenticated" scenario
            // We can detect this by the sessionId or a flag, but here we assume standard valid execution
            // unless the aggregate setup implies otherwise.
            // However, the auth check is on the Command or Context.
            // The aggregate checks `cmd.isAuthenticated()`. 
            // We need to pass false for the auth scenario. 
            // We can infer this from the aggregate ID or just parallel state.
            // Ideally, the scenario "Given... violates authentication" sets up a flag.
            // Since Cucumber steps are sequential, we can just check the aggregate ID.
            
            if (aggregate.id().equals("SESSION_UNAUTH")) {
                isAuthenticated = false;
            }

            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), VALID_TELLER_ID, VALID_TERMINAL_ID, isAuthenticated);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In this domain, we use RuntimeException or IllegalStateException for domain errors
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof UnknownCommandException);
    }
}
