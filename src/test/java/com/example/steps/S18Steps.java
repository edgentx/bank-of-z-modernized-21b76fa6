package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Tellers ID provided via context in the 'When' step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Terminal ID provided via context in the 'When' step
    }

    // --- Negative Scenario Setups ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Force the aggregate into a state where it is stale
        aggregate.markAsStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    // --- Actions ---

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Default valid parameters
        String sessionId = aggregate.id();
        String tellerId = "TELLER-001";
        String terminalId = "TERM-A";
        boolean isAuthenticated = true;
        boolean validContext = true;

        // We adjust parameters based on the state set up in the 'Given' steps.
        // Since we can't easily inspect step text history in Cucumber without scenario context,
        // we rely on specific setup methods mutating the aggregate state, and we pass specific
        // invalid flags in the command for the specific negative scenarios.
        // However, the authentication check is inside the aggregate logic.
        // To trigger the 'not authenticated' error, we pass false.
        // But we need to know *which* scenario we are in.
        // A simple way is to inspect the aggregate state we set up.

        if (aggregate.getLastActivityAt() != null && !aggregate.isActive()) {
            // This doesn't make sense, if it's not active, it can't have activity.
            // We use the markAsStale method which sets active=true and old date.
        }

        // Heuristic to detect specific negative scenarios based on setup method side effects:
        // 1. Authentication violation: No specific flag on aggregate exposed publicly implies this,
        //    but we can detect based on the fact we created a fresh one.
        //    Actually, it's cleaner to use the 'Then' assertions to verify the error type.
        //    We need a way to drive the command parameters.
        //    For Cucumber purity, we should use Scenario Outline or Examples, but for this task,
        //    I will derive the command parameters from specific public state checks or hardcoded context
        //    associated with the specific "Given" clauses above.

        // Scenario 1: Timeout (markAsStale sets active=true and old date)
        boolean isStaleScenario = (aggregate.getLastActivityAt() != null && 
                                   aggregate.getLastActivityAt().isBefore(Instant.now().minusSeconds(600)));

        // Scenario 2: Navigation State. We simulate this by passing invalid context.
        // But we need to know WHEN to pass invalid context.
        // I will check for a specific dummy state or just rely on a known convention.
        // Actually, the simplest way given the constraints is to assume a "Happy Path" default,
        // and override if the aggregate state looks suspicious (e.g. stale).
        // For the Auth error, we must pass isAuthenticated=false.
        // Since the 'Given' for auth error doesn't change aggregate state uniquely (it's just a fresh aggregate),
        // we have a problem distinguishing it from the happy path *before* execution.
        
        // HOWEVER, looking at the step definitions provided in the prompt:
        // "Given a TellerSession aggregate that violates: A teller must be authenticated to initiate a session."
        // This sets up the aggregate. The Command must trigger the error.
        // The error is inside the aggregate: `if (!cmd.isAuthenticated())`.
        // So for the 'When', I need to know to send a bad command.
        // I will inspect the ID or a marker.
        
        // To strictly follow the prompt's scenarios without over-engineering the Cucumber context:
        // I'll assume the 'Happy Path' defaults are valid.
        // For the specific violations, I will try to detect which one we are in.
        // If the aggregate is stale (set in previous step), I pass valid auth.
        // If it's not stale, I need to distinguish between Happy and Auth/Nav errors.
        // I'll check a dummy field or just assume the order.
        // Actually, standard BDD practice: The 'Given' prepares the data.
        // The 'When' acts.
        // I will use a simple heuristic:
        // - If aggregate.isStale() -> Use Valid Auth (tests timeout logic).
        // - Else -> This could be Happy, Auth, or Nav.
        // I'll just default to Valid Auth/Nav. The 'Auth' scenario expects an error.
        // Since I can't distinguish "Auth" from "Happy" via aggregate state (both are fresh),
        // I must assume the 'Auth' scenario has a unique setup or I simply run the valid command 
        // and the 'Then' catches nothing? No, that fails.
        
        // REVISION: I will use the aggregate ID or a flag if I could set it. But I can't change the Given code.
        // I will assume the 'Given' for Auth Error sets a specific flag on the aggregate if possible? No.
        // I will use the `InMemoryTellerSessionRepository` or a static context if needed? No.
        
        // SOLUTION: I'll check if the aggregate is NOT stale. If it is NOT stale, I will check if the ID contains "UNAUTH".
        // The 'Given' for Auth creates a UUID, which is random.
        // This is a limitation of pure text mapping without a shared scenario context object.
        // However, looking at the 'Given' steps I wrote, they are distinct methods.
        // In Cucumber, the specific 'Given' runs before 'When'.
        // I will use a thread-local or field in this class to track the scenario type, set by the 'Given' methods.
        
        // Refined 'Given' logic below (updating the methods above to set a flag field is best, but I can't edit prompt text).
        // Wait, I AM writing the Given methods above.
        // So I can add a `private String currentScenarioType = "HAPPY";` field and update it in the Given methods.
    }

    // --- Updated Given methods with State Tracking for the 'When' step ---
    private String currentScenario = "HAPPY";

    @Given("a valid TellerSession aggregate")
    public void setup_happy_path() {
        currentScenario = "HAPPY";
        a_valid_TellerSession_aggregate();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setup_auth_error() {
        currentScenario = "AUTH_ERROR";
        a_TellerSession_aggregate_that_violates_authentication();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setup_timeout_error() {
        currentScenario = "TIMEOUT_ERROR";
        a_TellerSession_aggregate_that_violates_timeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setup_nav_error() {
        currentScenario = "NAV_ERROR";
        a_TellerSession_aggregate_that_violates_navigation_state();
    }

    @When("the StartSessionCmd command is executed")
    public void execute_command_based_on_scenario() {
        String sessionId = aggregate.id();
        String tellerId = "TELLER-001";
        String terminalId = "TERM-A";
        
        boolean isAuthenticated = true;
        boolean validContext = true;

        switch (currentScenario) {
            case "AUTH_ERROR":
                isAuthenticated = false;
                break;
            case "NAV_ERROR":
                validContext = false;
                break;
            case "TIMEOUT_ERROR":
                // Aggregate is already stale via `markAsStale()` in the setup.
                // We send a valid command, it should fail internally.
                break;
            case "HAPPY":
            default:
                // Defaults are valid
                break;
        }

        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, validContext);

        try {
            resultEvents = aggregate.execute(cmd);
            // Also save to repo to simulate persistence side-effect if needed
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Assertions ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted, but got null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("TELLER-001", event.tellerId());
        Assertions.assertEquals("TERM-A", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // It can be IllegalStateException or IllegalArgumentException depending on implementation
        // Our implementation uses IllegalStateException for invariants.
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof UnknownCommandException || capturedException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException/IllegalArgumentException), got: " + capturedException.getClass().getSimpleName()
        );
    }
}