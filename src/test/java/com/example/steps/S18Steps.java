package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private DomainEvent resultingEvent;

    // State for valid execution
    private static final String VALID_TELLER_ID = "tell-100";
    private static final String VALID_TERMINAL_ID = "term-200";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // Pre-authenticate for the success case (assuming auth happens before start or is part of context)
        // The prompt says "Initiates a teller session following successful authentication"
        // We simulate an authenticated state for the aggregate
        aggregate.markAuthenticated(VALID_TELLER_ID);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate("session-fail-auth");
        // Intentionally NOT authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("session-fail-timeout");
        aggregate.markAuthenticated(VALID_TELLER_ID);
        aggregate.simulateTimeoutViolation();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("session-fail-nav");
        aggregate.markAuthenticated(VALID_TELLER_ID);
        aggregate.simulateInvalidNavigationState();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in "Given" step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup handled in "Given" step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // For scenarios needing authentication context, we use the constants
            Command cmd = new StartSessionCmd(aggregate.id(), VALID_TELLER_ID, VALID_TERMINAL_ID);
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvent, "Expected an event to be emitted");
        assertTrue(resultingEvent instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent evt = (SessionStartedEvent) resultingEvent;
        assertEquals(aggregate.id(), evt.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain exception to be thrown");
        // Validating it's an IllegalArgumentException or IllegalStateException (standard domain error types)
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
