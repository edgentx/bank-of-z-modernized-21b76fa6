package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private String deviceType;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-001");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.screenId = "LOGIN_SCRN_01";
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.deviceType = "3270";
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("map-invalid-01");
        // Setup state that implies missing data (simulated by setting fields to null/bad in the step below or command)
        this.screenId = null; // Violating constraint
        this.deviceType = "WEB";
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsLengths() {
        aggregate = new ScreenMapAggregate("map-invalid-len-01");
        this.screenId = "THIS_IS_A_VERY_LONG_SCREEN_NAME_THAT_EXCEEDS_THIRTY_CHARS_LIMIT";
        this.deviceType = "3270";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Construct the command from context variables
            RenderScreenCmd cmd = new RenderScreenCmd(
                aggregate.id(),
                this.screenId,
                this.deviceType,
                "{\"fields\": []}" // dummy valid JSON for content
            );
            aggregate.execute(cmd);
        } catch (IllegalArgumentException | UnknownCommandException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(aggregate.uncommittedEvents(), "Events list should not be null");
        assertFalse(aggregate.uncommittedEvents().isEmpty(), "Events list should not be empty");
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
        assertNull(capturedException, "No exception should have been thrown");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error exception");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
        assertTrue(aggregate.uncommittedEvents().isEmpty(), "No events should be emitted on failure");
    }
}
