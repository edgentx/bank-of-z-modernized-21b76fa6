package com.example.steps;

import com.example.domain.statement.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // In a real scenario, this would be part of the command construction context
        // For this step, we just ensure the context is ready to build the command.
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Context holder
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            Command cmd = new GenerateStatementCmd(
                "stmt-1",
                "ACC-123",
                LocalDate.now().minusMonths(1),
                LocalDate.now(),
                BigDecimal.ZERO,
                BigDecimal.valueOf(1000)
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-1");
        // Simulate the aggregate already having a generated statement for a period in the far past
        // which represents a "closed" period.
        aggregate.execute(new GenerateStatementCmd(
            "stmt-1",
            "ACC-123",
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 31),
            BigDecimal.ZERO,
            BigDecimal.valueOf(500)
        ));
        aggregate.clearEvents(); // Clear the setup events
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // Check for specific exception types or messages if needed, e.g. IllegalStateException
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-2");
        aggregate.execute(new GenerateStatementCmd(
            "stmt-2",
            "ACC-999",
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 31),
            BigDecimal.ZERO,
            BigDecimal.valueOf(1000) // Closing balance of Jan
        ));
        aggregate.clearEvents();
        
        // The violation would happen if the next command provides an opening balance != 1000
    }
}
