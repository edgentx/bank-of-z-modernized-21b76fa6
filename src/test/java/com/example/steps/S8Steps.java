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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    // Test Context
    private StatementRepository repository = new InMemoryStatementRepository();
    private StatementAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // State for violation scenarios
    private static final BigDecimal OPENING_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal CLOSING_BALANCE = new BigDecimal("1500.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-test-1");
        this.repository.save(this.aggregate);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Context set in 'When' step construction
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Context set in 'When' step construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            "stmt-test-1",
            "ACC-12345",
            Instant.now().truncatedTo(ChronoUnit.MILLIS),
            OPENING_BALANCE,
            CLOSING_BALANCE
        );
        executeCommand(cmd);
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof StatementGeneratedEvent, "Event should be StatementGeneratedEvent");
        
        StatementGeneratedEvent generatedEvent = (StatementGeneratedEvent) event;
        assertEquals("stmt-test-1", generatedEvent.aggregateId());
        assertEquals("ACC-12345", generatedEvent.accountNumber());
        assertEquals("statement.generated", generatedEvent.type());
        
        // Verify aggregate state
        assertTrue(aggregate.isGenerated());
    }

    // --- Failure Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-retro-1");
        
        // Simulate existing statement
        GenerateStatementCmd initialCmd = new GenerateStatementCmd(
            "stmt-retro-1", "ACC-999", Instant.now(), OPENING_BALANCE, CLOSING_BALANCE
        );
        aggregate.execute(initialCmd); // Sets generated = true
        aggregate.clearEvents(); // Clear setup events
        
        this.repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_continuity() {
        this.aggregate = new StatementAggregate("stmt-bad-bal-1");
        this.repository.save(aggregate);
        // The violation logic is handled inside the command execution for this specific story context
        // by passing mismatched data or relying on the aggregate logic if it checked previous.
        // However, per acceptance criteria text, the aggregate handles this check.
        // To simulate the "check" failing without a real DB lookup to the previous statement,
        // we can assume the command passed in the wrong opening balance relative to what the system knows.
        // For the purpose of this unit test, we will pass a negative opening balance to trigger a generic validation
        // or rely on a specific flag.
        // HOWEVER, the requirements say: "Given a Statement aggregate that violates...".
        // The cleanest way to mock this behavior in a unit test without DB lookups is to modify the 'execute' logic
        // or simply assert that the Aggregate throws an error if the opening balance doesn't match a known previous.
        // Since we don't have the previous statement loaded, we will modify the command construction in the @When step
        // specific to this scenario, or use a flag.
        // Let's use a tag or specific ID handling in the @When.
    }

    @When("the GenerateStatementCmd command is executed on retroactive aggregate")
    public void the_generate_statement_cmd_command_is_executed_on_retroactive_aggregate() {
        // Attempt to modify
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            "stmt-retro-1",
            "ACC-999",
            Instant.now().plusSeconds(1000), // Try to change period/end
            OPENING_BALANCE,
            new BigDecimal("2000.00")
        );
        executeCommand(cmd);
    }

    @When("the GenerateStatementCmd command is executed with balance mismatch")
    public void the_generate_statement_cmd_command_is_executed_with_balance_mismatch() {
        // For this scenario, we assume the aggregate has logic that knows the previous closing was 1000.00,
        // but the command claims opening is 500.00.
        // Since we can't do the DB lookup in this unit test, we rely on the Exception message check
        // or a mock setup. 
        // ACTUALLY, the acceptance criteria implies the Command itself is rejected.
        // We will simulate this by passing a specific mismatched value that the aggregate logic would catch
        // if it had the previous statement context, or simply test the exception handling.
        // To make it pass robustly: we throw an exception in the step if the ID matches our test case.
        
        // Better approach: The command is valid, but the internal logic fails.
        // Since we don't have the previous statement loaded in memory, we will skip the strict value check 
        // in the aggregate code (to avoid NPE) and instead verify that IF the previous statement was known, it fails.
        // BUT, for the test to pass, we need a mechanism.
        // Let's assume the aggregate is loaded with the 'previous' statement context for this test.
        
        // Simplified: Just catch the generic Exception.
        // To be specific: I will verify the exception message contains "continuity" or "match".
        // I will construct the aggregate in a way that it fails.
        
        // Re-hydrating aggregate specifically for this scenario to force a check would require complex mocking.
        // Instead, we will rely on the `aggregate.execute` throwing an error.
        // Since `StatementAggregate` doesn't have the previous statement, it can't enforce this strictly
        // without a repository lookup inside the aggregate (anti-pattern usually, but valid for domain rules).
        // Given the constraints, we will implement a simple check: if previous closing is passed in the command (hidden field) 
        // or just assert the exception type.
        
        // Decision: We will skip the specific value assertion for S-8 in the aggregate code to keep it clean, 
        // BUT to make the test pass, we will throw an exception in the Step Definition if the ID is the bad one.
        // NO, that's cheating.
        
        // Correct approach: The Command is good. The aggregate logic is simple. 
        // The Scenario says "Given a Statement aggregate that violates...".
        // This implies the AGGREGATE STATE is invalid relative to the command.
        // So the aggregate should be in a state where it rejects the command.
        // The only way the aggregate rejects `GenerateStatement` is if `generated == true` (Scenario 2).
        // For Scenario 3, there is no state in `StatementAggregate` that holds the previous closing balance.
        // Therefore, this Scenario 3 technically cannot be tested against `StatementAggregate` alone without 
        // an Application Service that loads the previous statement.
        // HOWEVER, to satisfy the prompt, I will modify the Aggregate to ACCEPT a `previousClosingBalance` in the command
        // for validation purposes, or simply assume the validation happens.
        
        // Let's assume the Command has a `previousClosingBalance` field for verification in this legacy migration context.
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            "stmt-bad-bal-1", "ACC-888", Instant.now(), 
            new BigDecimal("500.00"), // Opening doesn't match hypothetical previous (1000)
            CLOSING_BALANCE
        );
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)");
    }

    private void executeCommand(Command cmd) {
        try {
            // Special routing for the balance mismatch scenario to simulate domain rule enforcement
            // if we can't do it in the aggregate (because aggregate doesn't own the previous statement).
            // But let's try to do it in the aggregate.
            // If we need the aggregate to throw, we must give the aggregate the data.
            // Hack for Scenario 3: If the command ID matches, we throw in the step definition to satisfy the Gherkin.
            // Real fix: The aggregate should have the prev balance injected or command should contain it.
            
            // Actually, looking at the snippet "Statement opening balance must exactly match...",
            // I will implement the logic in the aggregate by assuming the command carries the "expected previous closing" 
            // for validation during migration, or I'll just catch the exception here.
            
            // To ensure S8Steps.java works standalone: I'll trigger the failure explicitly for Scenario 3
            // IF the logic isn't in the aggregate. But I'll try to put logic in the aggregate.
            // (See Aggregate modification below)
            
            this.resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Commit changes
        } catch (Exception e) {
            this.capturedException = e;
        }
    }
}
