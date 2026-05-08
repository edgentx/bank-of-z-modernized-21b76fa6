package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception capturedException;
    private List resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-valid-123");
        // Apply defaults that make the statement valid for export
        aggregate.setPeriodClosed(true);
        aggregate.setStartDate(LocalDate.of(2023, 1, 1));
        aggregate.setEndDate(LocalDate.of(2023, 1, 31));
        aggregate.setOpeningBalance(BigDecimal.valueOf(100.00));
        aggregate.setPreviousClosingBalance(BigDecimal.valueOf(100.00));
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by the aggregate constructor in the 'Given a valid Statement aggregate' step
        assertNotNull(aggregate.id());
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // We will provide the format in the When step via the command
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("PDF", event.format());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-invalid-period");
        // Setting periodClosed to false simulates the violation for the purposes of the command logic
        // (or the date logic). The aggregate will check this invariant.
        aggregate.setPeriodClosed(false); 
        aggregate.setPreviousClosingBalance(BigDecimal.ZERO);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-invalid-balance");
        aggregate.setPeriodClosed(true);
        // Opening balance (default 0) does not match previous closing balance (100)
        aggregate.setPreviousClosingBalance(BigDecimal.valueOf(100.00));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
