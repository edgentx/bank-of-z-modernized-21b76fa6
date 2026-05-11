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
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {
    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Parameters will be built in the When step using a default or specific value
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Parameters will be built in the When step
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-456");
        // Simulate closed period by executing once to set state to generated
        GenerateStatementCmd firstCmd = new GenerateStatementCmd(
            "stmt-456", "acc-1", Instant.now(), BigDecimal.ZERO, BigDecimal.TEN
        );
        aggregate.execute(firstCmd);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-789");
        // We are creating a command later that will have the wrong opening balance relative to what the aggregate expects
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default valid values
        String id = aggregate.id();
        String acc = "acc-12345";
        Instant end = Instant.now();
        BigDecimal opening = BigDecimal.ZERO;
        BigDecimal closing = BigDecimal.TEN;

        // Adjust for specific violation scenarios
        if (aggregate.id().equals("stmt-789")) {
            // Violation: Passing wrong opening balance (e.g., non-zero when aggregate expects zero, or mismatch)
            // Since aggregate starts fresh, we just set a mismatch arbitrarily for the test
            opening = new BigDecimal("999.99");
        }

        cmd = new GenerateStatementCmd(id, acc, end, opening, closing);
        try {
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("statement.generated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Check for specific error messages or types if necessary
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
