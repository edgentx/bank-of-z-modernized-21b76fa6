package com.example.steps;

import com.example.domain.uimodel.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // State handled in command construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // State handled in command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Assuming valid defaults for positive scenario
            RenderScreenCmd cmd = new RenderScreenCmd("screen-1", DeviceType.DESKTOP);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("screen.rendered", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    // Negative Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingFields() {
        aggregate = new ScreenMapAggregate("screen-err-1");
        // This sets up the aggregate, but the violation comes from the Command
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithInvalidLengths() {
        aggregate = new ScreenMapAggregate("screen-err-2");
    }

    @When("the command is executed with invalid parameters")
    public void theCommandIsExecutedWithInvalidParameters() {
        try {
            // This logic is specific to how we trigger the violation in context
            // We will construct commands that violate the rules inside the specific step context if needed,
            // but here we can just simulate the failure.
            RenderScreenCmd cmd = new RenderScreenCmd(null, null); // Violates mandatory
            aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    // Overriding the When for specific negative flows if necessary, or using the generic one
    @When("the RenderScreenCmd command is executed with invalid data")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidData() {
        try {
             // Simulating BMS length violation via command data if ScreenMap Aggregate checks it
             // For this test, we assume the command carries the bad data or the Aggregate check fails
             RenderScreenCmd cmd = new RenderScreenCmd("invalid-screen-id-too-long-for-bms", DeviceType.TN3270);
             aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
