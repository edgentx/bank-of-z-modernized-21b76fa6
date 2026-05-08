package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Test constants
    private static final String VALID_TELLER_ID = "TELLER_01";
    private static final String VALID_TERMINAL_ID = "TERM_42";
    private static final String SESSION_ID = "SESSION_101";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Ensure default state is valid
        aggregate.setCurrentScreen("SIGNON");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context prepared for the When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context prepared for the When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Simulate an unauthenticated state context
        // In this domain design, StartSession creates the session, so "unauthenticated"
        // might be modeled as attempting to start without proper credentials passed in command.
        // However, the prompt implies the *Aggregate* violates it.
        // We will rely on the aggregate's internal check.
        // Since StartSession creates the auth, to violate it, we might mock a state where
        // auth is strictly required but missing, or we test the command validation.
        // Let's assume the command fails validation if tellerId is invalid.
        // But to simulate the aggregate invariant, we can set a flag if the aggregate supported pre-auth checks.
        // Here, we'll test the negative case by effectively saying: 
        // "The command provided was effectively unauthenticated (null id)"
        // But the record throws exception on null.
        // We will simulate this by trying to start a session when one already exists (or another violation)
        // OR we can assume the 'StartSession' IS the authentication act.
        // Let's interpret "violates: must be authenticated" as the command being invalid.
        // But since `StartSessionCmd` validates input, we need a state-based violation.
        // Let's assume the aggregate has a `preAuthenticated` flag.
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Set last activity to 2 hours ago
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Set screen to something invalid for starting a session (e.g. TRANSACTION_IN_PROGRESS)
        aggregate.setCurrentScreen("TRANSACTION_MENU"); 
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Construct command based on scenario context
            // If we are in the 'unauthenticated' scenario (from our interpretation above),
            // we might try to pass invalid data, but the Record constructor blocks nulls.
            // We will execute the valid command. If the aggregate state is 'violating',
            // the aggregate logic inside `startSession` should throw the exception.
            
            // Note: For the specific "unauthenticated" scenario described in BDD, 
            // usually this means the command lacks auth token.
            // Since `StartSessionCmd` is simple data, we pass valid data and expect the 
            // aggregate to reject it based on its internal state if we configured it that way.
            // However, our aggregate implementation allows StartSession if valid.
            // To make the test pass for the "violates authentication" scenario, 
            // we need a logic branch in Aggregate or rely on Command validation.
            // Given the constraint "Command types: S18Command holding the request fields",
            // I will assume the command is valid data-wise, but aggregate rejects it.
            
            // Adjusting logic to match tests:
            // I will assume the generic catch block handles the domain errors.
            Command cmd = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
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
        assertEquals(SESSION_ID, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

}
