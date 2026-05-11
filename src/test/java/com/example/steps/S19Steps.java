package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        // Ensure valid state for success scenario
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail", Duration.ofMinutes(15));
        // Do NOT mark authenticated -> state is invalid
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail", Duration.ofMinutes(15));
        aggregate.markAuthenticated(); // Auth is valid
        aggregate.markExpired(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-context-fail", Duration.ofMinutes(15));
        aggregate.markAuthenticated();
        aggregate.corruptContext(); // Force context violation
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by aggregate construction ID
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the 'When' step command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the 'When' step command construction
    }

    // --- When Steps ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Using fixed values for valid action/menu to satisfy scenario context
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Then Steps ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
