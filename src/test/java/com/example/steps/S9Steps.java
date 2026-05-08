package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

  private StatementAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid Statement aggregate")
  public void a_valid_Statement_aggregate() {
    aggregate = new StatementAggregate("stmt-123");
  }

  @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
  public void a_statement_aggregate_in_open_period() {
    aggregate = new StatementAggregate("stmt-open");
    aggregate.markPeriodOpen();
  }

  @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
  public void a_statement_aggregate_with_balance_mismatch() {
    aggregate = new StatementAggregate("stmt-unbalanced");
    aggregate.markUnbalanced();
  }

  @And("a valid statementId is provided")
  public void a_valid_statementId_is_provided() {
    // IDs are hardcoded in constructor calls for simplicity in this phase
  }

  @And("a valid format is provided")
  public void a_valid_format_is_provided() {
    // Format provided in command execution
  }

  @When("the ExportStatementCmd command is executed")
  public void the_export_statement_cmd_command_is_executed() {
    try {
      Command cmd = new ExportStatementCmd(aggregate.id(), "PDF");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a statement.exported event is emitted")
  public void a_statement_exported_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
    StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
    assertEquals("statement.exported", event.type());
    assertEquals("PDF", event.format());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException);
  }
}
