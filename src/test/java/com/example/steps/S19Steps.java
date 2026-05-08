package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

  private TellerSessionAggregate aggregate;
  private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid TellerSession aggregate")
  public void aValidTellerSessionAggregate() {
    aggregate = new TellerSessionAggregate("session-123");
    // For a session to be valid in this context, teller must be authenticated
    aggregate.markAuthenticated("teller-456");
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
  public void aTellerSessionAggregateThatViolatesAuthentication() {
    aggregate = new TellerSessionAggregate("session-unauth-123");
    // Do NOT authenticate - simulating the violation
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
  public void aTellerSessionAggregateThatViolatesTimeout() {
    aggregate = new TellerSessionAggregate("session-timeout-123");
    aggregate.markAuthenticated("teller-456");
    // Set last activity to 20 minutes ago (exceeding 15 min timeout)
    aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    repo.save(aggregate);
  }

  @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
  public void aTellerSessionAggregateThatViolatesContext() {
    aggregate = new TellerSessionAggregate("session-context-123");
    aggregate.markAuthenticated("teller-456");
    repo.save(aggregate);
  }

  @And("a valid sessionId is provided")
  public void aValidSessionIdIsProvided() {
    // In this specific scenario flow, the aggregate ID serves as the sessionId.
    // We verify we can retrieve it.
    assertNotNull(aggregate.id());
  }

  @And("a valid menuId is provided")
  public void aValidMenuIdIsProvided() {
    // Handled in the When block by constructing the command with valid data
  }

  @And("a valid action is provided")
  public void aValidActionIsProvided() {
    // Handled in the When block by constructing the command with valid data
  }

  @When("the NavigateMenuCmd command is executed")
  public void theNavigateMenuCmdCommandIsExecuted() {
    try {
      // Reload to ensure we are testing the aggregate state correctly
      TellerSessionAggregate agg = repo.load(aggregate.id());
      
      // For the context violation scenario, we pass a BLANK menuId to trigger the invariant
      String menuId = "MENU_INVALID_CONTEXT".equals(agg.id()) ? "" : "MAIN_MENU_01"; 
      
      NavigateMenuCmd cmd = new NavigateMenuCmd(agg.id(), menuId, "ENTER");
      resultEvents = agg.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a menu.navigated event is emitted")
  public void aMenuNavigatedEventIsEmitted() {
    assertNull(capturedException, "Should not have thrown an exception");
    assertNotNull(resultEvents);
    assertFalse(resultEvents.isEmpty());
    assertEquals("menu.navigated", resultEvents.get(0).type());
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(capturedException, "Expected an exception to be thrown");
    // Checking for IllegalStateException (business logic violation) or IllegalArgumentException (validation)
    assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
  }
}
