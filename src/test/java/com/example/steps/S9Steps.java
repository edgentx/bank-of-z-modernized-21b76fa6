package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
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
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        String id = "stmt-123";
        aggregate = new StatementAggregate(id);
        // Setup valid state: Closed period, valid balances
        aggregate.setAccountId("acct-456");
        aggregate.setClosedPeriod(true);
        aggregate.setOpeningBalance(BigDecimal.ZERO);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        String id = "stmt-invalid-period";
        aggregate = new StatementAggregate(id);
        aggregate.setClosedPeriod(false); // Violation: not closed
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        // This scenario implies an integrity check. In the aggregate implementation,
        // we trigger this error if the opening balance is null (corrupted state) or if we had access to the previous statement.
        // For this unit-test level step, we simulate a state where the aggregate decides it cannot proceed.
        // In the implemented logic, we check for null opening balance as a proxy for "uninitialized/invalid state" 
        // because the aggregate doesn't hold the previous statement's data directly.
        String id = "stmt-invalid-balance";
        aggregate = new StatementAggregate(id);
        aggregate.setClosedPeriod(true);
        aggregate.setOpeningBalance(null); // Simulating invalid state
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled in the aggregate setup
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Will be used in the command execution
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
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals("PDF", event.format());
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
