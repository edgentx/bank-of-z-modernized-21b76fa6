package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private String providedSessionId;
  private String providedMenuId;
  private String providedAction;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    providedSessionId = "SESSION-123";
    aggregate = new TellerSessionAggregate(providedSessionId);
    // Hydrate with valid defaults
    aggregate.hydrate(
      "TELLER-1",
      true,   // authenticated
      "MAIN_MENU",
      Instant.now(),
      true
    );
  }

  @Given("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    providedSessionId = "SESSION-123";
  }

  @Given("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    providedMenuId = "ACCOUNT_DETAILS";
  }

  @Given("a valid action is provided")
  public void aValidActionIsProvided() {
    providedAction = "ENTER";
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    providedSessionId = "SESSION-UNAUTH";
    providedMenuId = "MAIN_MENU";
    providedAction = "ENTER";

    aggregate = new TellerSessionAggregate(providedSessionId);
    aggregate.hydrate(
      "TELLER-1",
      false,  // NOT authenticated
      "MAIN_MENU",
      Instant.now(),
      true
    );
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    providedSessionId = "SESSION-TIMEOUT";
    providedMenuId = "MAIN_MENU";
    providedAction = "ENTER";

    aggregate = new TellerSessionAggregate(providedSessionId);
    // Set last activity to 20 minutes ago (Timeout is 15)
    aggregate.hydrate(
      "TELLER-1",
      true,
      "MAIN_MENU",
      Instant.now().minus(Duration.ofMinutes(20)),
      true
    );
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesContext() {
    providedSessionId = "SESSION-CONTEXT";
    // Using specific input to trigger validation failure
    providedMenuId = "INVALID_CONTEXT";
    providedAction = "ENTER";

    aggregate = new TellerSessionAggregate(providedSessionId);
    aggregate.hydrate(
      "TELLER-1",
      true,
      "MAIN_MENU",
      Instant.now(),
      true
    );
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    Command cmd = new NavigateMenuCmd(providedSessionId, providedMenuId, providedAction);
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

    MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
    assertEquals(providedSessionId, event.aggregateId());
    assertEquals(providedMenuId, event.menuId());
    assertEquals("menu.navigated", event.type());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalStateException);
  }
}
