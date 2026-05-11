package com.example.steps;

import com.example.domain.screenmap.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Screen ID is implicitly handled in the command construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Device type is implicitly handled in the command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            if (cmd == null) {
                cmd = new RenderScreenCmd("screen-1", "3270");
            }
            aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        Assertions.assertEquals("screen.rendered", aggregate.uncommittedEvents().get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
        cmd = new RenderScreenCmd(null, "3270");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesLegacyFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-bms");
        // Assuming 3270 devices have strict length constraints e.g. max 32 chars for screen ID in BMS maps
        cmd = new RenderScreenCmd("this-screen-id-is-far-too-long-for-legacy-bms-maps", "3270");
    }
}
