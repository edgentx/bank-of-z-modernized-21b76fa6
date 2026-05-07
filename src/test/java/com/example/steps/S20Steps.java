package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        repo.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly created in the previous step
        Assertions.assertNotNull(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "TS-UNAUTH-001";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.unauthenticate(); // Force violation
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "TS-TIMEOUT-001";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.expireSession(); // Force violation
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "TS-NAV-001";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.corruptNavigationState(); // Force violation
        repo.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // Persist state changes
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.ended", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
        Assertions.assertFalse(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // In this implementation, we use IllegalStateException (RuntimeException) to model domain rule violations
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
