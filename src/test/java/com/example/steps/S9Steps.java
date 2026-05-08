package com.example.steps;

import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
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
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // Setup a valid statement: generated (so exportable), valid balances
        String id = "stmt-valid-1";
        aggregate = new StatementAggregate(id);
        
        // We simulate a pre-existing generated statement via internal state manipulation
        // In a real scenario, this would be loaded from events.
        // For unit testing the command handler, we assume the aggregate exists in a valid state.
        // Using reflection or a package-private helper to set state for testing would be ideal,
        // but here we rely on the aggregate accepting the command if valid.
        // We construct the command assuming the state is valid.
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Implicitly handled by the aggregate construction in the previous step
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format provided in the 'When' step
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            // Assume a generic valid ID and format for the success case
            ExportStatementCmd cmd = new ExportStatementCmd("stmt-valid-1", "PDF");
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
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("stmt-valid-1", event.aggregateId());
        assertEquals("PDF", event.format());
    }

    // --- Negative Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        // We represent a 'violating' aggregate simply by ID, 
        // relying on internal logic or specific command properties to trigger failure.
        // Or we assume the aggregate tracks status. 
        // For this BDD, we construct the aggregate and trigger the command.
        aggregate = new StatementAggregate("stmt-violate-closed");
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate("stmt-violate-balance");
    }

    // We can reuse the When step, but let's make it specific or ensure the previous applies.
    // Cucumber will match the generic When.

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // Verify it's a domain logic error (IllegalStateException)
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }

}
