package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.MenuNavigatedEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import com.example.domain.tellsession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repo = new TellerSessionRepository() {
        private TellerSessionAggregate cached;
        @Override public TellerSessionAggregate load(String id) { return cached; }
        @Override public void save(TellerSessionAggregate aggregate) { this.cached = aggregate; }
    };
    
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-1");
        // Hydrating aggregate to a valid state (Authenticated & Active)
        aggregate.apply(new MenuNavigatedEvent("session-1", "LOGIN", "AUTHENTICATED", "Teller", java.time.Instant.now()));
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate instantiation in previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by command instantiation in When step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by command instantiation in When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-1", "MAIN_MENU", "DISPLAY");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Expected one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-unknown");
        // Leaving aggregate unauthenticated (no applied event)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timedout");
        // Setup authenticated state first
        aggregate.apply(new MenuNavigatedEvent("session-timedout", "LOGIN", "AUTHENTICATED", "Teller", java.time.Instant.now().minusSeconds(3600)));
        // The aggregate logic will detect this based on current time vs last activity
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.apply(new MenuNavigatedEvent("session-bad-state", "LOGIN", "AUTHENTICATED", "Teller", java.time.Instant.now()));
        // Trying to navigate to a menu that doesn't exist or invalid transition
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected a domain exception (IllegalStateException/IllegalArgumentException), got: " + caughtException.getClass().getSimpleName()
        );
    }
}
