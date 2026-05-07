package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.model.TellerSessionEndedEvent;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S20Steps {

    // An in-memory repository would be injected here, but for simplicity we manage state in the step definition
    // backed by the InMemoryTellerSessionRepository implementation if it existed.
    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure it is in a valid, started state
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Valid sessionId is implicitly "session-123" from the previous step
        // This step satisfies the Gherkin requirement but relies on context setup.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create an aggregate but DO NOT authenticate it
        aggregate = new TellerSessionAggregate("session-unauth-456");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatHasTimedOut() {
        aggregate = new TellerSessionAggregate("session-timeout-789");
        aggregate.markAuthenticated();
        aggregate.markInactive(); // Force the timestamp to the past
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateWithCorruptNavigation() {
        aggregate = new TellerSessionAggregate("session-nav-err-999");
        aggregate.markAuthenticated();
        aggregate.corruptNavigationState();
        repository.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            // Reload aggregate from repository to simulate persistence boundary
            TellerSessionAggregate agg = repository.findById(aggregate.id()).orElseThrow();
            Command cmd = new EndSessionCmd(agg.id());
            resultEvents = agg.execute(cmd);
            repository.save(agg); // Save changes if successful
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a domain exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }

    // --- In-Memory Repository Stub for Testing ---

    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final java.util.Map<String, TellerSessionAggregate> store = new java.util.HashMap<>();

        @Override
        public void save(TellerSessionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }

        @Override
        public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.ofNullable(store.get(id));
        }
    }
}
