package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
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
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-1");
        aggregate.setCurrentMenu("MainMenu");
        aggregate.setLastActivity(Instant.now());
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do not mark authenticated
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-1");
        // Set last activity to 20 minutes ago (timeout is 15)
        aggregate.setLastActivity(Instant.now().minus(20, java.time.temporal.ChronoUnit.MINUTES));
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated("teller-1");
        aggregate.setCurrentMenu("OptionsMenu"); // Not MainMenu
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate construction in Given steps
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in When step command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in When step command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), "DepositMenu", "Enter");
            // If testing the context violation scenario, we adjust the command to trigger the specific logic
            if (aggregate.id().equals("session-bad-context")) {
                // This action assumes MainMenu context, but we are in OptionsMenu
                cmd = new NavigateMenuCmd(aggregate.id(), "DepositMenu", "View");
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Ideally a specific DomainException, but IllegalStateException/IllegalArgumentException fits the legacy/code
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
