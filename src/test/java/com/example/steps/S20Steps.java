package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = UUID.randomUUID().toString();
        TellerSession session = new TellerSession(sessionId, "teller001", Instant.now(), Duration.ofHours(1), true);
        this.aggregate = new TellerSessionAggregate(session);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // SessionId is implicit in the aggregate construction, but we verify existence.
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Violation: Authenticated flag is false
        String sessionId = UUID.randomUUID().toString();
        TellerSession session = new TellerSession(sessionId, "teller001", Instant.now(), Duration.ofHours(1), false);
        this.aggregate = new TellerSessionAggregate(session);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Violation: Last activity time is far in the past
        String sessionId = UUID.randomUUID().toString();
        // Instant created with a very old timestamp to simulate timeout
        Instant oldTime = Instant.now().minus(Duration.ofHours(24));
        // Constructor sets state to ACTIVE implying it should be active, but the timestamp is old.
        // The aggregate logic will detect the staleness.
        TellerSession session = new TellerSession(sessionId, "teller001", oldTime, Duration.ofHours(1), true);
        this.aggregate = new TellerSessionAggregate(session);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // We create a valid aggregate, then we will try to end it when it's already ended
        // or put it in a state where ending is invalid.
        // However, the violation text suggests the state is invalid. Let's simulate a session that is already ENDED.
        String sessionId = UUID.randomUUID().toString();
        TellerSession session = new TellerSession(sessionId, "teller001", Instant.now(), Duration.ofHours(1), true);
        this.aggregate = new TellerSessionAggregate(session);
        // Execute a valid end first to put it in ENDED state
        aggregate.execute(new EndSessionCmd(sessionId));
        // Now the aggregate is in ENDED state. Trying to end it again violates the state validity.
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
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
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception, but command succeeded.");
        // We accept IllegalStateException or IllegalArgumentException as domain errors in this simple model
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
