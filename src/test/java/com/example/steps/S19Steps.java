package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private java.util.List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated(); // Ensure valid state for base scenario
        this.aggregate.setCurrentMenu("DASHBOARD");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId initialized in previous step
        assertNotNull(this.sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(this.menuId, event.menuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Do not mark authenticated
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.setTimeoutThreshold(java.time.Duration.ofMinutes(30));
        // Set last activity to 31 minutes ago
        this.aggregate.setLastActivityAt(java.time.Instant.now().minus(java.time.Duration.ofMinutes(31)));
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-bad-nav";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.setCurrentMenu("LOGIN"); // Context violation logic defined in Aggregate
        this.menuId = "TRANSACTION"; // Trying to jump to transaction from login
        this.action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
