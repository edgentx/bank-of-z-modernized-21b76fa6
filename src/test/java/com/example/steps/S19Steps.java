package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Session 1: Standard execution
    private String sessionId = "session-123";
    private String menuId = "MAIN_MENU";
    private String action = "SELECT";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate to a valid state that can navigate
        aggregate.hydrateForNavigation("WELCOME_SCREEN");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId initialized in field
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
        // Reload from repository to ensure clean state if necessary, though in-memory ref is fine here
        var agg = repository.load(sessionId);
        var cmd = new NavigateMenuCmd(sessionId, menuId, action);
        
        try {
            resultEvents = agg.execute(cmd);
            // Save changes (events applied inside execute, but versioning updated here)
            repository.save(agg);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("bad-auth-session");
        // 'authenticated' defaults to false in constructor
        repository.save(aggregate);
        sessionId = "bad-auth-session";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("timed-out-session");
        aggregate.markAuthenticated(); // Make auth valid
        aggregate.hydrateForNavigation("MAIN_MENU"); // Make state valid
        // Set last activity to 2 hours ago
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
        aggregate.setTimeoutDuration(Duration.ofMinutes(30)); // Ensure 30 min timeout
        repository.save(aggregate);
        sessionId = "timed-out-session";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("lost-state-session");
        aggregate.markAuthenticated(); // Auth valid
        aggregate.setLastActivityAt(Instant.now()); // Time valid
        // Do NOT set currentMenu. It remains null, violating the context check.
        repository.save(aggregate);
        sessionId = "lost-state-session";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
