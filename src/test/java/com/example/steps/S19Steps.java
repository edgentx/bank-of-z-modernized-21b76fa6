package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Hydrate aggregate to a valid state (Authenticated, Active, Valid Context)
        // Assuming a constructor or internal mechanism to set state for test setup
        // Here we simulate it by constructing with valid params or relying on defaults
        // For this specific command, we need:
        // 1. Authenticated = true
        // 2. Active (Timeout > now)
        // 3. Context Valid
        // Since TellerSessionAggregate likely defaults to new/empty, we assume the repository
        // would rehydrate it. But for unit test steps, we can instantiate a 'valid' one.
        // We will use a test-friendly setup (or reflection/package-private access if needed,
        // but standard is to invoke commands to reach state. Since this is the FIRST command story,
        // we assume the constructor handles 'open' or we treat 'valid' as satisfying preconditions).
        
        // Let's assume the Aggregate has a test setup or we construct it satisfying the preconditions.
        // For the sake of BDD steps, we create a new instance and assume it meets the requirements 
        // or set internal state if possible. Given the constraints, we'll assume the constructor 
        // initializes it in a way that can be acted upon, or we relax the 'valid' definition 
        // to the command handler's validation. 
        // *However*, the scenarios explicitly test REJECTION based on state. 
        // So we need a way to set state.
        // Assuming a Test-Only constructor or method in TellerSessionAggregate (like `markAuthenticated()`)
        // but per domain rules, we should only use commands. 
        // Since S-19 is the implementation, we might be missing the Initiate command. 
        // We will assume `TellerSessionAggregate` can be constructed in a 'ready' state or we 
        // simulate the state check failure.
        
        // Valid State: Authenticated, Active, Context OK.
        // We'll use a specific constructor or helper if available, otherwise defaults.
        aggregate = new TellerSessionAggregate("SESSION-1", true, System.currentTimeMillis() + 10000, true);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in command construction
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd("SESSION-1", "MAIN_MENU", "SELECT");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Expected success but got exception: " + (caughtException != null ? caughtException.getMessage() : ""));
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty(), "Expected events to be emitted");
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create aggregate where authenticated = false
        aggregate = new TellerSessionAggregate("SESSION-2", false, System.currentTimeMillis() + 10000, true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Create aggregate where lastActive < timeoutThreshold (old timestamp)
        long pastTime = System.currentTimeMillis() - 100000;
        aggregate = new TellerSessionAggregate("SESSION-3", true, pastTime, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        // Create aggregate where context is invalid
        aggregate = new TellerSessionAggregate("SESSION-4", true, System.currentTimeMillis() + 10000, false);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a domain error but command succeeded");
        // Specific message checks can be added here, e.g., contains("authentication")
    }
}
