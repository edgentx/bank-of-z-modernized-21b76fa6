package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = "session-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(); // Ensure valid pre-condition
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        // Created, but NOT authenticated
        aggregate = new TellerSessionAggregate("unauthenticated-session");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("timed-out-session");
        aggregate.markAuthenticated(); // Auth is valid, but time is not
        aggregate.markTimedOut();     // Simulate time passing
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("bad-state-session");
        aggregate.markAuthenticated();
        // Set current state to a specific menu
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate initialization, but we ensure the command matches
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
            // Dynamic setup based on previous Givens to trigger failures
            String menuId = "DEPOSIT_SCREEN";
            String action = "ENTER";
            
            // If we are in the "bad state" scenario, force the conflict
            if (aggregate.getCurrentMenu() != null && aggregate.getCurrentMenu().equals("MAIN_MENU")) {
                 // Force the specific invariant violation condition for testing
                 // (Assuming MAIN_MENU -> RELOAD triggers the specific rule defined in aggregate)
                 // To match the generic Gherkin, we can just execute a command that fails.
                 // Let's use the RELOAD logic implemented in the aggregate for the specific failure case.
                 // However, the Gherkin is generic. Let's try a normal nav, but assume the aggregate logic
                 // rejects it based on the state we set. 
                 // Actually, the aggregate logic rejects RELOAD on SAME menu.
                 // Let's adjust the step logic: if we want to test rejection, we just need 
                 // the aggregate to throw.
                 
                 // Let's rely on the specific invariants: Auth (throws) and Timeout (throws).
                 // For the 3rd scenario (Nav state), we need to ensure the command we create 
                 // triggers the "bad state" logic in the aggregate.
                 // In the aggregate: if (cmd.menuId().equals(this.currentMenuId) && "RELOAD".equals(cmd.action()))
                 if ("MAIN_MENU".equals(aggregate.getCurrentMenu())) {
                     menuId = "MAIN_MENU";
                     action = "RELOAD";
                 }
            }

            command = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Should be MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Should be a domain error/IllegalStateException");
    }
}
