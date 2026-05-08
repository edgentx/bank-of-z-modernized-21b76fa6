package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private String sessionId;
    private String menuId;
    private String action;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated(); // Ensure valid state
        this.menuId = "MAIN_MENU";
        this.action = "VIEW";
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        assertNotNull(menuId);
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        assertNotNull(action);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "sess-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // aggregate.markAuthenticated() is NOT called
        this.menuId = "MAIN_MENU";
        this.action = "VIEW";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated(); // Auth is valid
        this.aggregate.markSessionExpired();  // But time has run out
        this.menuId = "MAIN_MENU";
        this.action = "VIEW";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "sess-inconsistent";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.markNavigationInconsistent(); // Context mismatch
        this.menuId = "MAIN_MENU";
        this.action = "VIEW";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}