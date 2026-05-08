package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S9Steps {

    private StatementAggregate aggregate;
    private ExportStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.setClosedPeriod(true);
        aggregate.setOpeningBalance("100.00");
        aggregate.setGenerated(true);
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled in command creation below
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in command creation below
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            cmd = new ExportStatementCmd("stmt-123", "PDF", "100.00");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        assertEquals("statement.exported", resultEvents.get(0).type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_period_closed() {
        aggregate = new StatementAggregate("stmt-invalid-period");
        aggregate.setClosedPeriod(false); // Violation: period is open
        aggregate.setOpeningBalance("100.00");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-invalid-balance");
        aggregate.setClosedPeriod(true);
        aggregate.setOpeningBalance("100.00");
        // In the When step, we will provide a previous closing balance that differs
    }

    // Specific When/Then combination for the balance violation scenario to override the generic one
    @When("the ExportStatementCmd command is executed with mismatched balances")
    public void the_export_statement_cmd_command_is_executed_with_mismatched_balances() {
        try {
            // Provide 200.00 when aggregate expects 100.00
            cmd = new ExportStatementCmd("stmt-invalid-balance", "PDF", "200.00");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
