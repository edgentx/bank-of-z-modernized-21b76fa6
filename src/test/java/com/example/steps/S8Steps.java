package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import com.example.domain.statement.repository.StatementRepository;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private StatementRepository repository = new InMemoryStatementRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test Data
    private static final String VALID_ACCOUNT = "ACC-12345";
    private static final LocalDate VALID_PERIOD_END = LocalDate.of(2023, Month.OCTOBER, 31);
    private static final BigDecimal VALID_OPENING_BALANCE = new BigDecimal("1000.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1");
        repository.save(aggregate);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Setup handled in 'a_valid_statement_aggregate'
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Setup handled in 'a_valid_statement_aggregate'
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            aggregate.id(),
            VALID_ACCOUNT,
            VALID_PERIOD_END,
            VALID_OPENING_BALANCE
        );

        try {
            // Reload aggregate from repo to ensure persistence is verified (though in-mem)
            StatementAggregate agg = repository.findById(aggregate.id()).orElseThrow();
            resultEvents = agg.execute(cmd);
            repository.save(agg); // Persist changes
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
        assertEquals("statement.generated", event.type());
        assertEquals(VALID_ACCOUNT, event.accountNumber());
        assertEquals(VALID_PERIOD_END, event.periodEnd());
    }

    // --- Rejection Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-retro");
        aggregate.setExistingStatement(VALID_ACCOUNT, VALID_PERIOD_END, VALID_OPENING_BALANCE, BigDecimal.ZERO);
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-balance-mismatch");
        // Simulate the previous statement had a closing balance of 500.00, so the new opening must be 500.00
        aggregate.setExpectedOpeningBalance(new BigDecimal("500.00"));
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
