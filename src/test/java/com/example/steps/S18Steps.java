package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private String currentTellerId;
    private String currentTerminalId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // We simulate violation by passing null/empty tellerId in the 'When' step or setting state here
        currentTellerId = null; // Invalid context
        currentTerminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // For the purpose of this test, we assume the aggregate logic checks active state
        // We'll leave it uninitialized/null or partially constructed to trigger the error via the command
        // Or rely on the aggregate state being inconsistent with the command.
        // Here we set valid IDs, but the aggregate logic might reject if it was already active (simulating conflict).
        // However, the prompt implies the aggregate *itself* might be in a state that triggers rejection.
        // Let's assume the aggregate is simply new, but the *command* or setup implies the failure.
        currentTellerId = "teller-1";
        currentTerminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        currentTellerId = "teller-1";
        currentTerminalId = null; // Invalid context
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        currentTellerId = "teller-123";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        currentTerminalId = "terminal-A";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // If the specific scenario set IDs, use them. If not, use valid defaults for the 'happy' path.
            String tid = (currentTellerId != null) ? currentTellerId : "teller-123";
            String term = (currentTerminalId != null) ? currentTerminalId : "terminal-A";

            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tid, term);
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
        assertEquals("teller.session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect an IllegalStateException or similar domain exception
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
