package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Scenario context setup handled in 'aValidScreenMapAggregate'
        // This step ensures preconditions for the command
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Scenario context setup handled in 'aValidScreenMapAggregate'
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("VALID_ID");
        // The violation will be triggered by passing a null or empty field in the Command later
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("VALID_ID");
        // The violation will be triggered by passing a field exceeding the legacy limit in the Command later
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Determine which command to execute based on the scenario context (Givens)
        // Ideally we use a scenario context object, but for simplicity we detect based on state or test
        // Since Cucumber steps run in sequence per scenario, we check which scenario we are in implicitly.
        
        // Scenario 1: Valid
        // Scenario 2: Missing fields
        // Scenario 3: Length violation
        
        if (aggregate != null && aggregate.id().equals("VALID_ID")) {
             try {
                // This block covers both the negative scenario setup
                // We execute a command that is intended to fail.
                // Since we can't distinguish the exact negative scenario easily without a context object,
                // we will try one known failure. However, Cucumber scenarios are isolated.
                // We will handle the logic inside the specific Scenario steps in a real runner, 
                // but here we assume the 'When' step is generic.
                // 
                // To fix this generic approach issue:
                // We will execute a 'correct' command for the positive flow.
                // If the test set up a negative precondition, the 'When' logic needs to know.
                // 
                // WORKAROUND: We'll execute a command that corresponds to the most recent Given.
                // However, standard practice is to store the intent in the step definitions.
                // Let's assume the 'When' step uses a shared variable 'currentCommand'.
                
                // Re-implementation for simplicity and robustness:
                // The prompt implies specific scenarios. I will execute a command that fits the 'Valid' scenario.
                // For negative scenarios, I will catch the exception.
                
                // But wait, if I execute a VALID command in a NEGATIVE scenario, the 'Then' checks for error will fail.
                // I need to know *which* command to execute.
                // Since the Givens just set up the aggregate, maybe the violation is in the COMMAND parameters, not the aggregate state.
                // So I will execute a command with parameters that trigger the specific error expected in the current scenario.
                
                // Since I can't easily detect which scenario is running from a generic 'When' method without context,
                // I will assume the command is always the one that MIGHT fail, or I rely on the test data.
                // Actually, the Givens say "Given a ScreenMap aggregate that violates...".
                // This implies the AGGREGATE state is the cause? No, the invariants are checked during Execute.
                // The violations are likely parameter-based.
                // 
                // Strategy: Execute a 'bad' command? No, scenarios are independent.
                // I will assume the test setup injects the specific command payload.
                // Let's default to a valid execution for the first scenario.
                
                // Better Strategy:
                // We will execute a valid command. If the scenario was negative, the test fails (which is correct behavior if the setup is missing).
                // But I need to make the negative tests pass too.
                // I will check the ID or a flag set by the Given steps.
             } catch (Exception e) {
                 caughtException = e;
             }
        }
    }

    // Refined approach for the 'When' step to handle multiple scenarios properly using Context Injection or shared state.
    // Here, I will manually inspect the flow.
    // Since this is a generated file, I will create a specific flow.

    // SCENARIO 1 FLOW
    @When("the RenderScreenCmd command is executed for valid data")
    public void theRenderScreenCmdCommandIsExecutedForValidData() {
        RenderScreenCmd cmd = new RenderScreenCmd("SCRN01", "WEB");
        resultEvents = aggregate.execute(cmd);
    }

    // SCENARIO 2 FLOW
    @When("the RenderScreenCmd command is executed with missing fields")
    public void theRenderScreenCmdCommandIsExecutedWithMissingFields() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(null, "WEB"); // Violation: null screenId
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    // SCENARIO 3 FLOW
    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidFieldLengths() {
        try {
            // Legacy BMS constraint for screenId is 8 chars. Passing 10 violates it.
            RenderScreenCmd cmd = new RenderScreenCmd("TOO_LONG_SCREEN_ID", "WEB");
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    // THEN steps
    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("screen.rendered", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Mappings for the generic When to specific implementations to satisfy the generic Gherkin
    // Note: In Cucumber, you can have multiple methods matching the same regex, or parameterized.
    // I will map the generic "When the RenderScreenCmd command is executed" to the specific logic
    // based on the state of the aggregate or exception.
    // However, the cleanest way provided the constraints is to ensure the 'When' step covers the success case,
    // and assume the prompt's generic "When" is just the label.
    
    // Re-mapping the generic When to the success case (Scenario 1)
    @When("^the RenderScreenCmd command is executed$")
    public void executeRenderCommandGeneric() {
        // This handles Scenario 1
        if (aggregate != null && aggregate.id().equals("SCRN01")) {
             theRenderScreenCmdCommandIsExecutedForValidData();
        }
        // Attempt to detect negative scenarios via heuristic or fail? 
        // The generated Gherkin uses the exact same "When" line for all 3 scenarios.
        // Standard Cucumber practice: The step definition code needs to be smart, OR the Gherkin should be different.
        // Given I cannot change the Gherkin (Prompt says "Use the acceptance criteria AS-IS"),
        // I must handle the logic inside the step definition.
        
        // Since 'Given' steps set the stage, I'll use a ThreadLocal or instance flag to determine mode.
        // But 'Given' methods are separate.
        // 
        // Actually, if I look at the 'Given' for violations: "Given a ScreenMap aggregate that violates..."
        // I will set a flag there.
    }
    
    // I'll update the 'Given' methods to set a "mode"
    private enum TestMode { VALID, MISSING_FIELDS, BMS_CONSTRAINT }
    private TestMode mode = TestMode.VALID;

    @Override
    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCRN01");
        mode = TestMode.VALID;
    }

    @Override
    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // No-op, keeping mode valid
    }

    @Override
    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // No-op
    }

    @Override
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("DUMMY_ID");
        mode = TestMode.MISSING_FIELDS;
    }

    @Override
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenMapAggregate("DUMMY_ID");
        mode = TestMode.BMS_CONSTRAINT;
    }

    @Override
    @When("^the RenderScreenCmd command is executed$")
    public void theRenderScreenCmdCommandIsExecuted() {
        caughtException = null;
        resultEvents = null;
        
        try {
            if (mode == TestMode.VALID) {
                RenderScreenCmd cmd = new RenderScreenCmd("SCRN01", "WEB");
                resultEvents = aggregate.execute(cmd);
            } else if (mode == TestMode.MISSING_FIELDS) {
                RenderScreenCmd cmd = new RenderScreenCmd(null, "WEB");
                resultEvents = aggregate.execute(cmd);
            } else if (mode == TestMode.BMS_CONSTRAINT) {
                RenderScreenCmd cmd = new RenderScreenCmd("TOO_LONG_SCREEN_ID", "WEB");
                resultEvents = aggregate.execute(cmd);
            }
        } catch (UnknownCommandException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Override
    @Then("^a screen.rendered event is emitted$")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events but got null (command likely failed)");
        assertFalse(resultEvents.isEmpty());
        assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Override
    @Then("^the command is rejected with a domain error$")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception but command succeeded");
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof UnknownCommandException);
    }
}