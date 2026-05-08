package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private String currentSessionId;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.currentSessionId = "SESSION-VALID-1";
        this.aggregate = new TellerSessionAggregate(currentSessionId);
        // Seed the aggregate to a valid state by simulating a login event application
        // In a real repo scenario, we'd load events. Here we construct state manually for the test.
        // This tells the aggregate it is authenticated.
        this.aggregate.initializeForTest("TELLER-01", Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is implicit from the aggregate creation in this test context
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the When step via command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the When step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        Command cmd = new NavigateMenuCmd(currentSessionId, "MENU_DEPOSITS", "SELECT");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.currentSessionId = "SESSION-NO-AUTH";
        this.aggregate = new TellerSessionAggregate(currentSessionId);
        // Do NOT initializeForTest -> isAuthenticated remains false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.currentSessionId = "SESSION-TIMEDOUT";
        this.aggregate = new TellerSessionAggregate(currentSessionId);
        // Initialize but with a timestamp far in the past
        this.aggregate.initializeForTest("TELLER-01", Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.currentSessionId = "SESSION-BAD-STATE";
        this.aggregate = new TellerSessionAggregate(currentSessionId);
        this.aggregate.initializeForTest("TELLER-01", Instant.now());
        // Force a state that implies the user is locked (cannot navigate)
        // Assuming 'LOCKED' is a state that doesn't allow navigation
        this.aggregate.forceStateForTest("LOCKED", "LOCK_REASON_01");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain exception, but none was thrown");
        // Usually IllegalStateException or IllegalArgumentException for domain rule violations
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
