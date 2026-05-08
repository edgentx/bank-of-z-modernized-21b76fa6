package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private Iterable<DomainEvent> resultEvents;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-456");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate creation in previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in When step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        var cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
        resultEvents = aggregate.execute(cmd);
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        var event = resultEvents.iterator().next();
        assertEquals("menu.navigated", event.type());
    }

    // Scenario 2: Authentication Invariant
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Not calling markAuthenticated() to violate the invariant
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed for unauth")
    public void theNavigateMenuCmdCommandIsExecutedForUnauth() {
        var cmd = new NavigateMenuCmd("session-unauth", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario 3: Timeout Invariant
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-456");
        aggregate.expire(); // Force the aggregate to look expired
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed for timeout")
    public void theNavigateMenuCmdCommandIsExecutedForTimeout() {
        var cmd = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    // Scenario 4: Context Invariant
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-context");
        aggregate.markAuthenticated("teller-456");
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed for invalid context")
    public void theNavigateMenuCmdCommandIsExecutedForInvalidContext() {
        // Passing blank menuId to violate context validation
        var cmd = new NavigateMenuCmd("session-context", "", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
