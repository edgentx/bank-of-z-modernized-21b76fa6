package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
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
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();

    // Scenario: Successfully execute NavigateMenuCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = repository.create();
        aggregate.markAuthenticated("teller-123"); // Ensure authenticated
        aggregate.setLastActivityAt(Instant.now()); // Ensure active
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly created by the aggregate constructor in aValidTellerSessionAggregate
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // The command will have the valid menuId
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // The command will have the valid action
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    // Scenario: NavigateMenuCmd rejected — Authentication
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = repository.create();
        aggregate.markUnauthenticated(); // Violate invariant
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("authenticated"));
    }

    // Scenario: NavigateMenuCmd rejected — Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = repository.create();
        aggregate.markAuthenticated("teller-123"); // Must be authenticated first
        // Set last activity to 31 minutes ago (timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(31 * 60));
    }

    // Scenario: NavigateMenuCmd rejected — Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = repository.create();
        aggregate.markAuthenticated("teller-123");
    }

    // Override When for state violation scenario to pass invalid data
    @When("the NavigateMenuCmd command is executed with invalid state")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidState() {
        try {
            // Pass null menuId to violate the invariant
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), null, "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
