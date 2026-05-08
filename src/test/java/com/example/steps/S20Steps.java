package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellerm_session.model.EndSessionCmd;
import com.example.domain.tellerm_session.model.TellerSessionAggregate;
import com.example.domain.tellerm_session.model.TellerSessionEndedEvent;
import com.example.domain.tellerm_session.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated, active, valid nav state, not timed out
        aggregate.markAuthenticated("teller-456");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally do NOT call markAuthenticated. State defaults to unauthenticated.
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatIsTimedOut() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-456");
        aggregate.expireSession(); // Simulate timeout
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateWithInvalidNavState() {
        String sessionId = "session-nav-bad";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-456");
        aggregate.invalidateNavigationState(); // Simulate bad nav state
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The sessionId is implicitly part of the aggregate instance setup in the Given steps.
        // We simply ensure the aggregate instance is not null here.
        assertNotNull(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Save state after execution
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);

        TellerSessionEndedEvent event = (TellerSessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We verify it's an IllegalStateException or similar domain error, not a generic NPE.
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
