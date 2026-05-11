package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number will be constructed in the When step using a placeholder
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Period end will be constructed in the When step
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Placeholder for 'And' usage
    }

    @And("a valid periodEnd is provided")
    public void aValidPeriodEndIsProvided() {
        // Placeholder for 'And' usage
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default valid values for the happy path
        // Using a past date for periodEnd (closed period)
        Instant pastDate = Instant.now().minusSeconds(86400);
        BigDecimal balance = new BigDecimal("100.00");
        
        // Ensure cmd is initialized if not done by previous steps
        if (cmd == null) {
            cmd = new GenerateStatementCmd("stmt-123", "acc-456", pastDate, balance, balance);
        }

        try {
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("statement.generated", event.type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-invalid-period");
        // Future date violates the closed period invariant
        Instant futureDate = Instant.now().plusSeconds(86400);
        cmd = new GenerateStatementCmd("stmt-invalid-period", "acc-456", futureDate, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-invalid-balance");
        Instant pastDate = Instant.now().minusSeconds(86400);
        // Opening balance (100) != Previous Closing balance (200)
        cmd = new GenerateStatementCmd("stmt-invalid-balance", "acc-456", pastDate, new BigDecimal("100.00"), new BigDecimal("200.00"));
    }
}