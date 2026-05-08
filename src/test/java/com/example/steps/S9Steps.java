package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
        // Configure valid state: generated, closed period, balances match
        aggregate.configureForTest(true, true, BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00));
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-violate-period");
        // Generated = true, Period Closed = false (Violation)
        aggregate.configureForTest(true, false, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-violate-balance");
        // Generated = true, Period Closed = true, but Opening != Previous Closing (Violation)
        aggregate.configureForTest(true, true, BigDecimal.valueOf(50.00), BigDecimal.valueOf(100.00));
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // ID is implicitly set in the aggregate constructor for these tests
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format will be provided in the When step via Command
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
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception, but command succeeded");
        // Check for the specific invariant messages defined in the aggregate
        String msg = capturedException.getMessage();
        assertTrue(
            msg.contains("closed period") || msg.contains("opening balance must exactly match"),
            "Error message did not match expected invariants: " + msg
        );
    }
}
