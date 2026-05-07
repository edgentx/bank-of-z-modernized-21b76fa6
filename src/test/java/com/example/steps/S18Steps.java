package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Test data
    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_TELLER_ID = "teller-alice";
    private static final String VALID_TERMINAL_ID = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Ensure it starts in a clean IDLE state with recent activity timestamp
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Command is created in the 'When' step to allow variation if needed
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Command is created in the 'When' step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Default valid command construction
            if (command == null) {
                command = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(VALID_SESSION_ID, event.aggregateId());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // We simulate the violation by providing a blank tellerId in the command later
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Set last activity to 31 minutes ago to trigger timeout (configured as 30 mins)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Manually force the status to ACTIVE to simulate invalid context (already started)
        aggregate.setStatus(TellerSessionAggregate.SessionStatus.ACTIVE);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // --- Context Setters for Negative Scenarios ---

    // We use simple hooking: if the command is null before execution, we check the context to fail it appropriately.
    // Ideally, we'd pass the intent to the When step, but we can manipulate the aggregate state above.
    
    // To differentiate violations in the 'When' step:
    @Given("prepare unauthenticated command")
    public void prepare_unauthenticated_command() {
        // This is just a marker, the real setup is in the 'Given' violations
        // We create a specific command for the Auth failure
        this.command = new StartSessionCmd(VALID_SESSION_ID, "", VALID_TERMINAL_ID);
    }

    @Given("prepare timeout command")
    public void prepare_timeout_command() {
        this.command = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
    }

    @Given("prepare invalid state command")
    public void prepare_invalid_state_command() {
        this.command = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
    }

    // Additional glue to map generic "Given violation" to specific command setup if needed
    // However, to keep it simple, we can check the aggregate state in the 'When' or just let the Aggregate logic fail.
    // Let's refine the 'When' to handle the specific setup for Auth failure.
}
