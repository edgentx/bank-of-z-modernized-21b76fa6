package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
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
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.clearEvents();
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Context setup handled in 'When' step via Command construction
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Context setup handled in 'When' step via Command construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

            // Default valid command (closed period)
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                    "stmt-123",
                    "ACC-001",
                    twoDaysAgo,
                    yesterday,
                    new BigDecimal("100.00"),
                    new BigDecimal("150.00")
            );

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);

        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("ACC-001", event.accountNumber());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-999");
        aggregate.clearEvents();
        // This setup prepares the aggregate for a scenario where the COMMAND will violate the rule.
    }

    @When("the GenerateStatementCmd command is executed with a future period")
    public void the_generate_statement_cmd_command_is_executed_with_future_period() {
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            LocalDate today = LocalDate.now();

            GenerateStatementCmd cmd = new GenerateStatementCmd(
                    "stmt-999",
                    "ACC-001",
                    today,
                    tomorrow,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );

            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-888");
        aggregate.clearEvents();
        // Simulate a previous closing balance of 100.00
        aggregate.setPreviousClosingBalance(new BigDecimal("100.00"));
    }

    @When("the GenerateStatementCmd command is executed with mismatched balance")
    public void the_generate_statement_cmd_command_is_executed_with_mismatched_balance() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

            // Opening balance is 50.00, but previous was 100.00
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                    "stmt-888",
                    "ACC-002",
                    twoDaysAgo,
                    yesterday,
                    new BigDecimal("50.00"), // Mismatch
                    new BigDecimal("150.00")
            );

            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        // Verify the error message matches the specific invariant violation
        assertTrue(capturedException.getMessage().contains("Statement opening balance must exactly match") ||
                capturedException.getMessage().contains("closed period"));
    }

    // Glue for the other scenarios that reuse the "When" step name but need specific data setup
    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed_violating_period() {
         // Delegate to the specific violation method to ensure data context is correct
         if(aggregate.id().equals("stmt-999")) {
             the_generate_statement_cmd_command_is_executed_with_future_period();
         } else if (aggregate.id().equals("stmt-888")) {
             the_generate_statement_cmd_command_is_executed_with_mismatched_balance();
         } else {
             the_generate_statement_cmd_command_is_executed();
         }
    }
}