package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Simulate auth via internal API or test helper
        aggregate.updateLastActivity(Instant.now().toEpochMilli());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId already initialized in aggregate constructor
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "MENU_MAIN_01";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated
        aggregate.updateLastActivity(Instant.now().toEpochMilli());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set activity to well over 30 minutes ago (e.g., 2 hours)
        long twoHoursAgo = Instant.now().minusSeconds(7200).toEpochMilli();
        aggregate.updateLastActivity(twoHoursAgo);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.updateLastActivity(Instant.now().toEpochMilli());
        // Simulate invalid state: requesting a transaction menu while in maintenance mode
        // For this test, we pass a menuId that logic will reject
        this.menuId = "TX_MENU_FORBIDDEN";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            if (this.menuId == null) {
                this.menuId = "SOME_MENU"; // Default if not set in violation context
            }
            if (this.action == null) {
                this.action = "SELECT";
            }
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultingEvents, "Events list should not be null");
        Assertions.assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected exception but command succeeded");
        // Depending on implementation, it could be IllegalStateException, IllegalArgumentException, or a custom DomainError
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
