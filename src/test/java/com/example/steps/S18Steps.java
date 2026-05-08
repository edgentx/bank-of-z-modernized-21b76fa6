package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private String testTellerId = "teller-123";
    private String testTerminalId = "term-T01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated(); // Default to authenticated for success path
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Do not mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.markAuthenticated();
        // Set last activity to 1 minute ago (within the 15 min timeout)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(1)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.markAuthenticated();
        // Set an invalid navigation state for starting a session
        aggregate.setNavigationState("TXN_IN_PROGRESS");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in context
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in context
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(aggregate.id(), testTellerId, testTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(testTellerId, event.tellerId());
        assertEquals(testTerminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect IllegalStateException for domain invariant violations
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof UnknownCommandException);
    }
}
