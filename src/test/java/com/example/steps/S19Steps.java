package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    private String sessionId;
    private String menuId;
    private String action;
    
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is initialized in the first Given step
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        var cmd = new NavigateMenuCmd(sessionId, menuId, action);
        try {
            // Re-load to ensure we are testing against a persisted instance if needed,
            // though in-memory testing can use the instance directly.
            var agg = repository.load(sessionId);
            if (agg == null) {
                // For the sake of the tests where we might manipulate the object directly before saving
                agg = aggregate; 
            }
            resultEvents = agg.execute(cmd);
            // Persist state changes
            repository.save(agg);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(false); // Violation
        repository.save(aggregate);
        // Setup valid command defaults
        menuId = "MAIN_MENU";
        action = "SELECT";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        repository.save(aggregate);
        menuId = "MAIN_MENU";
        action = "SELECT";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        sessionId = "session-invalid-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        repository.save(aggregate);
        menuId = "MAIN_MENU";
        action = "INVALID_CONTEXT"; // This triggers the validation error
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}