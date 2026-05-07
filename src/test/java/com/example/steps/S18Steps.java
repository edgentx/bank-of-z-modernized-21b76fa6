package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermetadata.model.StartSessionCmd;
import com.example.domain.tellermetadata.model.SessionStartedEvent;
import com.example.domain.tellermetadata.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Throwable caughtException;
    private List<DomainEvent> resultEvents;

    // Helper constants for valid scenarios
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_01";
    private static final String VALID_LOCATION_ID = "LOC_MAIN";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_with_no_auth() {
        aggregate = new TellerSessionAggregate("SESSION_2");
        // Leave authentication null/empty to violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_with_invalid_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_3");
        aggregate.updateAuthentication(VALID_TELLER_ID); // Must be authenticated
        aggregate.updateTimeoutConfig(Duration.ofSeconds(-1)); // Invalid config
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_with_invalid_navigation() {
        aggregate = new TellerSessionAggregate("SESSION_4");
        aggregate.updateAuthentication(VALID_TELLER_ID);
        aggregate.updateTimeoutConfig(Duration.ofMinutes(30));
        aggregate.setInvalidNavigationState(true);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // In a real repo context, we would set the auth state here.
        // Given the in-memory aggregate setup, we pre-set it in the 'Given' block
        // or use a specific setup method if the aggregate needed to exist in a 'clean' state first.
        // For this feature, we assume the 'valid' implies the internal state allows it.
        if (aggregate != null && "SESSION_1".equals(aggregate.id())) {
             aggregate.updateAuthentication(VALID_TELLER_ID);
        }
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Terminal ID is part of the command payload, not necessarily pre-existing state
        // unless checking terminal availability. Here we just acknowledge the context.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Construct the command with required context
            // For negative cases, specific internal state is already set in the Given blocks
            Command cmd = new StartSessionCmd(
                aggregate.id(),
                VALID_TERMINAL_ID,
                VALID_LOCATION_ID,
                Instant.now()
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We expect specific exceptions (IllegalStateException/IllegalArgumentException) indicating domain invariant violation
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain exception (IllegalStateException/IllegalArgumentException), got: " + caughtException.getClass().getSimpleName()
        );
    }
}
