package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "TS-" + UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "TELLER-001";
        this.terminalId = "TERM-3270-01";
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "TELLER-001";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "TERM-3270-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(this.sessionId, this.tellerId, this.terminalId, Instant.now().plusSeconds(3600));
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(this.resultEvents);
        Assertions.assertEquals(1, this.resultEvents.size());
        Assertions.assertTrue(this.resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) this.resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create aggregate in a state that assumes authentication check fails
        // For this command, the validation happens inside execute.
        this.sessionId = "TS-FAIL-AUTH";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = null; // Invalid
        this.terminalId = "TERM-3270-01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "TS-FAIL-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "TELLER-001";
        this.terminalId = "TERM-3270-01";
        // The validation logic handles the 'configured period', here we might pass a bad timeout
        // or the command validates the timeout. In this pattern, the command carries the timeout.
        // If the logic implies the AGGREGATE checks the timeout based on a configured policy,
        // we might need to construct the aggregate differently. 
        // Based on S-18 spec, we pass the timeout in the command. The violation here simulates passing a bad timeout.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        this.sessionId = "TS-FAIL-NAV";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "TELLER-001";
        this.terminalId = null; // Invalid nav context (terminalId is part of nav context)
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(this.caughtException);
        Assertions.assertTrue(
            this.caughtException instanceof IllegalStateException || 
            this.caughtException instanceof IllegalArgumentException
        );
    }

    // Helper class to simulate the specific timeout violation scenario for the 'When' clause
    // We overload the steps slightly to handle the specific violation data injection
    public static class Context {
        public static Instant invalidTimeout;
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_StartSessionCmd_command_is_executed_with_invalid_timeout() {
        try {
            // Using a timeout in the past to trigger validation error
            Command cmd = new StartSessionCmd(this.sessionId, this.tellerId, this.terminalId, Instant.now().minusSeconds(10));
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
}
