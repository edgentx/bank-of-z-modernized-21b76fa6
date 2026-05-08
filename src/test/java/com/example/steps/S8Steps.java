package com.example.steps;

import com.example.domain.account.model.StatementAggregate;
import com.example.domain.account.model.GenerateStatementCmd;
import com.example.domain.account.model.StatementGeneratedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Throwable caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // "Valid" in this context means it's a fresh aggregate ready to process a new generation.
        // In a real repo load scenario, we might hydrate it, but here we instantiate.
        this.aggregate = new StatementAggregate("stmt-1");
        // Pre-hydrate state if needed (e.g., existing version), but for generation, v0 or v1 is fine.
        // Let's assume it handles the transition to Generated.
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // We construct the command progressively or use a default in the 'When' clause.
        // Storing context for command construction.
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Storing context for command construction.
    }

    // 
    // Negative Scenarios - Setup State
    //

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period_retroactive_alteration() {
        this.aggregate = new StatementAggregate("stmt-2");
        // Setup: The aggregate is already in a 'GENERATED' state.
        // We simulate this by applying a previous event or directly setting state in a test setup.
        // Since we cannot easily apply events without a full hydrator, we will rely on 
        // the Command check or specific constructor behavior if available.
        // However, to strictly test the invariant "cannot be altered retroactively", 
        // the aggregate likely needs to be in a final state.
        // Let's assume the aggregate version > 0 implies closed, or we can mock the version.
        // A simple way for this test is to assume the aggregate was already generated.
        // If the code checks `version > 0`, we need to increment it. 
        // Since `incrementVersion` is protected, we might need a test-specific setup or 
        // we rely on the business logic: "Statement already generated".
        // Let's use a specific ID that implies a closed period or rely on the execute method throwing if called twice.
        // For this BDD, we assume the logic handles the check.
        
        // If we need to simulate an existing statement, we can create a cmd that tries to regenerate.
        // The logic will likely be `if (generated) throw`.
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance_mismatch() {
        this.aggregate = new StatementAggregate("stmt-3");
        // The Command will carry the openingBalance. 
        // The aggregate needs to know the expected closing balance of the previous statement.
        // In a real system, the Aggregate would be loaded with history.
        // Here, we will construct the Command with a mismatched balance 
        // and assume the Aggregate (or a repository call it makes) knows the previous balance was something else.
        // To make this test self-contained without a DB, we might need to inject a mock repository 
        // or pass the "previous closing balance" to the aggregate if it's a parameter of the logic.
        // Given the signature `execute(Command)`, the context must be inside the Aggregate.
        // We will assume the aggregate has a field `previousClosingBalance` (mocked).
    }

    //
    // Actions
    //

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            // Constructing a generally valid command for the positive path
            String acct = "acct-12345";
            LocalDate end = LocalDate.now();
            BigDecimal openBal = BigDecimal.ZERO; // valid for new or matching
            
            this.cmd = new GenerateStatementCmd("stmt-1", acct, end, openBal);
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Throwable t) {
            this.caughtException = t;
        }
    }

    // Specific When for the mismatch scenario to inject the bad data
    @When("the GenerateStatementCmd command is executed with mismatched balance")
    public void the_generate_statement_cmd_command_is_executed_with_bad_balance() {
         try {
            // Scenario 3: Mismatched balance
            // Assume previous closing was 100.00, but command says 50.00
            BigDecimal badBalance = new BigDecimal("50.00");
            
            // We need to tell the aggregate what the previous balance was. 
            // Since we don't have a full repo, we'll assume the aggregate can be constructed or 
            // updated with this context for the sake of the unit test pattern, 
            // OR the command carries the previous balance (unlikely), 
            // OR the aggregate checks a service.
            // For this implementation, let's assume the Aggregate constructor accepts a context.
            // Re-instantiating with specific context for Scenario 3
            this.aggregate = new StatementAggregate("stmt-3", new BigDecimal("100.00"));
            
            this.cmd = new GenerateStatementCmd("stmt-3", "acct-123", LocalDate.now(), badBalance);
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Throwable t) {
            this.caughtException = t;
        }
    }

    // Re-use the standard When for the "Closed Period" scenario (Scenario 2)
    @When("the GenerateStatementCmd command is executed on closed aggregate")
    public void the_generate_statement_cmd_command_is_executed_on_closed() {
        try {
            // Scenario 2: Closed period
            // We assume the aggregate is already in a generated state.
            // We manually increment version to simulate history if not using a full hydrator.
            // This is a bit of a hack for the BDD structure without a full repo.
            // However, checking `uncommittedEvents` size > 0 after a fake generation works too.
            // But simplest is to assume logic handles it.
            
            this.cmd = new GenerateStatementCmd("stmt-2", "acct-123", LocalDate.now(), BigDecimal.ZERO);
            // If the aggregate doesn't track state, we rely on the Command to carry the "old" flag? No.
            // Let's assume the aggregate was initialized as closed.
            this.aggregate = new StatementAggregate("stmt-2");
            // Manually setting a flag or simulating version bump to indicate existing
            // (In a real test, we'd call execute twice, but the first call might fail on other invariants)
            // For this output, we assume the logic `if (this.generated) throw` is present.
            // We can't force internal state easily without a package-private setter or reflection.
            // We will assume the Scenario 2 setup failed to set it, and we rely on the command check.
            // *Correction*: The generated code for the Aggregate will handle the check.
            // If the aggregate is fresh, it won't reject.
            // TO MAKE SCENARIO 2 WORK without reflection: The Command could have a flag? No.
            // We will assume the test setup creates the aggregate in a way it knows it's closed, 
            // OR we verify the error handling if the Command contains metadata indicating it's an update.
            // Since the prompt implies standard Execute(), Scenario 2 likely requires the aggregate to be loaded.
            // I will map the "closed" step to a specific instantiation or state change if possible.
            // If not, the test might pass erroneously if the check isn't implemented.
            // To ensure it works, I will add a constructor to StatementAggregate that accepts a boolean `isClosed`.
            this.aggregate = new StatementAggregate("stmt-2", true);
            
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Throwable t) {
            this.caughtException = t;
        }
    }


    //
    // Outcomes
    //

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultingEvents, "Events should not be null");
        assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultingEvents.get(0) instanceof StatementGeneratedEvent, "First event should be StatementGeneratedEvent");
        assertNull(caughtException, "Should not have thrown an exception: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain errors are typically IllegalArgumentException or IllegalStateException in this pattern
        assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected domain error (IllegalArgument/IllegalState), got: " + caughtException.getClass().getSimpleName()
        );
    }

}
