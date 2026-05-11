package com.example.steps;

import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd command;
    private Exception caughtException;
    private List events;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-valid-test-1");
        // Initialize the aggregate to a clean state as if it were just created
        // or loaded fresh, ready to receive a command.
        // In a real scenario, we might load from a repo, but here we construct directly.
        // We assume a fresh statement means no previous period constraints for the 'success' path.
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // We will build the command in the 'When' step using these contextual values,
        // or store them in fields. For simplicity, we construct the command fully in the When.
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default valid data
        String account = "ACC-12345";
        LocalDate periodEnd = LocalDate.now().minusMonths(1);
        BigDecimal openingBal = BigDecimal.ZERO;
        BigDecimal closingBal = new BigDecimal("1000.00");
        
        // If the aggregate setup in Given implies specific data (e.g. for failure cases),
        // we might want to use that. However, the 'Given' clauses are generic strings.
        // We will instantiate the command with valid defaults here.
        this.command = new GenerateStatementCmd(
            aggregate.id(), 
            account, 
            periodEnd, 
            openingBal, 
            closingBal
        );

        try {
            this.events = aggregate.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(events, "Events list should not be null");
        assertEquals(1, events.size(), "Expected exactly one event");
        assertTrue(events.get(0) instanceof StatementGeneratedEvent, "Event should be StatementGeneratedEvent");
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) events.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("ACC-12345", event.accountNumber());
    }

    // --- Failure Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-error-closed-period");
        // Setup: We construct the aggregate such that it believes it already has a statement for this period.
        // We simulate this by calling execute once successfully (if business logic allows) 
        // or by constructing the aggregate in a way that sets a flag.
        // Since our command checks for existing events/state, let's execute a command first.
        // Note: The aggregate logic needs to handle this. If we execute a command, version increments.
        // Let's assume the 'aggregate' represents the SAME statement ID. Executing twice should fail.
        
        // To simulate the violation, we execute a command first so the aggregate state becomes 'GENERATED'.
        GenerateStatementCmd firstCmd = new GenerateStatementCmd(
            aggregate.id(),
            "ACC-999",
            LocalDate.now(), // The period we are trying to close
            BigDecimal.ZERO,
            BigDecimal.TEN
        );
        try {
            aggregate.execute(firstCmd);
        } catch (Exception e) {
            // Ignore first run result for setup
        }
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        // This scenario implies a check against a PREVIOUS statement.
        // However, our command is on the StatementAggregate. 
        // If the command includes an explicit openingBalance, the check "opening balance must match previous closing"
        // usually implies an internal validation within the aggregate or a domain service fetching the previous.
        // Since we don't have a Repository in this step, we can't easily fetch the previous statement.
        // BUT, the prompt says "Given a Statement aggregate that violates...".
        // This implies the STATE of the aggregate is invalid with respect to the command parameters.
        
        // Interpretation: We execute a command with a mismatched opening balance.
        // Since we can't inject the "Previous Statement" into the current aggregate without a repo,
        // we will assume the command logic handles validation or we rely on the invariants:
        // "Statement opening balance must exactly match the closing balance of the previous statement."
        // This implies the Command/Aggregate logic needs to know the previous balance.
        // For this BDD test, we will create a scenario where we just execute the command.
        // To make it fail, we might need the aggregate to track the 'last known closing balance' if it represents the account stream, 
        // but here it is 'Statement'.
        
        // Simplification: We will rely on the Command logic accepting an opening balance, 
        // and we will leave the implementation to validate it if possible, or assume this specific scenario
        // requires a domain service which we are mocking via the Command/Aggregate.
        // For the purpose of S8, let's assume the aggregate can enforce this if we pass the expected previous closing balance.
        // BUT, the simple implementation might just check if opening balance is null/invalid.
        // Let's assume the violation is that we provide an opening balance that doesn't make sense (e.g. null or negative) 
        // OR we create a command where the Opening Balance is provided but the 'Previous Closing Balance' (if we tracked it in the aggregate) doesn't match.
        
        // Let's assume the aggregate tracks the 'Last Closing Balance' (Account Statement History).
        // But StatementAggregate is per statement.
        // Let's create a fresh aggregate. It has no memory of the previous.
        // This implies we need to pass the 'Previous Closing Balance' into the command for validation.
        this.aggregate = new StatementAggregate("stmt-error-opening-bal");
        // We will handle the specific logic in the execute method to check for a match if the prevClosing is provided.
        // Here we just set up the aggregate.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for specific domain exceptions if defined (e.g. IllegalStateException, IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}
