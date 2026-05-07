package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import com.example.domain.tellsession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellsession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate initialization in previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be used in command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be used in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("teller.session.menu.navigated", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(false); // Violation
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.setActive(false); // Violation
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(true);
        // Assuming there is a constraint preventing navigation from certain states
        // or to invalid menus. We mock this by a flag or specific state setup if available.
        // For this test, we assume the aggregate has a method to force an invalid state
        // or we rely on command validation failing (e.g. invalid menu ID).
        // Here we'll assume the aggregate tracks valid menus and we force it into a state
        // where navigation is blocked, or we simply pass an invalid command.
        // However, the prompt implies the AGGREGATE is in a violating state.
        aggregate.setLocked(true);
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Domain errors are usually IllegalStateException or IllegalArgumentException in this pattern
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
