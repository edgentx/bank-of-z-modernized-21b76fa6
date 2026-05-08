package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  // Standard valid session parameters
  private static final String VALID_SESSION_ID = "session-123";
  private static final String VALID_TELLER_ID = "teller-001";
  private static final Duration VALID_TIMEOUT = Duration.ofMinutes(15);

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TIMEOUT);
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled by the aggregate construction in the previous step
  }

  @Given("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    // Handled in the 'When' step via command construction
  }

  @Given("a valid action is provided")
  public void aValidActionIsProvided() {
    // Handled in the 'When' step via command construction
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    // Create aggregate that is NOT authenticated
    aggregate = new TellerSessionAggregate(VALID_SESSION_ID, VALID_TELLER_ID, false, Instant.now(), VALID_TIMEOUT, true);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesSessionTimeout() {
    // Create aggregate where lastActivity was 20 minutes ago, but timeout is 15 minutes
    Instant oldActivity = Instant.now().minus(Duration.ofMinutes(20));
    aggregate = new TellerSessionAggregate(VALID_SESSION_ID, VALID_TELLER_ID, true, oldActivity, VALID_TIMEOUT, true);
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationState() {
    // Create aggregate with invalid navigation state
    aggregate = new TellerSessionAggregate(VALID_SESSION_ID, VALID_TELLER_ID, true, Instant.now(), VALID_TIMEOUT, false);
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, "MAIN_MENU", "ENTER");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    Assertions.assertNotNull(resultEvents);
    Assertions.assertEquals(1, resultEvents.size());
    Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
    Assertions.assertTrue(caughtException instanceof IllegalStateException);
  }
}
