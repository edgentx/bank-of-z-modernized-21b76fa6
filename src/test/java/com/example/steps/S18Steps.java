package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId = "teller-123";
    private String terminalId = "term-ABC";
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    // Given Steps

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure starting state is clean/valid for this scenario
        this.capturedException = null;
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "user-100";
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "term-200";
    }

    // Negative Given Steps (Pre-loading state)

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // We will enforce this violation via the command payload (authenticated = false)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Force the aggregate into a stale state assuming it was reconstituted from DB
        aggregate.forceTimeoutStaleState();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Force the navigation context to be invalid
        aggregate.markNavigationContextInvalid();
    }

    // When Steps

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // By default, we use authenticated=true unless the scenario implies otherwise.
        // However, the specific 'authentication' violation scenario might expect us to set false.
        // To be precise, we check if the exception type is expected. But Cucumber scenarios are isolated.
        // We'll assume valid authenticated=true unless we are in the specific Violation scenario.
        boolean isAuthenticated = true;
        
        // Heuristic check based on previous step title is flaky, better to set a flag.
        // But for BDD simplicity, we rely on the fact that the 'Given' for Auth violation sets up the scenario.
        // We will default to authenticated = true. The specific test for Auth violation will need a specific logic injection or we modify the command construction.
        // *Strategy*: The scenario for Auth violation implies the command sent is invalid.
        // But usually, the invariant is checked inside the aggregate. The aggregate needs to know the Auth status.
        // Our `StartSessionCmd` takes `isAuthenticated`. We'll pass `false` for the failure case.
        
        // Detecting if we are in the 'auth violation' scenario by inspecting the aggregate state or a thread-local context is messy.
        // Instead, we assume the standard test context. 
        // If the user wants to test auth failure, they usually expect to pass a bad credential.
        // We will check the state of the aggregate if possible, or just default to true and let the test fail for the auth scenario, forcing us to update the step.
        // BETTER: Use a flag stored in the step class.
    }

    // Refined When Step allowing specific parameter control via implicit scenario context or explicit flag
    // Since Cucumber doesn't pass params to When from Given easily without shared state, we will assume
    // the specific Negative Scenarios are the ONLY ones that trigger failures.
    // The simplest way: The code inside the method determines the failure mode based on the aggregate state setup in Given.
    
    public void theStartSessionCmdCommandIsExecutedInternal(boolean isAuthenticated) {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated);
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist the result
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Overriding the generic @When with logic to detect the scenario context is hard in pure Java without more state.
    // We will implement the generic When and use a simple check: 
    // If the aggregate was marked with 'forceTimeoutStaleState', we know we are in the timeout scenario.
    
    @When("the StartSessionCmd command is executed")
    public void executeStartSessionCmd() {
        // Default: Valid Auth
        boolean auth = true;
        // Check for Auth Violation scenario context (none explicit in Given, so we rely on the test runner flow)
        // Since 'Given ... violates authentication' doesn't set a specific boolean flag visible here easily,
        // we will rely on the test structure. 
        // NOTE: In a real test, we'd have specific step params. Here, we assume if the exception is caught, it's checked in Then.
        // For the Auth scenario, the aggregate logic throws if !authenticated. We must pass false.
        // How do we know to pass false? The 'Given' step for Auth violation exists.
        // We'll look at the setup. If the aggregate is fresh (Auth scenario), we pass false.
        // But the 'Valid TellerSession aggregate' is also fresh.
        
        // Refactoring for robustness: We will default to authenticated=true.
        // The 'Auth Violation' scenario test is LIKELY intended to pass a command where the security context claims auth failed.
        // We will check if the scenario title or a hint is available (No).
        // We will rely on the fact that we can set a scenario-specific flag in the Given steps.
        this.capturedException = null;
        
        // Logic to determine command payload based on state
        boolean isAuthenticatedPayload = true;
        // If the aggregate was explicitly set to invalid navigation, we still want to be authenticated to test THAT invariant specifically.
        // If the aggregate was NOT modified by Given (Auth violation), it is empty.
        
        // Heuristic: We will use a simple state variable 'shouldForceUnauthenticated'.
        if (shouldForceUnauthenticated) {
            isAuthenticatedPayload = false;
        }
        
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticatedPayload);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    private boolean shouldForceUnauthenticated = false;

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupAuthViolation() {
        shouldForceUnauthenticated = true;
        aValidTellerSessionAggregate(); // Reset aggregate
    }

    // Then Steps

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error to be thrown");
        // We expect an IllegalStateException or similar domain exception
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException || capturedException instanceof UnknownCommandException);
    }

    // --- Mock Infrastructure ---

    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final java.util.Map<String, TellerSessionAggregate> store = new java.util.HashMap<>();
        
        @Override
        public void save(TellerSessionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }
        
        @Override
        public java.util.Optional<TellerSessionAggregate> findById(String id) {
            return java.util.Optional.ofNullable(store.get(id));
        }
    }
}
