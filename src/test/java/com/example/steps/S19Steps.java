package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Setup valid state
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate construction
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be used in the command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be used in the command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Do NOT call markAuthenticated. Default is false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markSessionTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        aggregate = new TellerSessionAggregate("session-context-fail");
        aggregate.markOperationalContextInvalid();
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException | IllegalArgumentException e) {
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
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // We expect an IllegalStateException based on our implementation, or potentially a DomainError wrapped exception
        assertTrue(thrownException instanceof IllegalStateException || 
                   thrownException instanceof IllegalArgumentException || 
                   thrownException instanceof UnknownCommandException);
        
        // Verify no events were produced
        assertTrue(resultEvents == null || resultEvents.isEmpty());
    }
}
