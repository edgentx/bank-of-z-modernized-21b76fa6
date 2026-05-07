package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "session-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(id);
        repository.save(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // No-op, handled by command execution context in real flow
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // No-op, handled by command execution context in real flow
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Scenario setup for failure (e.g. missing auth context)
        String id = "session-unauth-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(id);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // Scenario setup for failure (e.g. stale context)
        String id = "session-timeout-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(id);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        // Scenario setup for failure
        String id = "session-nav-error-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(id);
        repository.save(aggregate);
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Assuming standard test data for success case, violations handled via specific logic inside execute if flags were passed
            // For BDD simplicity, we invoke the command. The aggregate handles specific violations based on internal state or command validity.
            // Since the violation is described in the 'Given', we mock the state or command properties here.
            
            String tellerId = (aggregate.id().contains("unauth")) ? null : "teller-123";
            String terminalId = (aggregate.id().contains("nav-error")) ? null : "term-ABC";
            
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
            List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty());
        Assertions.assertTrue(aggregate.uncommittedEvents().get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // In a real robust test, we might check the message type (IllegalArgumentException, IllegalStateException, etc.)
    }

    @org.junit.platform.suite.api.SelectClasspathResource("features")
    @org.junit.platform.suite.api.Suite
    public static class S18TestSuite {
        // JUnit 5 Suite to run Cucumber
    }
}
