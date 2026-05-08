package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> result;
    private Exception error;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();

    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            return aggregate;
        }
        @Override
        public TellerSessionAggregate create(String id) {
            return new TellerSessionAggregate(id);
        }
        @Override
        public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.empty();
        }
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = repo.create("session-123");
        aggregate.markAuthenticated(); // Assume auth step passed
        aggregate.setNavigationContext("LOGIN");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Data setup handled in the When step construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Data setup handled in the When step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "term-1");
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            error = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(error, "Should not have thrown an error");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) result.get(0);
        assertEquals("session.started", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = repo.create("session-invalid-auth");
        // aggregate.markAuthenticated() is NOT called
        aggregate.setNavigationContext("LOGIN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = repo.create("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markTimedOut(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        aggregate = repo.create("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.setNavigationContext("TRANS_FLOW"); // Wrong context
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(error, "Expected a domain error but none was thrown");
        assertTrue(error instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
