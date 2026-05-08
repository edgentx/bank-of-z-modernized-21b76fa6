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
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Simulate a fully initialized, authenticated, valid session
        aggregate = repository.save(new TellerSessionAggregate("session-123", "teller-1"));
        aggregate.markAuthenticated(); // Helper assumed for testing valid state
        aggregate.keepAlive(); // Reset timeout
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate initialization
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command execution
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command execution
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create a session that is NOT authenticated
        aggregate = new TellerSessionAggregate("session-unauth", "teller-2");
        // Do NOT call markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout", "teller-3");
        aggregate.markAuthenticated();
        aggregate.simulateTimeout(); // Force state to timed out
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-error", "teller-4");
        aggregate.markAuthenticated();
        // Corrupt the internal navigation state or lock it to simulate inaccuracy/lock
        aggregate.lockNavigation();
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // In a real scenario, fields might come from context, but hardcoded valid defaults work for Unit/BDD style
            NavigateMenuCmd cmd = new NavigateMenuCmd("MENU_MAIN", "ENTER");
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("MENU_MAIN", event.menuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Check if it's the specific exception type (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}