package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private String lastEventId;

    // Helper to simulate an authenticated user
    private final String AUTHORIZED_TELLER_ID = "TELLER_123";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_001");
        // Simulate authentication state validity
        aggregate.markAuthenticated(AUTHORIZED_TELLER_ID);
        aggregate.validateNavigationState(true);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in context by the aggregate setup
    }

    @Given("a valid terminalId is provided")
    public void a valid_terminalId_is_provided() {
        // Handled in context by the aggregate setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            var cmd = new StartSessionCmd("SESSION_001", "TELLER_123", "TERM_001");
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEventId = events.get(0).aggregateId();
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(aggregate.uncommittedEvents());
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertEquals(SessionStartedEvent.class, aggregate.uncommittedEvents().get(0).getClass());
        assertEquals("SESSION_001", lastEventId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION_002");
        // Intentionally do NOT mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_003");
        aggregate.markAuthenticated(AUTHORIZED_TELLER_ID);
        // Force timeout
        aggregate.forceTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_004");
        aggregate.markAuthenticated(AUTHORIZED_TELLER_ID);
        // Invalidate navigation state
        aggregate.validateNavigationState(false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect either an IllegalStateException (invariant) or IllegalArgumentException (validation)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
