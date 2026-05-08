package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

@SpringBootTest
public class S18Steps {

    @Autowired
    private TellerSessionRepository repository;

    private TellerSessionAggregate aggregate;
    private List<SessionStartedEvent> resultEvents;
    private Exception capturedException;
    private StartSessionCmd cmd;

    // Positive Case
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in When
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in When
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Construct a valid command for positive flow or default case
            if (cmd == null) {
                cmd = new StartSessionCmd("session-1", "teller-123", "term-A", true, Instant.now().plusSeconds(3600), "HOME");
            }
            resultEvents = (List<SessionStartedEvent>) aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("session.started", resultEvents.get(0).type());
    }

    // Negative Case: Authentication
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        cmd = new StartSessionCmd("session-auth-fail", "teller-123", "term-A", false, Instant.now().plusSeconds(3600), "HOME");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }

    // Negative Case: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Pass an expiration date in the past to violate the invariant
        cmd = new StartSessionCmd("session-timeout-fail", "teller-123", "term-A", true, Instant.now().minusSeconds(1), "HOME");
    }

    // Negative Case: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Pass null or blank context
        cmd = new StartSessionCmd("session-nav-fail", "teller-123", "term-A", true, Instant.now().plusSeconds(3600), "");
    }
}
