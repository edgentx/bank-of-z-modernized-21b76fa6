package com.example.steps;

import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception thrownException;
    private List<com.example.domain.shared.DomainEvent> events;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // Setup a valid aggregate state
        aggregate = new StatementAggregate("stmt-123");
        aggregate.generate("acct-1", LocalDate.now().minusMonths(1), LocalDate.now(), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN);
        aggregate.clearEvents(); // Clear generation events to isolate the test
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Implicit in the aggregate setup, but we ensure it matches the command
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format will be passed in the command (PDF)
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
        try {
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Should emit exactly one event");
        assertTrue(events.get(0) instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
    }

    @Given("A Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-invalid-1");
        // Manually create a state that violates the invariant without executing the 'generate' command logic
        // This simulates a loaded aggregate from a read model that is in a bad state, or data manipulation.
        // Since state is private, we might rely on a specific business rule if available, but here we assume
        // the invariant implies the statement is editable/future or similar.
        // For the sake of the unit test, we assume the aggregate has a flag or method to simulate this state.
        // However, since we can't set state directly, we'll rely on the fact that a 'non-generated' statement violates this if generation is the only way to close a period.
        // Let's assume a fresh aggregate (not generated) attempts export, or we modify the aggregate to support this state.
        // Actually, the scenario says "Given a Statement aggregate that violates...".
        // The easiest way to violate "Generated for closed period" is to be in a state where it's NOT generated yet, or the period is open.
        // Let's assume the aggregate is created but not yet generated.
        aggregate = new StatementAggregate("stmt-open");
    }

    @Given("A Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate("stmt-bad-math");
        // Generate a statement with mismatched balances (opening 0, closing 100, sum 50)
        // The generate command logic usually validates this. To simulate the violation, we might need
        // a backdoor or a specific constructor variant not shown in existing patterns.
        // Or, we can assume the previous statement's closing balance was different.
        // Let's assume we can create one in a valid state first, then the check relies on external data (previous statement).
        // Since the test is unit-based, we might mock the repository response, but the prompt says "In-memory aggregate".
        // The pattern says "execute(cmd)". Invariants are often enforced in execute.
        // We will simulate the aggregate being loaded in a state that is invalid.
        aggregate.generate("acct-1", LocalDate.now().minusMonths(1), LocalDate.now(), BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE); // Sum(10) != End(1)
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Exception should have been thrown");
        assertTrue(thrownException instanceof DomainException || thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
