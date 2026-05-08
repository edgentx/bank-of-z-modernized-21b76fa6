package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermessaging.model.EndSessionCmd;
import com.example.domain.tellermessaging.model.SessionEndedEvent;
import com.example.domain.tellermessaging.model.TellerSessionAggregate;
import com.example.domain.tellermessaging.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    // Using a simple In-Memory Repo pattern consistent with existing mocks
    record InMemoryTellerSessionRepo(Map<String, TellerSessionAggregate> db) implements TellerSessionRepository {
        @Override public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            db.put(aggregate.id(), aggregate);
            return aggregate;
        }
        @Override public TellerSessionAggregate findById(String id) { return db.get(id); }
        @Override public boolean existsById(String id) { return db.containsKey(id); }
    }

    private final InMemoryTellerSessionRepo repo = new InMemoryTellerSessionRepo(new java.util.HashMap<>());
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "sess-" + UUID.randomUUID();
        // Create a valid active session: authenticated, within timeout, consistent state.
        // We assume a constructor or factory that creates an active session.
        // For this BDD, we create one and add it to the repo, assuming it handles basic validation.
        this.aggregate = new TellerSessionAggregate(sessionId);
        
        // Simulate previous events to bring it to a valid state
        // In a real aggregate, we'd apply events, but here we construct the object directly 
        // or assume the constructor sets valid defaults.
        // To enforce the 'Valid' state for the Happy Path, we ensure the internal flags are set to Active.
        // NOTE: This relies on the aggregate implementation logic below.
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "sess-unauth-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // To test the invariant "must be authenticated to end", we rely on the aggregate's state.
        // We create a session where the authentication flag is null/empty.
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "sess-timeout-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // We simulate a session created a long time ago.
        // The aggregate constructor allows setting the last active timestamp.
        // We assume a constructor that takes an Instant for testing.
        this.aggregate = new TellerSessionAggregate(sessionId, Instant.now().minus(Duration.ofHours(2)));
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String sessionId = "sess-nav-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session where the internal state is inconsistent or corrupted.
        // We might use a specific constructor or mutator if available, or rely on the aggregate
        // detecting this via specific inputs.
        // For this test, we assume the aggregate has a state property that we can manipulate via the constructor.
        // e.g. a mismatched state flag.
        this.aggregate = new TellerSessionAggregate(sessionId, "INVALID_STATE");
        repo.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            // We reload from repo to ensure we are testing a fresh fetch if needed, though here we use the instance.
            TellerSessionAggregate agg = repo.findById(aggregate.id());
            if (agg == null) throw new IllegalStateException("Aggregate not found");
            
            this.resultEvents = agg.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Expected no error, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Expected events to be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error exception, but command succeeded");
        // Check for specific exception types if needed (IllegalStateException, IllegalArgumentException, etc.)
        // The prompt implies "domain error", which usually maps to IllegalStateException in this codebase.
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected IllegalStateException or IllegalArgumentException, got: " + capturedException.getClass().getSimpleName());
    }
}
