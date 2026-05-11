package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    // --- Scenarios Setup ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup a valid authenticated state for the happy path
        aggregate.markAuthenticated("teller-456");
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicit in the aggregate ID initialized above
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Used in the 'When' step construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Used in the 'When' step construction
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth-123");
        // Intentionally not calling markAuthenticated()
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-123");
        aggregate.markAuthenticated("teller-456");
        // Set activity to 20 minutes ago (assuming 15 min timeout)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-context-123");
        aggregate.markAuthenticated("teller-456");
        aggregate.setLastActivityAt(Instant.now());
        // Setup a state where a transition to a restricted menu is invalid from current context
        aggregate.setCurrentMenuId("PUBLIC_MENU");
    }

    // --- Actions ---

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Determine target menu based on context setup to trigger violations or success
            String targetMenu = "ACCOUNT_INQUIRY"; // Default valid
            
            // Logic to handle specific violation test setup
            if (aggregate.getCurrentMenuId() != null && aggregate.getCurrentMenuId().equals("PUBLIC_MENU")) {
                // Triggering the Navigation State violation logic defined in Aggregate
                targetMenu = "RESTRICTED_ADMIN_MENU";
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // --- Outcomes ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("menu.navigated", resultingEvents.get(0).type());
        assertNull(capturedException, "Should not have thrown an exception for valid command");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        assertNull(resultingEvents, "No events should be produced when command is rejected");
    }
}
