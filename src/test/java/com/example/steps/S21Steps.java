package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // In BDD, often we set state. If cmd exists, update it. If not, we prepare it.
        // Here we construct the command in parts. 
        // Assuming this step implies standard valid data for the field.
        // We will finalize cmd construction in the "When" or combine them.
        // However, to satisfy the flow, let's store valid data parts.
        // Simplified: We assume the command is built valid by default in the 'When' unless specified otherwise.
        // But specific data setup steps can store values.
        // For S-21, we handle the logic in the WHEN block based on context or specifically created commands.
        // To strictly follow Gherkin flow, we might need a command builder pattern, but for this task,
        // we can just create the command in the WHEN block using hardcoded valid values unless overridden.
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // See comment above.
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        executeCommand("LOGIN_SCR", "3270"); // Valid defaults
    }

    private void executeCommand(String screenId, String deviceType) {
        cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(capturedException, "Expected no error, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate("screen-map-err-01");
        // We don't create the cmd yet, just the aggregate context
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_BMS_constraints() {
        aggregate = new ScreenMapAggregate("screen-map-err-02");
    }

    @When("the RenderScreenCmd command is executed with empty screenId")
    public void the_RenderScreenCmd_command_is_executed_with_empty_screenId() {
        executeCommand("", "3270");
    }

    @When("the RenderScreenCmd command is executed with null deviceType")
    public void the_RenderScreenCmd_command_is_executed_with_null_deviceType() {
        executeCommand("MENU_SCR", null);
    }

    @When("the RenderScreenCmd command is executed with long screenId")
    public void the_RenderScreenCmd_command_is_executed_with_long_screenId() {
        // BMS Constraint is 8 chars.
        executeCommand("VERY_LONG_SCREEN_NAME", "3270");
    }

    @When("the RenderScreenCmd command is executed with long deviceType")
    public void the_RenderScreenCmd_command_is_executed_with_long_deviceType() {
        // BMS Constraint is 16 chars.
        executeCommand("MENU_SCR", "THIS_DEVICE_TYPE_IS_TOO_LONG_FOR_LEGACY_BMS");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check specific error types or messages if desired, 
        // but the requirement just says "rejected with a domain error".
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // NOTE: Gherkin feature file described generic steps like 
    // "When the RenderScreenCmd command is executed" for both success and failure.
    // To map the Java step methods to the generic Gherkin text, we need to distinguish intent.
    // Since Cucumber matches regex, we can refine the step definitions below 
    // to match the specific Scenario Context or pass parameters from Gherkin.
    // However, the S-21.feature provided in the prompt uses EXACTLY the same text for the WHEN clause.
    // To support this cleanly in Java without modifying the Gherkin text provided in the prompt:
    // We assume the violation setup in the GIVEN block puts the aggregate in a state (mocks) 
    // or we rely on the "Given... that violates" step to actually perform the command with bad data.
    
    // Refined implementation for the "Given... violates" pattern:
    @Given("a ScreenMap aggregate that violates: {string}")
    public void a_ScreenMap_aggregate_that_violates(String ruleDescription) {
        aggregate = new ScreenMapAggregate("screen-map-error");
        // We execute the bad command immediately here to satisfy the "Given" context setup 
        // or store the bad data to be used in the generic When step.
        // Let's store the invalid command intent.
        if (ruleDescription.contains("mandatory")) {
            this.cmd = new RenderScreenCmd(aggregate.id(), "", "3270"); // Empty screen ID
        } else if (ruleDescription.contains("BMS")) {
            this.cmd = new RenderScreenCmd(aggregate.id(), "TOO_LONG_ID", "3270"); // > 8 chars
        }
    }

    // We override the generic When step to handle the cmd potentially set in Given
    // But we need to support the valid scenario too.
    // A common trick in Cucumber for this pattern:
    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed_generic() {
        // If the violation context set a specific cmd, use it. Otherwise assume valid.
        if (cmd == null) {
             // Default valid command for the success scenario
             cmd = new RenderScreenCmd(aggregate.id(), "VALID_SCR", "3270");
        }
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
