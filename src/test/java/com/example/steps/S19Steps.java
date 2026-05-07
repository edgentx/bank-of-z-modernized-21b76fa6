package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private String testSessionId = "session-123";
    private String testMenuId = "MainMenu";
    private String testAction = "OPEN_ACCT";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated(); // Assume authenticated by default for valid scenario
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // testSessionId is already set
        assertNotNull(testSessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        assertNotNull(testMenuId);
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        assertNotNull(testAction);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(testSessionId, testMenuId, testAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(testMenuId, event.menuId());
        assertEquals(testAction, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(testSessionId);
        // markAuthenticated() is NOT called, so isAuthenticated remains false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated();
        aggregate.markInactive(); // Force timeout state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated();
        // Setting invalid inputs (e.g., null) will be done via the command or could be setup here if state matters.
        // In this implementation, context validation happens on the command payload, so we trigger it by sending a bad command.
        // However, the Gherkin implies the AGGREGATE violates it. Let's assume the aggregate checks internal state consistency.
        // For this implementation, we will rely on passing an invalid command payload to trigger the specific error message,
        // but to follow the Gherkin strictly, let's assume the Command has invalid data.
        testMenuId = null; // This will violate context validity
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
