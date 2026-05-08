package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Initialize the aggregate as if it has started successfully (Authenticated, Active)
        // In a real scenario, we might replay events or load from repo, but here we construct state for testing
        aggregate.setAuthenticated(true);
        aggregate.setLastActivity(Instant.now());
        aggregate.setActive(true);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "session-401";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(false); // Not authenticated
        aggregate.setActive(true);
        aggregate.setLastActivity(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-408";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_context() {
        String sessionId = "session-409";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivity(Instant.now());
        // Set current context to a state that invalidates the requested action
        aggregate.setCurrentScreen("MainMenu"); // Context implies simple state
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by the aggregate creation in the Given steps
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        String sessionId = aggregate.id();
        
        // Default valid params for the positive flow or specific violations
        String targetMenu = "Deposits";
        String action = "ENTER";
        
        // Adjust params for specific violation context if needed (e.g. context mismatch)
        if (aggregate.getCurrentScreen() != null) {
             // If we are in a violation scenario where context matters
             // e.g. trying to go to a submenu not accessible from MainMenu
             targetMenu = "AdminSettings"; 
        }

        Command cmd = new NavigateMenuCmd(sessionId, targetMenu, action);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull("Result events should not be null", resultEvents);
        assertFalse("Result events should not be empty", resultEvents.isEmpty());
        
        DomainEvent event = resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertTrue("Event should be MenuNavigatedEvent", event instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("Deposits", navEvent.targetMenuId());
        
        assertNull("No exception should occur", capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("An exception should have been thrown", capturedException);
        
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue("Should be a domain error", 
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException);
    }
}
