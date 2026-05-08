package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
  private Exception capturedException;
  private Iterable<DomainEvent> resultEvents;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    aggregate.markAuthenticated(); // Assume valid implies authenticated for success path
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuth() {
    aggregate = new TellerSessionAggregate("session-401");
    // Deliberately not calling markAuthenticated()
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate("session-408");
    aggregate.markAuthenticated();
    aggregate.markExpired(); // Force expiration
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesNavState() {
    aggregate = new TellerSessionAggregate("session-400");
    aggregate.markAuthenticated();
    repo.save(aggregate);
  }

  @And("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // Handled implicitly via aggregate creation
  }

  @And("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    // Will be used in command construction
  }

  @And("a valid action is provided")
  public void aValidActionIsProvided() {
    // Will be used in command construction
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      String menuId = "MAIN_MENU";
      String action = "ENTER";
      
      // For the context violation scenario, we simulate an invalid action
      if (aggregate.id().equals("session-400")) {
         action = ""; // Invalid action
      }

      NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertTrue(resultEvents.iterator().hasNext());
    DomainEvent event = resultEvents.iterator().next();
    assertEquals("menu.navigated", event.type());
    assertNull(capturedException);
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException);
    // We check for IllegalStateException or IllegalArgumentException as our domain errors
    assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    System.out.println("Expected error caught: " + capturedException.getMessage());
  }
}
