package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.InvalidTellerSessionStateException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        // Create a valid authenticated session
        this.aggregate = new TellerSessionAggregate(this.sessionId, "teller-1", Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the aggregate initialization
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the command construction later
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the command construction later
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        // Create an aggregate where the teller is NOT authenticated
        this.sessionId = "session-invalid-auth";
        this.aggregate = new TellerSessionAggregate(this.sessionId, null, Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        // Create a session that was active a long time ago (e.g., 31 minutes)
        Instant lastActiveTime = Instant.now().minus(Duration.ofMinutes(31));
        this.aggregate = new TellerSessionAggregate(this.sessionId, "teller-1", lastActiveTime);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        this.sessionId = "session-bad-context";
        this.aggregate = new TellerSessionAggregate(this.sessionId, "teller-1", Instant.now());
        // Manually corrupt the internal state to simulate an operational context mismatch
        // For this test, we assume 'MENU_MAIN' is the only valid context after login.
        aggregate.simulateStateCorruption("INVALID_CORRUPT_STATE");
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, "MENU_DEPOSIT", "ENTER");
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (UnknownCommandException | IllegalStateException | IllegalArgumentException e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("MENU_DEPOSIT", event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof InvalidTellerSessionStateException,
            "Expected domain error exception"
        );
    }
}
