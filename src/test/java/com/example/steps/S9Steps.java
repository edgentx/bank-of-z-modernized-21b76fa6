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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

  private StatementAggregate aggregate;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid Statement aggregate")
  public void aValidStatementAggregate() {
    aggregate = new StatementAggregate("stmt-123");
    aggregate.setOpeningBalance(new BigDecimal("100.00"));
    aggregate.setClosingBalance(new BigDecimal("100.00"));
    aggregate.markPeriodClosed();
  }

  @And("a valid statementId is provided")
  public void aValidStatementIdIsProvided() {
    // Handled in aggregate constructor
  }

  @And("a valid format is provided")
  public void aValidFormatIsProvided() {
    // Handled in command execution
  }

  @When("the ExportStatementCmd command is executed")
  public void theExportStatementCmdCommandIsExecuted() {
    try {
      Command cmd = new ExportStatementCmd("stmt-123", "PDF");
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a statement.exported event is emitted")
  public void aStatementExportedEventIsEmitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
    StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
    assertEquals("PDF", event.format());
    assertEquals("stmt-123", event.aggregateId());
  }

  @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
  public void aStatementAggregateThatViolatesClosedPeriod() {
    aggregate = new StatementAggregate("stmt-invalid-period");
    aggregate.setOpeningBalance(new BigDecimal("100.00"));
    // Period is NOT closed
  }

  @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
  public void aStatementAggregateThatViolatesBalanceMatching() {
    aggregate = new StatementAggregate("stmt-invalid-balance");
    aggregate.setOpeningBalance(new BigDecimal("100.00"));
    aggregate.setClosingBalance(new BigDecimal("200.00")); // Mismatch
    aggregate.markPeriodClosed();
  }

  @Then("the command is rejected with a domain error")
  public void theCommandIsRejectedWithADomainError() {
    assertNotNull(caughtException);
    assertTrue(caughtException instanceof IllegalStateException);
  }
}