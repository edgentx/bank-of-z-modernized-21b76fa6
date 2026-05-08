package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.domain.tellsession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellsession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

@SpringBootTest
public class S19Steps {

    private TellerSession aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario: Successfully execute NavigateMenuCmd

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSession("session-123");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate initialization
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command creation
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DEPOSIT_SCREEN", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("DEPOSIT_SCREEN", event.targetMenuId());
        Assertions.assertEquals("session-123", event.aggregateId());
    }

    // Scenario: NavigateMenuCmd rejected — Authenticated

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSession("unauth-session");
        // Not calling markAuthenticated() leaves it false
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertTrue(thrownException.getMessage().contains("authenticated"));
    }

    // Scenario: NavigateMenuCmd rejected — Timeout

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSession("timedout-session");
        aggregate.markAuthenticated();
        // Set last activity to 2 hours ago
        aggregate.setLastActivityAt(Instant.now().minusSeconds(7200));
    }

    // Reuse @When("the NavigateMenuCmd command is executed")
    // Reuse @Then("the command is rejected with a domain error")

    // Scenario: NavigateMenuCmd rejected — Navigation State

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSession("stuck-session");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenuId("DEPOSIT_SCREEN");
    }

    // Override When for this specific scenario logic
    @When("the NavigateMenuCmd command is executed targeting the current menu")
    public void theNavigateMenuCmdCommandIsExecutedWithSameMenu() {
        try {
            // Trying to navigate to where we already are
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DEPOSIT_SCREEN", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
