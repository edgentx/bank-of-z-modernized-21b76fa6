package com.example.steps;

import com.example.domain.account.model.GenerateStatementCmd;
import com.example.domain.account.model.StatementAggregate;
import com.example.domain.account.model.StatementGeneratedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-2");
        aggregate.markPeriodAsClosed(); // Simulating the violation state
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate("stmt-3");
        // Simulate that the previous statement closed with 100.00
        aggregate.setPreviousStatementClosingBalance(new BigDecimal("100.00"));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is usually provided in the command construction, placeholder here
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Period end is provided in the command construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        String accountNum = "ACC-123";
        Instant periodEnd = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant periodStart = periodEnd.minus(30, ChronoUnit.DAYS);
        
        // Scenario 3: intentionally mismatch opening balance for the violation test
        BigDecimal openingBal = new BigDecimal("50.00"); // Does not match 100.00 from Given
        // If we are not in the violation scenario, use a valid opening balance (or null if no prev)
        if (aggregate.getClass().getName().contains("StatementAggregate") && !hasPreviousBalanceSet()) {
             openingBal = BigDecimal.ZERO; // Safe default for generic valid case
        } else if (hasPreviousBalanceSet()) {
             // Keep the mismatching balance to trigger the error in Scenario 3
             openingBal = new BigDecimal("90.00");
        }
        
        BigDecimal closingBal = new BigDecimal("200.00");

        try {
            cmd = new GenerateStatementCmd(
                aggregate.id(),
                accountNum,
                periodStart,
                periodEnd,
                openingBal,
                closingBal
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    private boolean hasPreviousBalanceSet() {
        // We detect the specific scenario context based on the ID/State logic if needed, 
        // or simpler: we just construct the command specifically for the failure case in the @When block logic above.
        // The logic inside theWhen block handles the balance value selection based on the scenario setup.
        return aggregate.id().equals("stmt-3"); 
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verify it's an IllegalStateException (domain invariant violation)
        assertTrue(caughtException instanceof IllegalStateException);
        // Verify the message matches one of our domain rules
        assertTrue(caughtException.getMessage().contains("closed period") || 
                   caughtException.getMessage().contains("opening balance must exactly match"));
    }
}
