package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellerm_session.model.EndSessionCmd;
import com.example.domain.tellerm_session.model.SessionEndedEvent;
import com.example.domain.tellerm_session.model.TellerSessionAggregate;
import com.example.domain.tellerm_session.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = "session-123";
        // Create a new aggregate instance. In a real scenario, we might hydrate it from events.
        // For testing, we assume the constructor allows creating a valid session context.
        // Or we hydrate it using the repository.
        this.aggregate = repository.findById(id); 
        // Hydrate manually for test setup to ensure it's valid
        aggregate.hydrateForTest(id, "teller-1", Instant.now(), true, "HOME_SCREEN");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The aggregate already has the ID, so the command just needs to match.
        Assertions.assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.ended", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String id = "session-unauth";
        this.aggregate = repository.findById(id);
        // Setup: Authenticated = false
        aggregate.hydrateForTest(id, "teller-2", Instant.now(), false, "HOME_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = "session-timeout";
        this.aggregate = repository.findById(id);
        // Setup: LastActivity is in the past (e.g., 31 minutes ago)
        aggregate.hydrateForTest(id, "teller-3", Instant.now().minus(Duration.ofMinutes(31)), true, "HOME_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String id = "session-nav-error";
        this.aggregate = repository.findById(id);
        // Setup: State indicates an invalid context (e.g., PENDING_TRANSACTION when it should be IDLE)
        // We define this contextually in the aggregate logic.
        aggregate.hydrateForTest(id, "teller-4", Instant.now(), true, "INVALID_CONTEXT");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain error exception");
        // It could be IllegalStateException, IllegalArgumentException, or a custom DomainException
        // We check it's not UnknownCommandException (which would imply code wiring issue)
        Assertions.assertFalse(capturedException instanceof UnknownCommandException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // --- Mocks for testing ---
    
    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate findById(String id) {
            return new TellerSessionAggregate(id);
        }
        // Other stubs not needed for this scenario
        @Override public void save(TellerSessionAggregate aggregate) {}
    }
}
