package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private DomainEvent resultEvent;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by the aggregate constructor in the previous step
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format is part of the command, handled in 'When'
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEvent = events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvent, "Event should not be null");
        assertTrue(resultEvent instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
        StatementExportedEvent event = (StatementExportedEvent) resultEvent;
        assertEquals("statement.exported", event.type());
        assertEquals("PDF", event.format());
        assertNotNull(event.artifactLocation());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-violate-period");
        aggregate.setClosedPeriod(false);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-violate-balance");
        aggregate.setOpeningBalanceValid(false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
