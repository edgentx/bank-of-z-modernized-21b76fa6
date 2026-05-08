package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private Exception thrownException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // By default, we set it up as valid (authenticated, etc.) for the happy path
        aggregate.markAuthenticated("teller-42"); 
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // markInactive sets authenticated = false
        aggregate.markInactive();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-1");
        // Simulate that the last activity was way in the past
        aggregate.simulateTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-context-fail");
        aggregate.markAuthenticated("teller-2");
        // Invalidate the context state
        aggregate.invalidateContext();
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by aggregate creation in 'Given' steps
        // Kept for Gherkin readability
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in 'When' step construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in 'When' step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        DomainEvent event = resultEvents.iterator().next();
        assertTrue(event instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", ((MenuNavigatedEvent) event).menuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We typically expect IllegalStateException for domain invariants
        assertTrue(thrownException instanceof IllegalStateException);
    }
}