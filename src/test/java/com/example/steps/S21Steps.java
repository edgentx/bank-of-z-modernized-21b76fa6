package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenAggregate;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenAggregate aggregate;
    private Command cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenAggregate("screen-01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Setup data handled in command construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Setup data handled in command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Constructing a valid command by default if not overridden
            if (cmd == null) {
                cmd = new RenderScreenCmd("screen-01", DeviceType.DESKTOP);
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit one event");
        Assertions.assertEquals("screen.rendered", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenAggregate("screen-invalid");
        // ScreenId null
        cmd = new RenderScreenCmd(null, DeviceType.DESKTOP);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = new ScreenAggregate("screen-long");
        // ScreenId exceeds BMS limit (assumed 8 chars for legacy 3270 map name)
        cmd = new RenderScreenCmd("very-long-screen-name", DeviceType.TN3270);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Exception should have been thrown");
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException);
    }

    // Inner enum for test usage if not exposed in domain, though likely should be in domain
    public enum DeviceType {
        DESKTOP, MOBILE, TN3270
    }
}
