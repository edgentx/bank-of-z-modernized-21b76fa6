package com.example.steps;

import com.example.domain.screenmap.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to create a valid base aggregate for positive scenarios
    private ScreenMapAggregate createValidAggregate() {
        ScreenMapAggregate agg = new ScreenMapAggregate("screen-1");
        // Assuming some hydration might be needed, or we just rely on command data
        // For this command, the aggregate logic is primarily processing.
        return agg;
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = createValidAggregate();
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup, mostly handled in the When block via command creation
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command execution
        RenderScreenCmd cmd = new RenderScreenCmd("screen-1", DeviceType.TN3270);
        executeCommand(cmd);
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("screen-1", event.aggregateId());
        Assertions.assertNotNull(event.occurredAt());
        Assertions.assertNotNull(event.layoutData());
    }

    // Negative Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = createValidAggregate();
        // Context prepared for execution with bad command in When block
    }

    @When("the RenderScreenCmd command is executed with invalid screenId")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidScreenId() {
        // Blank screenId
        RenderScreenCmd cmd = new RenderScreenCmd("", DeviceType.TN3270);
        executeCommand(cmd);
    }

    @When("the RenderScreenCmd command is executed with null deviceType")
    public void theRenderScreenCmdCommandIsExecutedWithNullDeviceType() {
        // Null deviceType
        RenderScreenCmd cmd = new RenderScreenCmd("screen-1", null);
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = createValidAggregate();
    }

    @When("the RenderScreenCmd command is executed with invalid field lengths")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidFieldLengths() {
        // Create command with inputs exceeding legacy constraints
        // Assuming max screen length 32 and field value 80 for BMS testing
        String tooLongScreenId = "x".repeat(33); 
        RenderScreenCmd cmd = new RenderScreenCmd(tooLongScreenId, DeviceType.TN3270);
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for specific exception types based on domain rules (IllegalArgumentException or IllegalStateException)
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected domain error (IllegalArgumentException or IllegalStateException), got: " + capturedException.getClass().getSimpleName()
        );
    }

    private void executeCommand(Command cmd) {
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
        } catch (Exception e) {
            this.capturedException = e;
        }
    }
}
