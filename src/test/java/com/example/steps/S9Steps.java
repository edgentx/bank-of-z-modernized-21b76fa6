package com.example.steps;

import com.example.domain.account.model.ExportStatementCmd;
import com.example.domain.account.model.StatementAggregate;
import com.example.domain.account.model.StatementExportedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for S-9: ExportStatementCmd.
 */
public class S9Steps {

    private StatementAggregate aggregate;
    private ExportStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-123");
        // Setup valid state
        aggregate.setAccountDetails("acct-456", new BigDecimal("100.00"), new BigDecimal("200.00"));
        aggregate.markPeriodAsClosed();
        aggregate.markOpeningBalanceVerified();
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-violation-period");
        aggregate.setAccountDetails("acct-456", new BigDecimal("100.00"), new BigDecimal("200.00"));
        aggregate.markPeriodAsOpen(); // Violation
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        this.aggregate = new StatementAggregate("stmt-violation-balance");
        aggregate.setAccountDetails("acct-456", new BigDecimal("100.00"), new BigDecimal("200.00"));
        aggregate.markPeriodAsClosed();
        aggregate.markOpeningBalanceUnverified(); // Violation
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by aggregate construction, but we ensure the command uses it later
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in command construction
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            this.cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            this.resultEvents = aggregate.execute(cmd);
            this.capturedException = null;
        } catch (Exception e) {
            this.capturedException = e;
            this.resultEvents = null;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof StatementExportedEvent, "Expected StatementExportedEvent");
        
        StatementExportedEvent exportedEvent = (StatementExportedEvent) event;
        assertEquals("statement.exported", exportedEvent.type());
        assertEquals("PDF", exportedEvent.format());
        assertTrue(exportedEvent.artifactLocation().contains("stmt-123"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
