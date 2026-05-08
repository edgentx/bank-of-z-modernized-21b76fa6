package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

  private StatementAggregate aggregate;
  private String accountNumber;
  private LocalDate periodEnd;
  private LocalDate periodStart;
  private BigDecimal openingBalance;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid Statement aggregate")
  public void a_valid_statement_aggregate() {
    this.aggregate = new StatementAggregate("stmt-123");
    // Reset the static mock state for balance checks
    StatementAggregate.setLastKnownClosingBalance(BigDecimal.ZERO);
  }

  @And("a valid accountNumber is provided")
  public void a_valid_account_number_is_provided() {
    this.accountNumber = "ACC-334455";
  }

  @And("a valid periodEnd is provided")
  public void a_valid_period_end_is_provided() {
    // Period must be closed (in the past)
    this.periodEnd = LocalDate.now().minusDays(1);
    this.periodStart = this.periodEnd.minusMonths(1);
    this.openingBalance = BigDecimal.ZERO;
  }

  @When("the GenerateStatementCmd command is executed")
  public void the_generate_statement_cmd_command_is_executed() {
    try {
      GenerateStatementCmd cmd = new GenerateStatementCmd(
          "stmt-123",
          accountNumber,
          periodStart,
          periodEnd,
          openingBalance
      );
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a statement.generated event is emitted")
  public void a_statement_generated_event_is_emitted() {
    assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);

    StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
    assertEquals("stmt-123", event.aggregateId());
    assertEquals("ACC-334455", event.accountNumber());
  }

  // --- Negative Scenarios ---

  @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
  public void a_statement_aggregate_that_violates_closed_period() {
    this.aggregate = new StatementAggregate("stmt-bad-period");
    this.accountNumber = "ACC-001";
    this.periodStart = LocalDate.now().minusMonths(1);
    // Future period -> violation
    this.periodEnd = LocalDate.now().plusDays(1); 
    this.openingBalance = BigDecimal.ZERO;
  }

  @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
  public void a_statement_aggregate_that_violates_opening_balance() {
    this.aggregate = new StatementAggregate("stmt-bad-bal");
    this.accountNumber = "ACC-001";
    this.periodEnd = LocalDate.now().minusDays(1);
    this.periodStart = this.periodEnd.minusMonths(1);
    
    // Simulate that the system expects 100.00, but we provide 90.00
    StatementAggregate.setLastKnownClosingBalance(new BigDecimal("100.00"));
    this.openingBalance = new BigDecimal("90.00");
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException, "Expected a domain error exception, but command succeeded.");
    assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
  }
}
