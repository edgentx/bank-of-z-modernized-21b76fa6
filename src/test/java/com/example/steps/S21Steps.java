package com.example.steps;

import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenRenderedEvent;
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
        aggregate = new ScreenMapAggregate("LOGIN01");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Defer command creation to When step, or store parts
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Defer command creation
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Construct a valid command using the initialized aggregate's ID
        cmd = new RenderScreenCmd(aggregate.id(), "3270");
        executeCommand();
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN01", event.aggregateId());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("LOGIN01");
        // Command will be created with invalid data in the When step
    }

    @When("the RenderScreenCmd command is executed with null screenId")
    public void theRenderScreenCmdCommandIsExecutedWithNullScreenId() {
        cmd = new RenderScreenCmd(null, "3270");
        executeCommand();
    }

    @When("the RenderScreenCmd command is executed with null deviceType")
    public void theRenderScreenCmdCommandIsExecutedWithNullDeviceType() {
        cmd = new RenderScreenCmd("LOGIN01", null);
        executeCommand();
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("LONGSCREEN"); // > 7 chars
    }

    @When("the RenderScreenCmd command is executed with invalid length")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidLength() {
        cmd = new RenderScreenCmd("TOOLONGSCREENID", "3270");
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    private void executeCommand() {
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}
