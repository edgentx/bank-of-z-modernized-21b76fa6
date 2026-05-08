package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // In-memory mock repository pattern
    private final TellerSessionRepository repo = new TellerSessionRepository() {
        @Override
        public void save(TellerSessionAggregate aggregate) {
            // No-op for in-memory test
        }

        @Override
        public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.ofNullable(aggregate);
        }
    };

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid authenticated state
        aggregate.markAuthenticated("teller-01");
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate setup
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context variable handled in 'When' step execution
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context variable handled in 'When' step execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNT_INQUIRY", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNT_INQUIRY", event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setUnauthenticated(); // Explicitly violate auth
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01");
        // Set activity to 20 minutes ago (Default timeout is 15m)
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-bad-state";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01");
        aggregate.setCurrentMenu("SOME_INTERMEDIATE_MENU"); // Not Main Menu
        // Logic in Aggregate prevents jumping to CASH_WITHDRAWAL from non-main
    }

    // When clause is reused for negative flows, but we need to inject the specific invalid command context for the state violation
    @When("the NavigateMenuCmd command is executed with invalid context jump")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidJump() {
        // Trying to jump to a restricted screen from wrong context
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "CASH_WITHDRAWAL", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Standard When for Auth and Timeout
    @When("the NavigateMenuCmd command is executed with valid params")
    public void theNavigateMenuCmdCommandIsExecutedWithValidParams() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DASHBOARD", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

}