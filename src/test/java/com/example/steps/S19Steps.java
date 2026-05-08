package com.example.steps;

import com.example.domain.shared.Command;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Shared state for commands
    private String currentSessionId;
    private String currentMenuId;
    private String currentAction;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        currentSessionId = "session-123";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-456"); // Ensure valid state
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aValidTellerSessionAggregate
        assertNotNull(currentSessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        currentMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        currentAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(currentSessionId, currentMenuId, currentAction);
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(currentMenuId, event.menuId());
        assertEquals(currentAction, event.action());
    }

    // --- Scenarios for Rejection ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        currentSessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Intentionally NOT calling markAuthenticated
        currentMenuId = "MAIN_MENU";
        currentAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        currentSessionId = "session-timeout-fail";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-456");
        aggregate.markExpired(); // Force timeout
        currentMenuId = "MAIN_MENU";
        currentAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        currentSessionId = "session-context-fail";
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-456");
        
        // Set current menu to same as target
        String targetMenu = "MAIN_MENU";
        aggregate.setCurrentMenu(targetMenu);
        
        currentMenuId = targetMenu;
        currentAction = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
