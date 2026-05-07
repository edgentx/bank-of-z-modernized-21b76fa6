package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
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
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // State for inputs
    private String inputSessionId = "session-123";
    private String inputMenuId = "MAIN_MENU";
    private String inputAction = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(inputSessionId);
        // Ensure valid state
        aggregate.markAuthenticated("teller-001");
        aggregate.setCurrentMenu("LOGIN");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(inputSessionId);
        aggregate.markUnauthenticated(); // Violation: Not authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(inputSessionId);
        aggregate.markAuthenticated("teller-001");
        // Set last activity to 20 minutes ago (threshold is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_context() {
        aggregate = new TellerSessionAggregate(inputSessionId);
        aggregate.markAuthenticated("teller-001");
        aggregate.setCurrentMenu("LOGIN");
        // We will attempt to jump to a restricted menu
        inputMenuId = "ADMIN_DASHBOARD"; // Invalid transition from LOGIN
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        inputSessionId = "session-123";
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        inputMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        inputAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        // Reload from repo to ensure clean state if needed, though here we use instance directly
        var cmd = new NavigateMenuCmd(inputSessionId, inputMenuId, inputAction);
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist changes
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
