package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSession aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    private String sessionId;
    private String menuId;
    private String action;
    private String currentContext;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "session-123";
        aggregate = new TellerSession(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setCurrentMenuId("MAIN_MENU");
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        menuId = "ACCOUNT_DETAILS";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        action = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action, currentContext);
            // Reload from repo to simulate persistence boundary
            aggregate = repo.findById(sessionId).orElseThrow();
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // save changes
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.newMenuId());
    }

    // ---------- Negative Scenarios ----------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "session-unauth";
        aggregate = new TellerSession(sessionId);
        aggregate.setAuthenticated(false); // Not authenticated
        repo.save(aggregate);

        menuId = "ADMIN";
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSession(sessionId);
        aggregate.setAuthenticated(true);
        // Set last activity to 31 minutes ago (Timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(31, java.time.temporal.ChronoUnit.MINUTES));
        repo.save(aggregate);

        menuId = "MAIN_MENU";
        action = "REFRESH";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = "session-bad-context";
        aggregate = new TellerSession(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setCurrentMenuId("MAIN_MENU"); // Actual state
        repo.save(aggregate);

        menuId = "SETTINGS";
        action = "GOTO";
        // Lie about the current context
        currentContext = "FAKE_MENU";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Depending on the invariant, the exception type might vary (IllegalStateException, IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        assertNull(resultEvents);
    }
}