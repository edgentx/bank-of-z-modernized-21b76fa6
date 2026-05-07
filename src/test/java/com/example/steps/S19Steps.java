package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Pre-condition: simulate an authenticated, active session for the happy path
        aggregate.markAuthenticated("teller-01", "DASHBOARD");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly leaving it unauthenticated (default constructor behavior sets authenticated=false)
        // However, to bypass other potential checks, we might need to ensure it's 'created' but not 'authenticated'
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01", "DASHBOARD");
        // Use the helper method to manipulate time for the test
        aggregate.violateSessionTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String sessionId = "session-bad-state";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01", "DASHBOARD");
        // Force aggregate into a bad state
        aggregate.violateNavigationState();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by the aggregate setup in the Given steps
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Parameter will be handled in the 'When' step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Parameter will be handled in the 'When' step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // We assume a generic valid navigation command for the standard happy path and error checks
            // Specific constraint violations are handled by the state setup in the Given steps
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect IllegalStateException for domain invariant violations based on the Aggregate implementation
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
