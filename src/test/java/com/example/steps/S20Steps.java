package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    @Autowired
    private TellerSessionRepository repository;

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_TELLER_ID = "teller-001";
    private static final String VALID_STATE_CHECKSUM = "abc123checksum";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = repository.create(VALID_SESSION_ID);
        // Initialize to a valid state manually for the test context
        aggregate.init(VALID_TELLER_ID, VALID_STATE_CHECKSUM);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = repository.create(VALID_SESSION_ID);
        // Do not init; tellerId remains null, active remains false
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = repository.create(VALID_SESSION_ID);
        aggregate.init(VALID_TELLER_ID, VALID_STATE_CHECKSUM);
        aggregate.expireForTesting(); // Force timeout
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = repository.create(VALID_SESSION_ID);
        aggregate.init(VALID_TELLER_ID, VALID_STATE_CHECKSUM);
        aggregate.corruptNavigationState();
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // ID is already set in the @Given steps above
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
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
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect an IllegalStateException thrown by the aggregate logic
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof UnknownCommandException);
    }
}
