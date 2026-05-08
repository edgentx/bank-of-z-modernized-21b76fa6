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

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Test Data Constants
    private static final String SESSION_ID = "TS-123";
    private static final String MENU_A = "MainMenu";
    private static final String MENU_B = "DepositScreen";
    private static final String ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID, Duration.ofMinutes(30));
        aggregate.markAuthenticated(); // Ensure it is in valid state to start
        aggregate.setCurrentMenu(MENU_A);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID, Duration.ofMinutes(30));
        aggregate.markUnauthenticated(); // Violation: Not authenticated
        aggregate.setCurrentMenu(MENU_A);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID, Duration.ofMinutes(30));
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu(MENU_A);
        aggregate.expireSession(); // Violation: Expired
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(SESSION_ID, Duration.ofMinutes(30));
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("SomeOldMenu"); // Internal state is SomeOldMenu
        // We will simulate a command coming from "MainMenu", creating a mismatch
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // SESSION_ID constant is used
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // MENU_A constant is used
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // ACTION constant is used
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Reload to ensure clean state from repo if needed, though we have the ref
            var agg = repository.findById(SESSION_ID).orElseThrow();
            
            // Construct command. For the 'Stale Context' scenario, we intentionally pass a currentMenuId 
            // that differs from the aggregate's internal state to trigger the error.
            String providedCurrentMenu = MENU_A; 
            if (agg.getCurrentMenu() != null && !agg.getCurrentMenu().equals("MainMenu") && agg.getCurrentMenu().equals("SomeOldMenu")) {
                // In the violation scenario, the aggregate is at 'SomeOldMenu' but we pretend the UI sent 'MainMenu'
                providedCurrentMenu = "MainMenu"; 
            } else if (agg.getCurrentMenu() != null) {
                providedCurrentMenu = agg.getCurrentMenu();
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, providedCurrentMenu, MENU_B, ACTION);
            resultEvents = agg.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}