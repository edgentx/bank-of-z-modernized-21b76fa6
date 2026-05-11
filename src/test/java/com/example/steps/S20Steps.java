package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-20.feature")
public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Scenario 1: Successfully execute EndSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        // Manually hydrating to a valid state (simulating history)
        // We are bypassing command execution here to set up the state for the 'Given'
        // In a real repository, we would load the aggregate which applies events.
        aggregate.hydrateForTest("teller-1", Instant.now().minusSeconds(60), "/home/main");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        assertNotNull(aggregate.id());
    }

    // Scenario 2: EndSessionCmd rejected — Not authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        String id = "session-invalid-auth";
        aggregate = new TellerSessionAggregate(id);
        // Setting up a state where teller is null (not authenticated)
        aggregate.hydrateForTest(null, Instant.now().minusSeconds(60), "/home/main");
    }

    // Scenario 3: EndSessionCmd rejected — Session Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        // Creating a session that last had activity 30 minutes ago (assuming timeout is 15m)
        aggregate.hydrateForTest("teller-1", Instant.now().minus(Duration.ofMinutes(30)), "/home/main");
    }

    // Scenario 4: EndSessionCmd rejected — Navigation state mismatch
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        String id = "session-bad-nav";
        aggregate = new TellerSessionAggregate(id);
        // Setting valid auth/time, but invalid nav state
        aggregate.hydrateForTest("teller-1", Instant.now().minusSeconds(60), "/INVALID/CONTEXT");
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("teller.session.ended", event.type());
        
        // Verify aggregate state cleared
        assertTrue(((TellerSessionAggregate) aggregate).isEnded());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect specific exceptions depending on the invariant violated
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
