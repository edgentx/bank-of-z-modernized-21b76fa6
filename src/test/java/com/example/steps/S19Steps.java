package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("SESSION-1", "TELLER-1");
        // Simulate a successful login to establish a valid base state
        aggregate.execute(new LoginTellerCmd("SESSION-1", "TELLER-1"));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate initialization
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the When step via Command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the When step via Command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create an aggregate but DO NOT execute LoginCmd.
        // The TellerSessionAggregate constructor defaults authenticated = false.
        this.aggregate = new TellerSessionAggregate("SESSION-ERR-1", null);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("SESSION-TMO-1", "TELLER-1");
        aggregate.execute(new LoginTellerCmd("SESSION-TMO-1", "TELLER-1"));
        // Force the last active time to be ancient (simulating timeout)
        aggregate.forceTimeoutForTesting();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        // Create a valid session, but we will attempt a navigation that is invalid based on context
        // (e.g. navigating to a restricted screen without proper auth context)
        // For this implementation, we assume a state mismatch requires the aggregate to be in an inconsistent state
        // or the command parameters to conflict with the aggregate's current reality.
        this.aggregate = new TellerSessionAggregate("SESSION-NAV-1", "TELLER-1");
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Use defaults for "valid" inputs provided in the Given steps
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        try {
            // Specifically for the "Navigation state must accurately reflect..." scenario.
            // Attempting to navigate to 'ADMIN_MENU' from a base session might be rejected if logic dictates.
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ADMIN_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Depending on implementation, this could be IllegalStateException or IllegalArgumentException
        // The Check ensures an exception was thrown.
    }

    // Helper for the specific scenario mapping if needed, 
    // though Cucumber will auto-map by regex phrase to method.
}
