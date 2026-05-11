package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private String statementId;
    private String format;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.statementId = "stmt-123";
        this.aggregate = new StatementAggregate(statementId);
        // Default valid state: closed period, valid balances
        aggregate.configureForTest(true, true);
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // statementId already initialized in "a valid Statement aggregate"
        assertNotNull(statementId);
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        this.format = "PDF";
        assertNotNull(format);
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            Command cmd = new ExportStatementCmd(statementId, format);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals(statementId, event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.statementId = "stmt-violation-period";
        this.aggregate = new StatementAggregate(statementId);
        // Set periodClosed to false to violate the rule "must be generated for a closed period"
        aggregate.configureForTest(false, true);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        this.statementId = "stmt-violation-balance";
        this.aggregate = new StatementAggregate(statementId);
        // Set openingBalanceValid to false to trigger the specific error
        aggregate.configureForTest(true, false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify no events were produced due to failure
        assertNull(resultEvents);
    }
}
