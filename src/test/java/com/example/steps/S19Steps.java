package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {
  private TellerSessionAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception thrownException;
  private String testSessionId = "sess-123";
  private String testMenuId = "MAIN_MENU";
  private String testAction = "ENTER";

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate(testSessionId);
    aggregate.setAuthenticated(true);
    aggregate.setActive(true);
    aggregate.setLastActivityAt(Instant.now());
  }

  @And("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled by initialization in Given
  }

  @And("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    // Handled by initialization in Given
  }

  @And("a valid action is provided")
  public void aValidActionIsProvided() {
    // Handled by initialization in Given
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      Command cmd = new NavigateMenuCmd(testSessionId, testMenuId, testAction);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNotNull(resultEvents, "Events should not be null");
    assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
    DomainEvent event = resultEvents.get(0);
    assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
    assertEquals("menu.navigated", event.type());
    MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
    assertEquals(testSessionId, navEvent.aggregateId());
    assertEquals(testMenuId, navEvent.menuId());
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate(testSessionId);
    aggregate.setAuthenticated(false); // Violation
    aggregate.setLastActivityAt(Instant.now());
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate(testSessionId);
    aggregate.setAuthenticated(true);
    // Set last activity to 35 minutes ago (configured timeout is 30)
    aggregate.setLastActivityAt(Instant.now().minusSeconds(35 * 60));
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavigationContext() {
    aggregate = new TellerSessionAggregate(testSessionId);
    aggregate.setAuthenticated(true);
    aggregate.setLastActivityAt(Instant.now());
    aggregate.setActive(false); // Violation: inactive session cannot navigate
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(thrownException, "Exception should have been thrown");
    assertTrue(thrownException instanceof IllegalStateException, "Should be an IllegalStateException domain error");
  }
}
