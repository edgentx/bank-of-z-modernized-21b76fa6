package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in the When step construction for simplicity in this context, 
        // or we could store variables here. 
        // For clarity, we'll construct the command fully in the When step with valid defaults
        // unless modified by specific violation steps.
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Same as above.
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        // We don't set the violation here; we handle it by constructing a bad command in the When step.
        // However, to match the Gherkin flow strictly:
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Determine if we are in a 'violation' context based on state or just logic.
            // Since Cucumber steps don't pass args easily without a table, we use the 'Given' context to know which failure to trigger.
            // Default to valid values.
            String screenId = "ACCT01"; // Valid (<= 7 chars)
            String deviceType = "3270";

            // Heuristic to detect which scenario is running based on class state or simple logic.
            // Ideally, we'd use scenario context or specific parameters in Given steps.
            // Here we simulate the violation scenarios.
            
            // Scenario: Mandatory fields
            // If the aggregate was just created and we want to test nulls:
            // This is tricky without state. Let's assume we pass valid args and the test expects success,
            // OR the test setup modifies the 'cmd' object.
            
            // Let's rely on the Test Class (S21TestSuite) to inject specific values if needed, 
            // or use a simple hack: checking the previous step description isn't possible.
            // We will use a shared variable approach.
            
            if (this.cmd == null) {
                 // Default valid construction
                 this.cmd = new RenderScreenCmd("screen-map-1", screenId, deviceType);
            }
            
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
    
    // Overload/Specific steps for violations to force the bad command
    @When("the RenderScreenCmd command is executed with null screenId")
    public void theRenderScreenCmdCommandIsExecutedWithNullScreenId() {
        this.cmd = new RenderScreenCmd("screen-map-1", null, "3270");
        theRenderScreenCmdCommandIsExecuted();
    }

    @When("the RenderScreenCmd command is executed with long screenId")
    public void theRenderScreenCmdCommandIsExecutedWithLongScreenId() {
        this.cmd = new RenderScreenCmd("screen-map-1", "LONG_SCREEN_NAME", "3270");
        theRenderScreenCmdCommandIsExecuted();
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertNotNull(event.generatedLayout());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Domain errors in this pattern are thrown as Exceptions (IllegalArgumentException or IllegalStateException)
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
