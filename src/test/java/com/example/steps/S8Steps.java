package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    
    // Test data constants
    private static final String TEST_STATEMENT_ID = "stmt-123";
    private static final String TEST_ACCOUNT = "acc-456";
    private static final LocalDate START = LocalDate.of(2023, 1, 1);
    private static final LocalDate END = LocalDate.of(2023, 1, 31);
    private static final BigDecimal OPENING_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal CLOSING_BALANCE = new BigDecimal("1500.00");
    private static final BigDecimal PREVIOUS_CLOSING = new BigDecimal("1000.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        aggregate.setClosedPeriod(true); // Ensure period is closed for successful path
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_violates_closed_period() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        aggregate.setClosedPeriod(false); // Period is open, which violates the requirement for generation
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_violates_opening_balance_match() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        aggregate.setClosedPeriod(true);
        // The violation will be handled in the cmd creation data passed during execution
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is part of the command constructed in the When step
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Dates are part of the command constructed in the When step
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            // Check if we are in the mismatch scenario by looking at the context (hacky but works for steps)
            // Better approach: We determine the command payload based on the Gherkin context.
            // Since we don't have context injection here, we rely on standard defaults and check the specific failure case.
            
            BigDecimal previousClosing = PREVIOUS_CLOSING;
            BigDecimal opening = OPENING_BALANCE;

            // If we are in the "violates opening balance" scenario, we manually mismatch the data here.
            // In a real framework, we might use a scenario context.
            // However, Cucumber runs scenarios in isolation. We can detect the specific scenario state if needed,
            // but usually we just construct the specific failing case for that scenario's Given/When.
            // Given that these are distinct scenarios, we can construct the command to fail specifically for the 3rd scenario.
            // But we are in one class. Let's look at the aggregate state setup.
            
            // A simple heuristic for this specific POC:
            // The "violates match" Given doesn't change the arithmetic data, so we check if the aggregate is closed. 
            // If it is closed, we assume we are testing success OR the balance mismatch.
            // Since we can't easily distinguish "success" from "balance mismatch" without a context object, 
            // we will assume the default is SUCCESS. The 3rd scenario test will rely on specific data passed in the cmd?
            // Actually, the 3rd scenario step is specific: "Given ... violates: opening balance...".
            // Let's assume that scenario is the one where we want to force a mismatch.
            // Since Cucumber re-instantiates the Steps class, we can use a flag or simply logic.
            // But we don't have a flag. Let's assume standard success data. The failure test will need to ensure the cmd is bad.
            // Wait, the GenerateStatementCmd takes the values.
            
            // Scenario 2 check: if closedPeriod is false (from Given), it throws error.
            // Scenario 3 check: We need to send mismatched data.
            // Let's assume the aggregate is in a state where we can trigger the error.
            // To support all three, let's assume the 'success' path is default, and we adjust for the failure scenarios
            // based on the aggregate state set in the Given.
            
            boolean isClosedPeriodScenario = !aggregate.getClass().getDeclaredFields().toString().isEmpty(); // Can't do this.
            // We will rely on the default "Success" data. The Scenario 3 will fail if we don't inject the bad data.
            // However, the prompt asks to write ONE step definition method. 
            // So how do we trigger the failure in Scenario 3?
            // We can inspect the aggregate? No.
            // We rely on the fact that in Scenario 3, the 'Given' sets up the aggregate.
            // We can try to detect if we are in the "mismatch" scenario.
            // Actually, the best way in Cucumber without context objects is to just assume valid input, 
            // and the Scenario 3 test will FAIL unless we customize the input.
            // But the prompt says: "Implement ... scenarios". They must pass.
            // So the When step must be smart.
            
            // Let's use a simple discriminator based on the aggregate's state or a shared field in Steps class (which is reset).
            // Actually, the simplest way: The Given for Scenario 3 is unique. 
            // But we can't pass data from Given to When easily without instance fields.
            // We have `private Exception capturedException;`.
            // Let's create a field `private boolean expectBalanceMismatch = false;` and set it in the Given.
            
            // Refining the Given logic: 
            // We will check `this.expectBalanceMismatch` flag.
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Helper to execute the command with specific parameters
    private void executeCommand(BigDecimal opening, BigDecimal previous) {
        try {
            Command cmd = new GenerateStatementCmd(
                TEST_STATEMENT_ID,
                TEST_ACCOUNT,
                START,
                END,
                opening,
                CLOSING_BALANCE,
                previous
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Re-defining the When logic to be smarter or using the flag approach.
    // Since we can't edit the generated code's flow easily in this thought block, let's stick to the generated method.
    // I will update the content of 'the_generate_statement_cmd_command_is_executed' below.
    
    /* Revised When Logic */
    @When("the GenerateStatementCmd command is executed")
    public void executeGenerateStatementCmd() {
        // Default values
        BigDecimal opening = OPENING_BALANCE;
        BigDecimal previous = PREVIOUS_CLOSING;

        // Determine if we need to force a mismatch for Scenario 3.
        // We can't easily distinguish scenarios unless we track state in the Given.
        // However, we know Scenario 2 sets closedPeriod = false. Scenario 1 sets closedPeriod = true.
        // Scenario 3 sets closedPeriod = true (as per the logic needed to reach the balance check).
        // So if closedPeriod is true, it could be Scen 1 or Scen 3.
        // To make Scen 3 fail as requested, we explicitly corrupt the data IF we are in a state that implies Scen 3.
        // But the aggregate doesn't know it's Scenario 3.
        // Workaround: The Scenario 3 Given sets up the aggregate to *expect* the failure? No, it says "violates".
        // Let's assume the test setup injects the violation via a specific flag or we assume the default is valid.
        // IF the default is valid, Scenario 3 PASSES (falsely).
        // SO, we must make Scenario 3 fail.
        // How? We check if `aggregate` is in a state that only Scenario 3 sets.
        // Both Scen 1 and 3 set closedPeriod = true.
        // Maybe Scenario 3 sets a specific marker on the aggregate in the Given? The code below for Given doesn't.
        // Let's look at the prompt: "Given a Statement aggregate that violates...".
        // I will add a marker in the Given method below: `aggregate.setScenario3(true);`
        // And check it here.
        
        if (this.isScenario3) {
            opening = new BigDecimal("999.99"); // Mismatch
        }

        try {
            Command cmd = new GenerateStatementCmd(TEST_STATEMENT_ID, TEST_ACCOUNT, START, END, opening, CLOSING_BALANCE, previous);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
    
    // Supporting field for the hack
    private boolean isScenario3 = false;

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void setupMismatchScenario() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        aggregate.setClosedPeriod(true);
        this.isScenario3 = true;
    }

    // Clean up other Givens to reset flags
    @Given("a valid Statement aggregate")
    public void setupValidScenario() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        aggregate.setClosedPeriod(true);
        this.isScenario3 = false;
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void setupOpenPeriodScenario() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        aggregate.setClosedPeriod(false);
        this.isScenario3 = false;
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals(TEST_ACCOUNT, event.accountNumber());
        assertEquals(END, event.periodEnd());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // The exception can be IllegalStateException or IllegalArgumentException depending on the invariant violated
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
