package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.*;
import com.example.domain.statement.repository.StatementRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class S8Steps {

    private final StatementRepository repository = new InMemoryStatementRepository();
    private StatementAggregate aggregate;
    private Throwable thrownException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new StatementAggregate(id);
        repository.save(aggregate);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Context setup for the command
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Context setup for the command
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new StatementAggregate(id);
        // Simulate a state where generation is not allowed (e.g. already generated)
        // For this specific logic, we rely on the Aggregate's internal state check.
        // Assuming the command implies retroactive alteration or duplicate generation.
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        String id = UUID.randomUUID().toString();
        this.aggregate = new StatementAggregate(id);
        // Context where opening balance does not match previous closing
        // The command passed to execute would contain mismatched data.
        repository.save(aggregate);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            // We construct a valid command for the success case.
            // For failure cases, the construction might differ or the internal state rejects it.
            // Here we assume the violation is handled by the aggregate logic or command data mismatch.
            String id = aggregate.id();
            String account = "ACC-123";
            LocalDate periodEnd = LocalDate.now();
            BigDecimal openingBal = BigDecimal.ZERO;
            BigDecimal closingBal = new BigDecimal("100.00");
            
            // Scenario 2: Retroactive (assuming command represents a past date or aggregate prevents it)
            // Scenario 3: Opening balance mismatch (assume command has mismatched opening balance)
            
            Command cmd = new GenerateStatementCmd(id, account, periodEnd, openingBal, closingBal);
            List<DomainEvent> events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                repository.save(aggregate);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.thrownException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        List<DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected an event to be emitted");
        Assertions.assertEquals("statement.generated", events.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
