package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
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
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-123");
        // Setup valid state: OPEN balances, CLOSED period
        aggregate.loadSnapshot(
                "acct-456",
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                Instant.now(),
                "CLOSED"
        );
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by the aggregate construction in the previous step or implicit in command execution
        assertNotNull(aggregate);
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in command execution
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("PDF", event.format());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-violation-period");
        // Setup invalid state: OPEN period
        aggregate.loadSnapshot(
                "acct-violation",
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                Instant.now(),
                "OPEN" // Violates invariant
        );
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        this.aggregate = new StatementAggregate("stmt-violation-bal");
        // Setup invalid state: NULL opening balance (simulating mismatch)
        aggregate.loadSnapshot(
                "acct-violation",
                null, // Violates invariant check in aggregate
                new BigDecimal("200.00"),
                Instant.now(),
                "CLOSED"
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().length() > 0);
    }
}
