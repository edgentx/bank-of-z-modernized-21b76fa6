package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // In-memory repository implementation stub for test context
    private final TellerSessionRepository repo = new TellerSessionRepository() {
        @Override
        public Optional<TellerSessionAggregate> findById(String id) {
            // Not used for these unit-level step definitions, aggregate is constructed directly
            return Optional.empty();
        }

        @Override
        public void save(TellerSessionAggregate aggregate) {
            // No-op for test
        }
    };

    // Setup: Valid Aggregate
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.setAuthenticated(true);
        aggregate.setOperationalContext("BRANCH_MAIN");
        aggregate.setLastActivityAt(Instant.now()); // Active
    }

    // Setup: Violations
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        // Set last activity to 31 minutes ago (Timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(java.time.Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate("session-context");
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        // Context is DRIVE_THRU, but we will try to navigate to a BRANCH menu
        aggregate.setOperationalContext("DRIVE_THRU");
    }

    // Inputs
    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is implicitly handled by the aggregate instance ID in these tests
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When step construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step construction
    }

    // Action
    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Determine menuId based on context for the negative test case
            String targetMenu = "BRANCH_MAIN_DEPOSIT";
            
            // If we are testing the Context Violation (Drive Thru trying to access Branch)
            // We specifically check a known context mismatch if possible, or just let the logic fail
            // if the aggregate state implies it.
            // However, to keep steps generic, we assume the command parameters come from the context.
            // For the specific violation case, we pass a menu ID that conflicts.
            if ("session-context".equals(aggregate.id())) {
                 targetMenu = "BRANCH_ADMIN"; // Starts with BRANCH, context is DRIVE_THRU
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    // Outcomes
    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertFalse(resultEvents.isEmpty(), "Expected list of events not to be empty");
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
