package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Common valid data
    private static final String VALID_ID = "session-123";
    private static final String VALID_TELLER = "teller-01";
    private static final String VALID_TERMINAL = "term-05";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        repository.save(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in the 'When' step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in the 'When' step construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        repository.save(aggregate);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        executeCommand(true, Instant.now().toEpochMilli(), "IDLE");
    }

    @When("the StartSessionCmd command is executed with missing auth")
    public void the_start_session_cmd_command_is_executed_without_auth() {
        executeCommand(false, Instant.now().toEpochMilli(), "IDLE");
    }

    @When("the StartSessionCmd command is executed with stale timestamp")
    public void the_start_session_cmd_command_is_executed_with_stale_timestamp() {
        // 20 minutes ago
        long past = Instant.now().minus(20, java.time.temporal.ChronoUnit.MINUTES).toEpochMilli();
        executeCommand(true, past, "IDLE");
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void the_start_session_cmd_command_is_executed_with_invalid_nav_state() {
        executeCommand(true, Instant.now().toEpochMilli(), "TRANSACTION_IN_PROGRESS");
    }

    private void executeCommand(boolean isAuthenticated, long timestamp, String navState) {
        // Reload from repo to ensure we are testing the aggregate instance state if needed, though here we just use the field
        // In a real scenario, we might fetch. Here 'aggregate' is the specific instance under test.
        // Ensure we have the aggregate instance created in Given steps
        if (aggregate == null) {
            aggregate = new TellerSessionAggregate(VALID_ID);
        }

        StartSessionCmd cmd = new StartSessionCmd(
            VALID_ID,
            VALID_TELLER,
            VALID_TERMINAL,
            isAuthenticated,
            timestamp,
            navState
        );

        try {
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
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Check for the specific message or type based on the violation
        // This covers all rejection scenarios in the feature file
    }

    // Helper to route When steps to specific methods if Cucumber doesn't match exact string
    // The Cucumber engine matches the regex. If the text matches multiple regex, it might pick the first.
    // However, standard Cucumber practice relies on distinct text.
    // The feature file reuses "When the StartSessionCmd command is executed" text.
    // We need to differentiate.

    // To handle the generic "When" text for multiple scenarios with different Given states,
    // we will use a single When method and inspect the aggregate state or specific flags.
    // However, cleaner way in Cucumber is to assume the context from Given.
    // But the violations are in the COMMAND payload, not the aggregate state (since it's a new session start).
    // So I need to match the specific text in the feature file.
    
    // Re-mapping the When steps:
    
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setup_auth_violation() { this.scenarioType = ScenarioType.AUTH_FAIL; aggregate = new TellerSessionAggregate(VALID_ID); }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setup_timeout_violation() { this.scenarioType = ScenarioType.TIMEOUT_FAIL; aggregate = new TellerSessionAggregate(VALID_ID); }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setup_nav_violation() { this.scenarioType = ScenarioType.NAV_FAIL; aggregate = new TellerSessionAggregate(VALID_ID); }

    private enum ScenarioType { SUCCESS, AUTH_FAIL, TIMEOUT_FAIL, NAV_FAIL }
    private ScenarioType scenarioType = ScenarioType.SUCCESS;

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_generic() {
        switch (scenarioType) {
            case SUCCESS -> the_start_session_cmd_command_is_executed();
            case AUTH_FAIL -> the_start_session_cmd_command_is_executed_without_auth();
            case TIMEOUT_FAIL -> the_start_session_cmd_command_is_executed_with_stale_timestamp();
            case NAV_FAIL -> the_start_session_cmd_command_is_executed_with_invalid_nav_state();
        }
    }

    @Given("a valid TellerSession aggregate")
    public void setup_success() {
        this.scenarioType = ScenarioType.SUCCESS;
        aggregate = new TellerSessionAggregate(VALID_ID);
    }
}
