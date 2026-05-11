package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class S20Steps {

    private TellerSession session;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        session = new TellerSession("session-123");
        // Setup valid state for successful scenario
        session.markAuthenticated();
        session.markValidNavigation();
        // Not timed out is default
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in constructor of aggregate in previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        session = new TellerSession("session-auth-fail");
        session.markUnauthenticated();
        session.markValidNavigation();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSession("session-timeout-fail");
        session.markAuthenticated();
        session.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        session = new TellerSession("session-nav-fail");
        session.markAuthenticated();
        session.markInvalidNavigation();
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(session.id());
            List<var> events = session.execute(cmd);
            repository.save(session);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertThat(capturedException).isNull();
        assertThat(session.uncommittedEvents()).hasSize(1);
        assertThat(session.uncommittedEvents().get(0)).isInstanceOf(SessionEndedEvent.class);
        SessionEndedEvent event = (SessionEndedEvent) session.uncommittedEvents().get(0);
        assertThat(event.type()).isEqualTo("session.ended");
        assertThat(session.isActive()).isFalse();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertThat(capturedException).isNotNull();
        assertThat(capturedException).isInstanceOf(IllegalStateException.class);
    }
}
