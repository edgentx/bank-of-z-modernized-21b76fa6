package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import com.example.domain.tellsession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private String sessionId;
    private String menuId;
    private String action;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure it is valid for success case
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set in the previous step
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            var cmd = new NavigateMenuCmd(sessionId, menuId, action);
            var events = aggregate.execute(cmd);
            // We do not apply events to state in this simple aggregate, it's done in execute.
            // But typically we might reload from repo. Here we just verify return value.
            assertFalse(events.isEmpty(), "Expected at least one event");
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        // Verify internal state updated by execute logic
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected uncommitted events");
        assertEquals("menu.navigated", events.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "session-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // 'authenticated' defaults to false in constructor
        this.menuId = "ANY_MENU";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Auth is fine
        aggregate.expireSession(); // Make it stale
        this.menuId = "ANY_MENU";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.sessionId = "session-context-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        String currentCtx = "CURRENT_MENU";
        aggregate.setContext(currentCtx);
        
        this.menuId = currentCtx; // Trying to navigate to where we already are
        this.action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
