package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSession session;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Create a session that is authenticated, active, and valid.
        // We simulate the internal state needed for the aggregate to pass validations.
        String sessionId = "TS-123";
        session = new TellerSession(sessionId);

        // We must initialize the aggregate state to reflect an active, authenticated session.
        // Since S-10 exists (StartSession), we assume the state is hydrated via events.
        // For unit test isolation in this step, we invoke a behavior or set state that passes
        // the invariants checks: isAuthenticated=true, isActive=true, lastActivity=recent.
        
        // Hydrate manually for the "Given" context:
        session.enforceTestState(TellerSessionState.AUTHENTICATED, Instant.now(), true);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The sessionId is implicitly handled by the aggregate instance.
        // We verify the session ID is not null.
        Assertions.assertNotNull(session.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "TS-FAIL-AUTH";
        session = new TellerSession(sessionId);
        // Set state to UNAUTHENTICATED (or similar) to violate the rule.
        session.enforceTestState(TellerSessionState.UNAUTHENTICATED, Instant.now(), false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "TS-FAIL-TIMEOUT";
        session = new TellerSession(sessionId);
        // Set last activity time to 2 hours ago (violating a hypothetical 30min timeout).
        Instant oldTime = Instant.now().minus(Duration.ofHours(2));
        session.enforceTestState(TellerSessionState.AUTHENTICATED, oldTime, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        String sessionId = "TS-FAIL-NAV";
        session = new TellerSession(sessionId);
        // Set navigation state to INCONSISTENT to violate the rule.
        session.enforceTestState(TellerSessionState.AUTHENTICATED, Instant.now(), false);
        // We assume the aggregate has a way to flag inconsistent navigation context.
        session.flagNavigationStateInconsistent();
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(session.id());
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals(SessionEndedEvent.class, resultEvents.get(0).getClass());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Depending on implementation, this could be IllegalStateException, IllegalArgumentException, etc.
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException,
            "Expected a domain error exception, but got: " + caughtException.getClass()
        );
    }
}
