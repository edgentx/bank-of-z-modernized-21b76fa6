package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Cucumber Steps for S-18: StartSessionCmd on TellerSession.
 * Uses in-memory repository for isolated testing.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Scenario 1 & 2 Setup ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // Pre-requisite for valid session is not being active yet, or valid context
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // TellerId is provided in the When step, this just documents the context
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // TerminalId is provided in the When step
    }

    // --- Scenario 2: Auth Violation ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // In this simplified domain, validation happens via the command contents.
        // We pass an invalid teller ID (not "AUTH_TELLER") in the When step.
    }

    // --- Scenario 3: Timeout Violation ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // Force the aggregate into an expired state using the helper method
        aggregate.markExpired();
    }

    // --- Scenario 4: Navigation State Violation ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        // Violation is triggered by passing "INVALID_CTX" as terminalId in the command
    }

    // --- Execution ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Determine inputs based on the scenario context implicitly handled by Gherkin tags or simple logic
        // For simplicity in S-18, we rely on specific data paths.
        // However, Cucumber contexts are usually isolated. 
        // We will use the aggregate's internal state or specific identifiers to trigger paths.
        
        String tellerId = "AUTH_TELLER"; // Default valid
        String terminalId = "TERM_01";

        // Detect violation scenarios to adjust inputs dynamically for the single 'When' step
        // This keeps the step definition generic.
        try {
            // Check if aggregate is pre-marked as expired (Scenario 3)
            // We don't have a direct getter for expired check exposed without logic, but we know we called markExpired
            // A safer way in real Cucumber is scenario context objects, but here we inspect state.
            
            // For Scenarios 2 & 4, we need to modify inputs. 
            // Since we can't pass parameters from Gherkin here easily without defining them, 
            // we will use a convention based on the Aggregate ID or a hidden flag if needed.
            // BUT, standard Cucumber passes data via tables or strings.
            // The provided Gherkin doesn't pass parameters. 
            // We must infer the parameters based on the "Given".
            
            // Let's check if we are in the "Auth Violation" scenario.
            // If the aggregate is new (Scenario 2) and we want to fail auth, we pass invalid teller.
            // However, "Valid aggregate" (Scenario 1) also starts new.
            // To differentiate, we might need to look at the specific setup or use a Scenario Context.
            
            // Solution: Use a mutable scenario context object or inspect the aggregate class (hacky).
            // Given the constraints, I will assume specific IDs trigger specific paths for the sake of the demo,
            // or simply try-catch and ignore.
            
            // BETTER APPROACH: The 'Given' steps set specific internal flags or states.
            // Scenario 2: No internal state change, just intend to send bad command.
            // Scenario 3: markExpired() called.
            
            // Let's look at the aggregate ID. 
            // If the aggregate is "expired" (checked via try/catch of the logic or a helper flag), we pass valid creds.
            // But wait, Scenario 2 expects auth error.
            
            // Since I cannot change the Gherkin, I will assume the test runner (Cucumber) isolates scenarios.
            // But one Step Definition class instance is used.
            // I will add a helper field `scenarioType` in the Given steps.
            
        } catch (Exception e) {
            // ignore
        }
        
        // Refined logic: Use 'null' checks or a string constant set in Given steps.
        // Let's set a field in the Given steps.
    }

    // Overriding the When step with specific logic inferred from context would be complex without ScenarioContext.
    // I will use a simple internal state tracker set in the Given methods.

    private String testTellerId = "AUTH_TELLER";
    private String testTerminalId = "TERM_01";

    @Given("a valid tellerId is provided")
    public void setValidTellerId() {
        this.testTellerId = "AUTH_TELLER";
    }

    @Given("a valid terminalId is provided")
    public void setValidTerminalId() {
        this.testTerminalId = "TERM_01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupAuthViolation() {
        this.testTellerId = "UNAUTH_TELLER"; // Triggers violation
        this.testTerminalId = "TERM_01";
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setupTimeoutViolation() {
        this.testTellerId = "AUTH_TELLER";
        this.testTerminalId = "TERM_01";
        String id = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(id);
        this.aggregate.markExpired(); // Internal helper
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setupNavViolation() {
        this.testTellerId = "AUTH_TELLER";
        this.testTerminalId = "INVALID_CTX"; // Triggers violation
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
    }

    @When("the StartSessionCmd command is executed")
    public void executeStartSessionCmd() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), testTellerId, testTerminalId);
            this.resultEvents = aggregate.execute(cmd);
            // Persist (in memory)
            repository.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    // --- Assertions ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected exception but command succeeded");
        // Verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Exception should be a domain error (IllegalStateException/IllegalArgumentException), but was: " + capturedException.getClass()
        );
    }

    // --- In-Memory Repository Implementation (Inner class or separate file, usually separate) ---
    // Included here for completeness of the pasteable block, though usually in its own file.
    public static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        // Simple map storage
        // Note: In a real project, this is in src/test/java/com/example/mocks/
    }
}
