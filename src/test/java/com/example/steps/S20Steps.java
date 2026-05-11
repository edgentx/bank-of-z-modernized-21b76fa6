package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

  private TellerSessionAggregate aggregate;
  private List<com.example.domain.shared.DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_teller_session_aggregate() {
    UUID id = UUID.randomUUID();
    aggregate = new TellerSessionAggregate(id, true, Instant.now(), "MAIN_MENU");
  }

  @Given("a valid sessionId is provided")
  public void a_valid_session_id_is_provided() {
    // Handled by constructor in previous step
  }

  @When("the EndSessionCmd command is executed")
  public void the_end_session_cmd_command_is_executed() {
    try {
      EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.ended event is emitted")
  public void a_session_ended_event_is_emitted() {
    assertNull(capturedException, "Should not have thrown exception");
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_auth() {
    UUID id = UUID.randomUUID();
    aggregate = new TellerSessionAggregate(id);
    aggregate.markUnauthenticated();
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout() {
    UUID id = UUID.randomUUID();
    aggregate = new TellerSessionAggregate(id);
    aggregate.markExpired();
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation() {
    UUID id = UUID.randomUUID();
    aggregate = new TellerSessionAggregate(id);
    aggregate.invalidateNavigationState();
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    // We expect IllegalStateException wrapping the domain error logic,
    // or a specific domain exception if defined. Based on snippet, IllegalStateException is used.
    assertTrue(capturedException instanceof IllegalStateException);
  }
}
