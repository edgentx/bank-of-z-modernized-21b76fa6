package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private static final String TEST_SESSION_ID = "session-123";
  private static final String TEST_MENU_ID = "MAIN_MENU";
  private static final String TEST_ACTION = "ENTER";

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;

  // Given steps
  ################################################################################

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
    // Initialize state to valid (authenticated, active)
    aggregate.markAuthenticated();
    aggregate.updateLastActivity(Instant.now());
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled by constant in context setup, or could be stored in scenario context
  }

  @Given("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    // Handled by constant
  }

  @Given("a valid action is provided")
  public void aValidActionIsProvided() {
    // Handled by constant
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
    // Intentionally do NOT mark as authenticated.
    // The session starts in an unauthenticated state (or we ensure isAuthenticated is false).
    // In this model, new aggregates are unauthenticated by default.
    aggregate.updateLastActivity(Instant.now()); // Ensure it doesn't fail on timeout first
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
    aggregate.markAuthenticated();
    // Set last activity to 2 hours ago (assuming timeout is < 2 hours)
    aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(2)));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationState() {
    aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
    aggregate.markAuthenticated();
    aggregate.updateLastActivity(Instant.now());
    // Force the aggregate into a terminal or invalid state (e.g. SESSION_ENDED)
    aggregate.endSession("Force ended for test violation");
  }

  // When steps
  ################################################################################

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      Command cmd = new NavigateMenuCmd(TEST_SESSION_ID, TEST_MENU_ID, TEST_ACTION);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  // Then steps
  ################################################################################

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNull(thrownException, "Should not have thrown an exception: " + thrownException);
    assertNotNull(resultEvents, "Result events list should not be null");
    assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
    assertEquals("menu.navigated", resultEvents.get(0).type(), "Event type should be menu.navigated");
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(thrownException, "Expected an exception to be thrown, but command succeeded");
    // We typically expect IllegalStateException or IllegalArgumentException for domain invariants
    assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
        "Expected a domain error (IllegalStateException or IllegalArgumentException), but got: " + thrownException.getClass().getSimpleName());
  }
}
