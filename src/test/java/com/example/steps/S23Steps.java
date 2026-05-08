package com.example.steps;

import com.example.domain.legacy.model.EvaluateRoutingCmd;
import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S23Steps {

  private LegacyTransactionRoute aggregate;
  private EvaluateRoutingCmd command;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid LegacyTransactionRoute aggregate")
  public void a_valid_legacy_transaction_route_aggregate() {
    this.aggregate = new LegacyTransactionRoute("route-123");
  }

  @And("a valid transactionType is provided")
  public void a_valid_transaction_type_is_provided() {
    // Handled in When step via command construction for simplicity in this context
  }

  @And("a valid payload is provided")
  public void a_valid_payload_is_provided() {
    // Handled in When step via command construction
  }

  @When("the EvaluateRoutingCmd command is executed")
  public void the_evaluate_routing_cmd_command_is_executed() {
    try {
      // Default to a valid command state
      String payload = "valid payload content";
      this.command = new EvaluateRoutingCmd("route-123", "WIRE", payload, 1);
      this.resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      this.capturedException = e;
    }
  }

  @When("the EvaluateRoutingCmd command is executed with rule version {int}")
  public void the_evaluate_routing_cmd_command_is_executed_with_version(int version) {
    try {
      this.command = new EvaluateRoutingCmd("route-123", "WIRE", "payload", version);
      this.resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      this.capturedException = e;
    }
  }

  @Then("a routing.evaluated event is emitted")
  public void a_routing_evaluated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertEquals("routing.evaluated", resultEvents.get(0).type());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
  }

  // Specific scenarios for invariants

  @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
  public void a_route_aggregate_violating_uniqueness() {
    // The violation is triggered by the command/logic, not the aggregate state directly in this model.
    // However, to fit the Gherkin, we setup the aggregate.
    this.aggregate = new LegacyTransactionRoute("route-bad");
  }

  @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
  public void a_route_aggregate_violating_versioning() {
    this.aggregate = new LegacyTransactionRoute("route-bad-version");
  }

  @When("the EvaluateRoutingCmd command is executed violating version constraint")
  public void execute_bad_version() {
    the_evaluate_routing_cmd_command_is_executed_with_version(0);
  }
}
