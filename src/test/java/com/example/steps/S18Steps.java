package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

  private TellerSessionAggregate aggregate;
  private StartSessionCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  // --- Scenario: Successfully execute StartSessionCmd ---

  @Given("a valid TellerSession aggregate")
  public void a_valid_TellerSession_aggregate() {
    this.aggregate = new TellerSessionAggregate("session-1");
    // Ensure clean state
    this.aggregate.markActive(false);
  }

  @And("a valid tellerId is provided")
  public void a_valid_tellerId_is_provided() {
    // Setup step handled in the When block construction or implicit validation
  }

  @And("a valid terminalId is provided")
  public void a_valid_terminalId_is_provided() {
    // Setup step handled in the When block construction or implicit validation
  }

  @When("the StartSessionCmd command is executed")
  public void the_StartSessionCmd_command_is_executed() {
    // Valid data
    cmd = new StartSessionCmd("session-1", "teller-123", "terminal-A");
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a session.started event is emitted")
  public void a_session_started_event_is_emitted() {
    Assertions.assertNull(thrownException, "Should not have thrown exception");
    Assertions.assertNotNull(resultEvents);
    Assertions.assertEquals(1, resultEvents.size());
    Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
    Assertions.assertEquals("session-1", event.aggregateId());
    Assertions.assertEquals("teller-123", event.tellerId());
    Assertions.assertEquals("terminal-A", event.terminalId());
    Assertions.assertEquals("session.started", event.type());
  }

  // --- Scenario: StartSessionCmd rejected — A teller must be authenticated ---

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void a_TellerSession_aggregate_that_violates_authentication() {
    // Interpretation: The aggregate state implies the teller is NOT authenticated,
    // so the command execution logic should reject it.
    // However, the 'StartSessionCmd' is the command that AUTHENTICATES.
    // To make this test scenario meaningful based on the 'Violates' prompt:
    // We will simulate a state where the session is ALREADY active or initialized,
    // and we are trying to re-start without proper auth flow, OR
    // we mock the command to represent an unauthenticated context (but StartSessionCmd has no 'isAuthenticated' field).
    // ALTERNATIVE: The aggregate has a flag that must be true before StartSession works.
    // Let's assume the 'StartSessionCmd' CAN be executed by an unauthenticated user to BECOME authenticated.
    // So, this scenario likely covers the case where the user is ALREADY logged in and trying to start again? No.
    // Let's interpret "Violates: A teller must be authenticated" as: The command is rejected because
    // the pre-condition of the aggregate (perhaps loaded from repo) is invalid.
    // Let's assume for this test: we mock the aggregate to think it needs explicit auth flag set true.
    // Actually, looking at the Domain: `authenticated = true` is set INSIDE `handleStartSession`.
    // So this scenario might be testing that IF we were to check auth BEFORE setting it.
    // Let's assume the aggregate has a `authenticated` flag that defaults to false.
    // To make this test pass meaningfully: We will assume the command logic checks `authenticated == true` BEFORE starting,
    // which implies we need a separate 'AuthenticateCmd' first.
    // BUT the story says: "Initiates a teller session following successful authentication."
    // This implies Auth happens BEFORE this command.
    // So we need to simulate a NON-authenticated aggregate trying to run this.
    this.aggregate = new TellerSessionAggregate("session-2");
    this.aggregate.markAuthenticated(false); // Default is false, so this is explicit.
  }

  // Note: We reuse the 'When' and 'Then' blocks from above? No, they expect specific results.
  // We need specific Then for rejection.

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    Assertions.assertNotNull(thrownException, "Expected exception but command succeeded");
    // Verify it's an IllegalStateException or Domain specific error
    Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
  }

  // --- Scenario: Sessions must timeout ---

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void a_TellerSession_aggregate_that_violates_timeout() {
    this.aggregate = new TellerSessionAggregate("session-3");
    // Simulate an active session that has timed out
    this.aggregate.markActive(true);
    // Set last activity to 31 minutes ago (Timeout is 30)
    this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
  }

  // --- Scenario: Navigation state must accurately reflect ---

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void a_TellerSession_aggregate_that_violates_navigation() {
    // This is vague. Let's assume it means the session is already in a bad state (Active but maybe null Teller?)
    // Or simply that it is already active.
    this.aggregate = new TellerSessionAggregate("session-4");
    this.aggregate.markActive(true);
  }

}