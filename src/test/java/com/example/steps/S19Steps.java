package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private NavigateMenuCmd cmd;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-1");
        aggregate.setLastActivity(Instant.now());
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do not mark authenticated
        aggregate.setLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-1");
        // Set last activity to 20 minutes ago
        aggregate.setLastActivity(Instant.now().minusSeconds(1200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated("teller-1");
        aggregate.setLastActivity(Instant.now());
        // We will pass an action in the cmd that triggers the violation in the aggregate logic
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by the aggregate creation in the Given steps
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be used in command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be used in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            String action = "VIEW";
            // If we are in the "bad context" scenario, use the action that triggers the error
            if (aggregate.id().equals("session-bad-context")) {
                action = "INVALID_CONTEXT";
            }
            cmd = new NavigateMenuCmd(aggregate.id(), "DEPOSIT_MENU", action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull("Expected events to be emitted", resultEvents);
        assertFalse("Expected at least one event", resultEvents.isEmpty());
        assertTrue("Expected MenuNavigatedEvent", resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("DEPOSIT_MENU", event.menuId());
        assertNull("Expected no exception", capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", capturedException);
        assertTrue("Expected IllegalStateException or Domain Error", capturedException instanceof IllegalStateException);
    }
}
