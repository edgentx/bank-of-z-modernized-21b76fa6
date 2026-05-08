package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private static final String SESSION_ID = "session-123";
    private static final String TELLER_ID = "teller-001";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(TELLER_ID);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Do not authenticate
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(TELLER_ID);
        aggregate.deactivate(); // Force inactive state
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(TELLER_ID);
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by constant usage
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Contextual, used in command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Contextual, used in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Scenario 1 & 4 inputs
            String menuId = "MAIN_MENU";
            String action = "DISPLAY";

            // Scenario 4 specific: Context violation implies bad input state in this simulation
            // Or aggregate state. We'll simulate bad input via empty check in aggregate.
            // If the scenario is specifically about the aggregate state being invalid for the navigation,
            // the aggregate handles it. Here we just send a command.
            
            // Load from repo to simulate persistence boundary
            TellerSessionAggregate loadedAggregate = repository.load(SESSION_ID);
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, menuId, action);
            resultEvents = loadedAggregate.execute(cmd);
            repository.save(loadedAggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.getMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Checking for IllegalStateException or IllegalArgumentException as domain error signals
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
