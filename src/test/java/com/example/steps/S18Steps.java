package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private StartSessionCmd.StartSessionCmdBuilder cmdBuilder;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    cmdBuilder = StartSessionCmd.builder()
            .tellerId("teller-001")
            .terminalId("term-42")
            .isAuthenticated(true)
            .isTimeoutConfigured(true)
            .isNavigationStateValid(true);
  }

  @Given("a valid tellerId is provided")
  public void a_valid_tellerId_is_provided() {
    // Handled in the initial setup builder
  }

  @Given("a valid terminalId is provided")
  public void a_valid_terminalId_is_provided() {
    // Handled in the initial setup builder
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_teller_session_aggregate_that_violates_authentication() {
    aggregate = new TellerSessionAggregate("session-auth-fail");
    cmdBuilder = StartSessionCmd.builder()
            .tellerId("teller-001")
            .terminalId("term-42")
            .isAuthenticated(false) // Violation
            .isTimeoutConfigured(true)
            .isNavigationStateValid(true);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_teller_session_aggregate_that_violates_timeout_config() {
    aggregate = new TellerSessionAggregate("session-timeout-fail");
    cmdBuilder = StartSessionCmd.builder()
            .tellerId("teller-001")
            .terminalId("term-42")
            .isAuthenticated(true)
            .isTimeoutConfigured(false) // Violation
            .isNavigationStateValid(true);
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_teller_session_aggregate_that_violates_navigation_state() {
    aggregate = new TellerSessionAggregate("session-nav-fail");
    cmdBuilder = StartSessionCmd.builder()
            .tellerId("teller-001")
            .terminalId("term-42")
            .isAuthenticated(true)
            .isTimeoutConfigured(true)
            .isNavigationStateValid(false); // Violation
  }

  @When("the StartSessionCmd command is executed")
  public void the_StartSessionCmd_command_is_executed() {
    try {
      Command cmd = cmdBuilder.build();
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    
    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    assertEquals("session.started", event.type());
    assertEquals("session-123", event.aggregateId());
    assertEquals("teller-001", event.tellerId());
    assertEquals("term-42", event.terminalId());
    assertNotNull(event.occurredAt());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNull(resultEvents);
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
  }

  // Ensure Lombok/Builder annotations work by adding static inner class if not using Lombok annotation processor explicitly in this snippet context,
  // but assuming standard Lombok usage or manual builder below if Lombok is not fully active in the execution environment.
  // For safety in this output, we assume Lombok is present in Spring Boot, but let's verify the builder usage.
}
