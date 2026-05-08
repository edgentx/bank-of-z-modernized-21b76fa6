package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {
    private TellerSession aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private UUID sessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSession(sessionId);
        aggregate.initializeValidSession("teller-123");
        repo.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // SessionId initialized in previous step
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(sessionId);
        try {
            // Reload aggregate to ensure clean state from repo
            var agg = repo.findById(sessionId).orElseThrow();
            resultEvents = agg.execute(cmd);
            repo.save(agg); // Persist state changes
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not throw exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(sessionId.toString(), event.aggregateId());
    }

    // Scenarios for Rejections

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSession(sessionId);
        aggregate.initializeValidSession("teller-123");
        aggregate.setAuthenticated(false); // Violate invariant
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSession(sessionId);
        aggregate.initializeValidSession("teller-123");
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1800)); // 30 mins ago (> 15 min timeout)
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        sessionId = UUID.randomUUID();
        aggregate = new TellerSession(sessionId);
        aggregate.initializeValidSession("teller-123");
        aggregate.setOperationalContextValid(false); // Violate invariant
        repo.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Exception should be thrown for invariant violation");
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
