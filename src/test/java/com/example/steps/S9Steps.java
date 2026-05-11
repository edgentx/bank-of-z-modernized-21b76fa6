package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private final InMemoryStatementRepository repository = new InMemoryStatementRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        String id = "stmt-123";
        aggregate = new StatementAggregate(id);
        // Hydrate with valid state: closed, valid balances
        aggregate.hydrate("acct-456", true, false, "2023-10", 100.0, 200.0);
        repository.save(aggregate);
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled in the setup of the aggregate
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in the execution step
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        String id = "stmt-open";
        aggregate = new StatementAggregate(id);
        // Hydrate with invalid state: not closed
        aggregate.hydrate("acct-456", false, false, "2023-11", 0.0, 0.0);
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        String id = "stmt-bad-balance";
        aggregate = new StatementAggregate(id);
        // Hydrate with invalid state: negative opening balance triggers the simulated check
        // (In real logic, we'd look up previous statement, but here we simulate the invariant failure)
        aggregate.hydrate("acct-456", true, false, "2023-12", -1.0, 0.0);
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
