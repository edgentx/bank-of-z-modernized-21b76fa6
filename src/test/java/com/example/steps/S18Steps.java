package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

@Suite
@IncludeEngages("cucumber")
@SelectClasspathResource("features/S-18.feature")
public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Throwable thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context: The command construction will use valid IDs
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context: The command construction will use valid IDs
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd("SESSION-1", "TELLER-1", "TERM-1", Instant.now().plusSeconds(300));
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("SESSION-1", event.aggregateId());
        assertEquals("TELLER-1", event.tellerId());
        assertEquals("TERM-1", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate("SESSION-2");
        // No explicit authentication state setup needed as command must carry auth token/flag
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("SESSION-3");
        // In a real scenario, this might imply passing an expired timeout configuration or an expired auth token
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("SESSION-4");
        // Invalid context state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}