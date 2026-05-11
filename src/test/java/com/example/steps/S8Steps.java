package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import com.example.domain.statement.repository.StatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private final StatementRepository repository = new InMemoryStatementRepository();
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new StatementAggregate(id);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // State setup handled in When block
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // State setup handled in When block
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        executeCommand(Instant.now().minusSeconds(86400), Instant.now().minusSeconds(3600), new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("150.00"));
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(aggregate);
        List<com.example.domain.shared.DomainEvent> events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.get(0) instanceof StatementGeneratedEvent);

        StatementGeneratedEvent event = (StatementGeneratedEvent) events.get(0);
        assertEquals("statement.generated", event.type());
    }

    // --- Error Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate(UUID.randomUUID().toString());
    }

    @When("the GenerateStatementCmd command is executed with future date")
    public void the_generate_statement_cmd_command_is_executed_with_future_date() {
        // Future date
        executeCommand(Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200), new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("150.00"));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_continuity() {
        aggregate = new StatementAggregate(UUID.randomUUID().toString());
    }

    @When("the GenerateStatementCmd command is executed with mismatched balance")
    public void the_generate_statement_cmd_command_is_executed_with_mismatched_balance() {
        // Opening 100, PrevClose 50 -> Mismatch
        executeCommand(Instant.now().minusSeconds(86400), Instant.now().minusSeconds(3600), new BigDecimal("100.00"), new BigDecimal("50.00"), new BigDecimal("150.00"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Helper
    private void executeCommand(Instant start, Instant end, BigDecimal opening, BigDecimal prevClosing, BigDecimal closing) {
        try {
            String id = UUID.randomUUID().toString();
            // Need to re-instantiate aggregate if ID changed, but for simplicity we assume 'aggregate' is the target
            // The repository usually saves it, here we act directly.
            
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                id, "ACC-123", start, end, opening, closing, prevClosing
            );
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
