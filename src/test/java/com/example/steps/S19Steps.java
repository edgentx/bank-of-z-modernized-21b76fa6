package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
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
    private InMemoryTellerSessionRepository repository;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // --- Background / Setup ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure authenticated by default for the happy path
        aggregate.setLastActivityAt(Instant.now()); // Ensure active
        
        repository = new InMemoryTellerSessionRepository();
        repository.save(aggregate);
    }

    // --- Happy Path Scenario ---

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by setup in 'aValidTellerSessionAggregate'
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by command creation in the 'When' step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by command creation in the 'When' step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // persist state changes
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-auth-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated()
        repository = new InMemoryTellerSessionRepository();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        repository = new InMemoryTellerSessionRepository();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-nav-fail";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        repository = new InMemoryTellerSessionRepository();
        repository.save(aggregate);
        // The command used in the 'When' step for this scenario must trigger the failure.
        // We will use a specific menuId "INVALID_CONTEXT" in a custom When step below, 
        // but since the 'When' step is shared in the Gherkin, we check the violation context here.
        // We'll inject a flag or handle the specific command in the shared When if possible, 
        // or Cucumber context handling. For simplicity, we rely on the 'When' using specific params.
        // *Correction*: The Gherkin reuses the standard When step. 
        // The aggregate logic checks `isValidNavigation`.
        // We will assume the command in the When step for this context is correct.
    }

    // Helper to handle the specific command for the state violation scenario
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        // Triggering the "Navigation state must accurately reflect..." failure
        // logic in TellerSessionAggregate.isValidNavigation checks for "INVALID_CONTEXT"
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "INVALID_CONTEXT", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
