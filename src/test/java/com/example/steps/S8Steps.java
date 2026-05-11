package com.example.steps;

import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // Setup a clean aggregate state representing a new statement being generated
        aggregate = new StatementAggregate("stmt-123");
        // Assume validity context is implicitly set for success case
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Data stored for use in When
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Data stored for use in When
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-bad-1");
        // In a real scenario, we might hydrate the aggregate to a state where period is closed.
        // Here we rely on the command inputs to trigger the validation error.
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-bad-2");
        // Simulate a state where balance mismatch exists.
        // This relies on the validation logic within the command execution.
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            // We construct the command dynamically based on the scenario context.
            // For the "valid" scenario, we provide good data.
            // For the "violates" scenarios, the aggregate context or specific bad data would be passed.
            // To keep step definitions simple and reusable:
            // We assume the system defaults to testing valid logic unless the aggregate setup forces failure.

            String accountId = "acct-100";
            LocalDate periodEnd = LocalDate.now().minusMonths(1);
            BigDecimal openingBalance = BigDecimal.ZERO;
            BigDecimal closingBalance = new BigDecimal("100.00");

            // If the aggregate ID indicates a specific violation test, we can mock the inputs accordingly.
            if (aggregate.id().contains("bad-1")) {
                // Passing a future date for period end to simulate "closed period" constraint if logic checks dates,
                // or rely on specific invariant logic. For this exercise, we assume standard valid inputs
                // unless specific data is injected.
                // NOTE: The prompt implies the aggregate state carries the violation context, 
                // but without persistence, we assume the Command carries the balance to check.
                command = new GenerateStatementCmd("stmt-bad-1", accountId, LocalDate.now().plusDays(1), BigDecimal.ZERO, BigDecimal.ZERO);
            } else if (aggregate.id().contains("bad-2")) {
                // Mismatch: Opening 100, Previous Closing 200
                command = new GenerateStatementCmd("stmt-bad-2", accountId, periodEnd, new BigDecimal("100.00"), new BigDecimal("200.00"));
            } else {
                // Valid command
                command = new GenerateStatementCmd("stmt-123", accountId, periodEnd, openingBalance, closingBalance);
            }

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
        assertEquals("statement.generated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNull(resultEvents, "Events should be null when exception is thrown");
        assertNotNull(capturedException, "An exception should have been thrown");
        // Checking for IllegalArgumentException or IllegalStateException is typical domain error pattern
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
