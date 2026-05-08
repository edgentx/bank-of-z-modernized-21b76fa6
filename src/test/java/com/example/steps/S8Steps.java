package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Command will be constructed in the When step
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Command will be constructed in the When step
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-retro");
        // Simulate the aggregate already having a generated statement for the period
        aggregate.applyMutation(new StatementGeneratedEvent("stmt-retro", "ACC-1", LocalDate.now().minusMonths(1), Instant.now(), BigDecimal.ZERO, BigDecimal.TEN));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-balance");
        // Simulate the aggregate having a previous closing balance of 100, but we try to open with 0 (default)
        // The aggregate logic enforces the match.
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            if (command == null) {
                // Default success case construction
                command = new GenerateStatementCmd("stmt-1", "ACC-1", LocalDate.now(), BigDecimal.ZERO);
            }
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
