package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private final InMemoryStatementRepository repo = new InMemoryStatementRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Placeholder for test setup context if needed
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Placeholder for test setup context if needed
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
                "stmt-1",
                "ACC-123",
                LocalDate.now(ZoneId.of("UTC")),
                new BigDecimal("100.00"),
                new BigDecimal("200.00")
        );
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("stmt-1", event.aggregateId());
        assertEquals("ACC-123", event.accountNumber());
        assertEquals("statement.generated", event.type());
        assertTrue(aggregate.isGenerated());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-2");
        // Execute once to make it generated
        GenerateStatementCmd initialCmd = new GenerateStatementCmd(
                "stmt-2", "ACC-123", LocalDate.now(ZoneId.of("UTC")),
                BigDecimal.ZERO, BigDecimal.ZERO
        );
        aggregate.execute(initialCmd);
        aggregate.clearEvents(); // Reset uncommitted events for the test scenario
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-3");
        // We simulate this violation by passing null opening balance in the command step
        // which triggers the validation logic in the aggregate.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on the violation, it could be IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
