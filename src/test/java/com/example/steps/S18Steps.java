package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    private static final String VALID_AGGREGATE_ID = "session-123";
    private static final String VALID_TELLER_ID = "teller-01";
    private static final String VALID_TERMINAL_ID = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_AGGREGATE_ID);
        // Ensure authentication for success scenario defaults
        aggregate.markAuthenticated();
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Assumption: Test data constants are valid
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Assumption: Test data constants are valid
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(VALID_AGGREGATE_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents, "Result events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_AGGREGATE_ID);
        // Do NOT mark authenticated. The constructor sets it to false.
        // Boolean.isAuthenticated remains false, triggering the exception.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_AGGREGATE_ID);
        aggregate.markAuthenticated();
        aggregate.markSessionStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate(VALID_AGGREGATE_ID);
        aggregate.markAuthenticated();
        aggregate.markOperationalContextInvalid();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // We check for IllegalStateException as the domain error representation
        Assertions.assertTrue(capturedException instanceof IllegalStateException, 
            "Expected IllegalStateException, but got: " + capturedException.getClass().getSimpleName());
    }
}
