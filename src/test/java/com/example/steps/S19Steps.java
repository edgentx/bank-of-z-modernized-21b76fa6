package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private NavigateMenuCmd command;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated(); // Ensure valid state
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled in aggregate initialization
  }

  @Given("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    // Will be set in command construction
  }

  @Given("a valid action is provided")
  public void aValidActionIsProvided() {
    // Will be set in command construction
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      command = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
      aggregate.execute(command);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNull(capturedException, "Should not have thrown an exception");
    assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
    assertEquals("menu.navigated", aggregate.uncommittedEvents().get(0).type());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate("session-violate-auth");
    aggregate.markUnauthenticated(); // Violate invariant
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate("session-violate-timeout");
    aggregate.markAuthenticated();
    aggregate.expireSession(); // Violate invariant
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationContext() {
    aggregate = new TellerSessionAggregate("session-violate-nav");
    aggregate.markAuthenticated();
    aggregate.invalidateNavigationContext(); // Violate invariant
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException, "Should have thrown an exception");
    assertTrue(capturedException instanceof IllegalStateException, "Should be an IllegalStateException (Domain Error)");
  }
}
