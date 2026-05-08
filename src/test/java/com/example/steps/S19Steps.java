package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Hydrate to a valid authenticated state
        aggregate.hydrate("teller-01", true, Instant.now(), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-unauth-123");
        // Hydrate to an unauthenticated state
        aggregate.hydrate("teller-01", false, Instant.now(), "LOGIN_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-123");
        aggregate.configureTimeout(Duration.ofMinutes(15));
        // Hydrate with a lastActivityAt time 20 minutes in the past
        aggregate.hydrate("teller-01", true, Instant.now().minus(Duration.ofMinutes(20)), "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-bad-ctx-123");
        aggregate.hydrate("teller-01", true, Instant.now(), "INVALID_MENU_STATE");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the aggregate constructor in the Given steps
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by the When step argument
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by the When step argument
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Using a standard valid menu ID for positive flow, 
            // but the negative flows will be caught by the aggregate internal state validation
            var cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNTS_SUMMARY", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        var event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("ACCOUNTS_SUMMARY", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect either IllegalStateException or IllegalArgumentException depending on the invariant violated
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}