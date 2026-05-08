package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a valid base aggregate
    private void createValidAggregate() {
        aggregate = new StatementAggregate("stmt-123");
        // Setup a valid closed period state
        aggregate.loadSnapshot(
            "acct-456",
            Instant.parse("2023-01-01T00:00:00Z"),
            Instant.parse("2023-01-31T23:59:59Z"),
            new BigDecimal("100.00"), // Opening matches previous closing (default 0)
            new BigDecimal("500.00"),
            true // isClosed
        );
        aggregate.setPreviousStatementClosingBalance(new BigDecimal("100.00"));
    }

    @Given("a valid Statement aggregate")
    public void a_valid_Statement_aggregate() {
        createValidAggregate();
    }

    @Given("a valid statementId is provided")
    public void a_valid_statementId_is_provided() {
        // Handled in 'When' command construction, assuming valid ID for aggregate existence
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in 'When' command construction
    }

    @When("the ExportStatementCmd command is executed")
    public void the_ExportStatementCmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);

        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_Statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-999");
        aggregate.loadSnapshot(
            "acct-999",
            Instant.now(),
            Instant.now(),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            false // isClosed = FALSE violates the invariant
        );
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_Statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-888");
        aggregate.loadSnapshot(
            "acct-888",
            Instant.now().minusSeconds(86400),
            Instant.now(),
            new BigDecimal("50.00"), // Opening Balance
            new BigDecimal("100.00"),
            true // isClosed = true
        );
        // Set previous closing balance to something different than opening
        aggregate.setPreviousStatementClosingBalance(new BigDecimal("60.00"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}