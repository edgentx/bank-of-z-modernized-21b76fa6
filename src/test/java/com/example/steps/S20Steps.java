package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "TS-12345";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup a valid authenticated state
        aggregate.markAuthenticated("TELLER-01");
        aggregate.setLastActivityAt(Instant.now());
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        assertNotNull(this.sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(this.sessionId);
            // Reload aggregate from repo to ensure clean state for execution context
            var agg = repository.findById(sessionId).orElseThrow();
            this.resultEvents = agg.execute(cmd);
            // Save changes
            repository.save(agg);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "TS-FAIL-AUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly keep active false or unauthenticated
        aggregate.setActive(false);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "TS-FAIL-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        // Set last activity to 31 minutes ago (configured timeout is 30m)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "TS-FAIL-NAV";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        // Set screen to a state that disallows termination
        aggregate.setCurrentScreen("TRANSACTION_IN_PROGRESS");
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect IllegalStateException or a custom DomainException
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
