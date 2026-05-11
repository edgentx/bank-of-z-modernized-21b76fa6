package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Context managed in the execution step
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Context managed in the execution step
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default valid data if command wasn't customized by specific violation scenarios
        if (command == null) {
            Instant past = Instant.now().minus(30, ChronoUnit.DAYS);
            command = new GenerateStatementCmd(
                    "stmt-123",
                    "acc-456",
                    past.minus(1, ChronoUnit.DAYS),
                    past,
                    BigDecimal.ZERO,
                    new BigDecimal("100.00"),
                    null // No previous balance for a happy path new account
            );
        }

        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("acc-456", event.accountNumber());
    }

    // --- Failure Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-fail-1");
        // Period is in the future
        Instant future = Instant.now().plus(1, ChronoUnit.DAYS);
        command = new GenerateStatementCmd(
                "stmt-fail-1",
                "acc-789",
                future.minus(1, ChronoUnit.DAYS),
                future,
                BigDecimal.ZERO,
                BigDecimal.TEN,
                null
        );
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-fail-2");
        Instant past = Instant.now().minus(30, ChronoUnit.DAYS);
        command = new GenerateStatementCmd(
                "stmt-fail-2",
                "acc-101",
                past.minus(1, ChronoUnit.DAYS),
                past,
                new BigDecimal("50.00"), // Opening is 50
                new BigDecimal("100.00"),
                new BigDecimal("100.00") // Previous closing was 100 (Mismatch!)
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception but command succeeded");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
