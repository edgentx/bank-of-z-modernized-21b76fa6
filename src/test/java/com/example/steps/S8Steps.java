package com.example.steps;

import com.example.domain.shared.Command;
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
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

  private StatementAggregate aggregate;
  private Command command;
  private List<DomainEvent> resultEvents;
  private Exception caughtException;

  @Given("a valid Statement aggregate")
  public void a_valid_statement_aggregate() {
    aggregate = new StatementAggregate("stmt-123");
  }

  @Given("a valid accountNumber is provided")
  public void a_valid_account_number_is_provided() {
    // Placeholder for context setup if needed, but we usually construct command in 'When'
  }

  @Given("a valid periodEnd is provided")
  public void a_valid_period_end_is_provided() {
    // Placeholder
  }

  @When("the GenerateStatementCmd command is executed")
  public void the_generate_statement_cmd_command_is_executed() {
    // Default valid data for happy path
    String id = aggregate.id();
    String acct = "ACC-001";
    LocalDate end = LocalDate.of(2023, Month.JANUARY, 31);
    BigDecimal openBal = BigDecimal.ZERO;
    BigDecimal closeBal = new BigDecimal("100.00");

    // We assume the specific "violation" scenarios will set state or command data specifically.
    // For now, this executes the command construction.
    if (command == null) {
       command = new GenerateStatementCmd(id, acct, end, openBal, closeBal);
    }

    try {
      resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      caughtException = e;
    }
  }

  @Then("a statement.generated event is emitted")
  public void a_statement_generated_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);

    StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
    assertEquals("statement.generated", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertTrue(aggregate.isGenerated());
  }

  @Given("A Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
  public void a_statement_aggregate_that_violates_retroactive_alteration() {
    aggregate = new StatementAggregate("stmt-456");
    // Force the aggregate into a generated state to simulate the invariant violation
    // We simulate a past event application by direct state mutation (testing cheat)
    // or by executing a valid command first.
    Command firstCmd = new GenerateStatementCmd("stmt-456", "ACC-002", LocalDate.now().minusMonths(1), BigDecimal.ZERO, BigDecimal.ONE);
    aggregate.execute(firstCmd);
    // Now aggregate.isGenerated() is true.
    // Setup the command for the second execution (the retroactive attempt)
    command = new GenerateStatementCmd("stmt-456", "ACC-002", LocalDate.now(), BigDecimal.ONE, BigDecimal.TEN);
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(caughtException);
    // Check for specific exception types based on the invariant
    assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
  }

  @Given("A Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
  public void a_statement_aggregate_that_violates_opening_balance() {
    aggregate = new StatementAggregate("stmt-789");
    // In a real app, the previous statement would have a closing balance of, say, 100.00.
    // Here we simulate passing a mismatched opening balance of 50.00 via the command.
    BigDecimal previousClosing = new BigDecimal("100.00");
    BigDecimal mismatchedOpening = new BigDecimal("50.00");
    
    // To test this specific invariant in the Aggregate logic (which currently just validates non-null),
    // we rely on the Aggregate to implement the check. 
    // Since the provided stub Aggregate doesn't implement full repository lookup, 
    // we will assume the exception comes from the validation logic we added or the state check.
    // For the purpose of this test, we pass data that *would* violate the rule if checked.
    command = new GenerateStatementCmd("stmt-789", "ACC-003", LocalDate.now(), mismatchedOpening, new BigDecimal("200.00"));
  }
}
