package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Scenarios Setup ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context is handled in the 'When' step via command construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context is handled in the 'When' step via command construction
    }

    // --- Successful Execution ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand(true, "HOME");
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesInactivity() {
        // We use a valid aggregate, but for the purpose of this specific acceptance criteria
        // focusing on the command execution logic:
        // The error 'Sessions must timeout...' usually implies the session IS active but old.
        // However, the command 'StartSessionCmd' usually INITIATES the session.
        // Assuming the requirement implies we cannot start a session if one exists and is stale/timed out,
        // or we are checking the invariant.
        // For the BDD test, we will setup a scenario where we try to execute a command that triggers this check.
        aggregate = new TellerSessionAggregate("session-timeout");
        // Note: The actual logic in the Aggregate handles the timeout check if status is already ACTIVE.
        // Since StartSessionCmd transitions to ACTIVE, if we call it on an ACTIVE session that is old,
        // the aggregate throws the error.
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        repository.save(aggregate);
    }

    // --- Error Handling ---

    @When("the StartSessionCmd command is executed with auth=false")
    public void theStartSessionCmdCommandIsExecutedWithoutAuth() {
        executeCommand(false, "HOME");
    }

    @When("the StartSessionCmd command is executed with invalid navigation")
    public void theStartSessionCmdCommandIsExecutedWithInvalidNav() {
        executeCommand(true, ""); // Blank navigation state
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
                capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Expected a domain logic exception, but got: " + capturedException.getClass().getSimpleName()
        );
    }

    // --- Helper Methods ---

    private void executeCommand(boolean isAuthenticated, String navState) {
        try {
            // Reload from repo to ensure clean state if needed, or use instance var
            if (aggregate == null) {
                aggregate = new TellerSessionAggregate("new-session");
                repository.save(aggregate);
            }
            
            // Ensure we are working with the potentially persisted version
            // For InMemory, this acts as a reload simulation
            aggregate = repository.load(aggregate.id());

            StartSessionCmd cmd = new StartSessionCmd(
                    aggregate.id(),
                    "teller-01",
                    "terminal-01",
                    isAuthenticated,
                    navState
            );

            resultEvents = aggregate.execute(cmd);
            
            // Persist the new state if successful
            if (resultEvents != null && !resultEvents.isEmpty()) {
                repository.save(aggregate);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Mock Repository ---
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final java.util.Map<String, TellerSessionAggregate> store = new java.util.HashMap<>();

        @Override
        public void save(TellerSessionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }

        @Override
        public TellerSessionAggregate load(String sessionId) {
            return store.get(sessionId);
        }
    }
}
