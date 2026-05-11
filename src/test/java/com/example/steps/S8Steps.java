package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {
    private StatementAggregate aggregate;
    private String accountNumber;
    private Instant periodEnd;
    private BigDecimal openingBalance;
    private Optional<BigDecimal> previousClosingBalance = Optional.empty();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_period_closed() {
        aggregate = new StatementAggregate("stmt-456");
        // Violation: Period is in the future
        this.periodEnd = Instant.now().plusSeconds(3600);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-789");
        // Violation: Opening balance does not match previous closing
        this.previousClosingBalance = Optional.of(new BigDecimal("100.00"));
        this.openingBalance = new BigDecimal("90.00"); // Mismatch
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.accountNumber = "ACC-334455";
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        this.periodEnd = Instant.now().minusSeconds(86400); // Yesterday
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            // If openingBalance wasn't set by a specific violation Given, default it
            if (openingBalance == null) {
                openingBalance = new BigDecimal("0.00");
            }
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                    aggregate.id(),
                    accountNumber,
                    periodEnd,
                    openingBalance,
                    previousClosingBalance
            );
            resultEvents = aggregate.execute(cmd);
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
        assertEquals(accountNumber, event.accountNumber());
        assertEquals(periodEnd, event.periodEnd());
        assertEquals(openingBalance, event.openingBalance());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on the specific violation, it could be IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
