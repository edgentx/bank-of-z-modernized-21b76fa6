package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.MenuNavigatedEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import com.example.domain.tellsession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Seed with valid state to simulate an active, authenticated session
        // Simulate SessionStartedEvent state changes internally for the test sake
        aggregate.injectStateForTest("teller-101", Instant.now(), "MAIN_MENU");
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate creation in the previous step
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the When step via command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the When step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DEPOSIT_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("DEPOSIT_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // State implies no authenticated teller ID
        aggregate.injectStateForTest(null, Instant.now(), "LOGIN_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // State implies last activity was 30 minutes ago
        aggregate.injectStateForTest("teller-101", Instant.now().minus(Duration.ofMinutes(30)), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        String sessionId = "session-bad-context";
        aggregate = new TellerSessionAggregate(sessionId);
        // State implies we are trying to navigate from an invalid screen
        aggregate.injectStateForTest("teller-101", Instant.now(), null);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
