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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_Statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.setOpeningBalance(BigDecimal.ZERO);
        aggregate.markPeriodClosed();
    }

    @Given("a valid statementId is provided")
    public void a_valid_statementId_is_provided() {
        // Handled by the aggregate construction in the previous step
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled by the command construction in the When step
    }

    @When("the ExportStatementCmd command is executed")
    public void the_ExportStatementCmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
        assertEquals("statement.exported", resultEvents.get(0).type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-invalid-period");
        aggregate.setOpeningBalance(BigDecimal.TEN);
        // Do NOT mark period closed (period is open) -> violates the requirement
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Should be an IllegalStateException");
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-invalid-balance");
        aggregate.markPeriodClosed(); // Period is closed
        // Set opening balance to null or invalid value to simulate mismatch with previous closing balance
        aggregate.setOpeningBalance(null);
    }
}
