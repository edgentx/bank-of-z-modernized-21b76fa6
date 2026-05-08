package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMap aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMap("screen-123");
        aggregate.initialize(); // Pre-set valid state
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled implicitly in the When step construction of the command
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Handled implicitly in the When step construction of the command
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Default valid command for the happy path
        RenderScreenCmd cmd = new RenderScreenCmd("screen-123", "3270", DeviceType.TERMINAL_3270);
        executeCommand(cmd);
    }

    // --- Variations for Negative Scenarios ---

    // We use a helper to allow injecting bad commands based on the context if needed,
    // or simple logic to detect which violation to simulate based on the Given text.
    // Since Cucumber steps are regex matched, we can overload the Given or the When.
    // Here we use specific When steps for clarity.

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMap("screen-missing-fields");
        // The aggregate state itself doesn't matter as much as the command validation,
        // but we ensure it exists.
        aggregate.initialize();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = new ScreenMap("screen-long-field");
        aggregate.initialize();
    }

    // We need a way to trigger the specific violation. 
    // I will interpret the 'When' step for negative scenarios as executing a SPECIFIC bad command.
    // However, the Gherkin uses the exact same 'When' line: "When the RenderScreenCmd command is executed"
    // This implies the Command creation logic inside the step must adapt, OR we rely on the aggregate state
    // to throw the error when a valid command is applied. 
    // Given the text "Given a ScreenMap aggregate that violates...", it suggests the Aggregate is bad?
    // No, validation usually happens on the Command. 
    // I will create a specialized 'When' step that matches the context of the previous 'Given'. 
    // Wait, Cucumber doesn't link Given to When automatically. 
    // I will implement the 'When' to try-catch, and the 'Given' sets up the AGGREGATE. 
    // But to trigger the error, the 'When' needs to pass a BAD command. 
    // The prompt's Gherkin shows the exact same 'When' line for all scenarios. 
    // This implies the 'Given' prepares the context such that the 'When' logic (which constructs the command) 
    // might differ, or the 'Given' sets the aggregate to a state that rejects a *valid-looking* command. 
    // BUT, the validation criteria "All mandatory input fields" is a Command check. 
    // I will assume the 'When' for the negative cases injects a failure via a setup flag or by 
    // actually sending a bad command. Since I can't change the 'When' line text, I will use 
    // a helper in the steps to store the 'currentCommandToBuild' and modify it in the Given.

    private RenderScreenCmd.Builder cmdBuilder = new RenderScreenCmd.Builder();

    @Given("a ScreenMap aggregate that violates: {string}")
    public void a_ScreenMap_aggregate_that_violates(String rule) {
        aggregate = new ScreenMap("screen-error");
        aggregate.initialize();
        
        if (rule.contains("mandatory input fields")) {
            cmdBuilder.withScreenId(null); // Violate mandatory
        } else if (rule.contains("Field lengths")) {
            cmdBuilder.withDeviceType("VERY_LONG_DEVICE_NAME_EXCEEDING_BMS");
        }
    }

    // ... Actually, keeping it simple. I will create specific When steps matching the intent,
    // but since the prompt Gherkin is fixed, I must stick to the provided text.
    // I will use the generic 'When' and assume that for the error cases, I need to detect the context.
    // Since I can't easily detect context in Cucumber without state, and I can't change the Gherkin:
    // I will implement the 'When' to execute a command that *might* fail based on internal step state
    // set in the 'Given'.

    // Refined approach for S21Steps:
    
    @Given("a valid ScreenMap aggregate")
    public void setup_valid_aggregate() {
        aggregate = new ScreenMap("valid-id");
        aggregate.initialize();
        this.cmdTemplate = new RenderScreenCmd("valid-id", "3270", DeviceType.TERMINAL_3270);
    }

    @Given("a ScreenMap aggregate that violates: {string}")
    public void setup_invalid_aggregate_context(String violationType) {
        aggregate = new ScreenMap("invalid-id");
        aggregate.initialize();

        if (violationType.contains("mandatory input fields")) {
            // Create a command that violates the rule
            this.cmdTemplate = new RenderScreenCmd(null, "3270", DeviceType.TERMINAL_3270);
        } else if (violationType.contains("Field lengths")) {
            this.cmdTemplate = new RenderScreenCmd("id", "TYPE_THAT_IS_TOO_LONG_FOR_BMS", DeviceType.WEB);
        }
    }

    private RenderScreenCmd cmdTemplate;

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed_generic() {
        executeCommand(cmdTemplate);
    }

    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
