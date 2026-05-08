package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Command constructed in the When step or stored if needed
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Command constructed in the When step or stored if needed
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid command construction
        if (cmd == null) {
            cmd = new RenderScreenCmd("screen-1", "DESKTOP", 1920, 1080);
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        this.aggregate = new ScreenMapAggregate("screen-invalid");
        // Violation: screenId is null or blank
        this.cmd = new RenderScreenCmd(null, "DESKTOP", 100, 100);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("screen-long");
        // Violation: Legacy BMS fields width > 80 chars (assuming constraint is <= 80 for fields)
        this.cmd = new RenderScreenCmd("screen-long", "3270", 100, 100); 
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || 
                   capturedException instanceof IllegalStateException);
    }
}
