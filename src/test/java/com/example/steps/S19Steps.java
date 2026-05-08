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
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // ----------------------------------------------------------------
    // Givens
    // ----------------------------------------------------------------

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Setup a standard authenticated session
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(Instant.now(), "MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Covered by aggregate instantiation in previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be provided in the When step via cmd object
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be provided in the When step via cmd object
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create an unauthenticated session
        aggregate = new TellerSessionAggregate("session-unauth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Mark active, but set last activity to 20 minutes ago (Timeout is 15)
        aggregate.markInactive(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        aggregate = new TellerSessionAggregate("session-context");
        // Logic: If in POST_TRANSACTIONS, cannot go to ADMIN_SETTINGS
        aggregate.markOperationalContextInvalid("POST_TRANSACTIONS");
    }

    // ----------------------------------------------------------------
    // Whens
    // ----------------------------------------------------------------

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Target menu defaults to DASHBOARD for success, or ADMIN_SETTINGS for context violation
            String targetMenu = "POST_TRANSACTIONS".equals(aggregate.getCurrentMenuId()) ? "ADMIN_SETTINGS" : "DASHBOARD";
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(
                aggregate.id(),
                targetMenu,
                "ENTER"
            );
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException | IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    // ----------------------------------------------------------------
    // Thens
    // ----------------------------------------------------------------

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // It must be an unchecked exception (RuntimeException) which acts as Domain Error rejection
        Assertions.assertTrue(thrownException instanceof IllegalStateException || 
                             thrownException instanceof IllegalArgumentException);
    }
}
