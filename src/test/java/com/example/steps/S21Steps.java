package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup handled in When step construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup handled in When step construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        RenderScreenCmd cmd = new RenderScreenCmd(
            "screen-map-1",
            "LOGIN", // Valid: <= 8 chars
            "3270",
            Map.of("user", "john.doe")
        );
        executeCommand(cmd);
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertTrue(resultEvents.iterator().hasNext());
        DomainEvent event = resultEvents.iterator().next();
        Assertions.assertTrue(event instanceof ScreenRenderedEvent);
        Assertions.assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputs() {
        aggregate = new ScreenMapAggregate("screen-map-err-1");
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidData() {
        RenderScreenCmd cmd = new RenderScreenCmd(
            "screen-map-err-1",
            "   ", // Blank screenId
            null,  // Null deviceType
            null   // Null fields
        );
        executeCommand(cmd);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("screen-map-err-2");
    }

    @When("the RenderScreenCmd command is executed with BMS-violating data")
    public void theRenderScreenCmdCommandIsExecutedWithBmsViolatingData() {
        // ScreenId "TOO_LONG_SCREEN_ID" exceeds 8 chars
        RenderScreenCmd cmd = new RenderScreenCmd(
            "screen-map-err-2",
            "TOO_LONG_SCREEN_ID",
            "3270",
            Map.of()
        );
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // Helper to execute and capture exceptions
    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Method overloading to match specific When scenarios if needed, 
    // though generic 'execute' is cleaner.
    public void theRenderScreenCmdCommandIsExecuted() {
       // default implementation delegates to generic
    }
}
