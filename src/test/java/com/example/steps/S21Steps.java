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
    private String screenId;
    private String deviceType;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Screen ID length <= 7 (Legacy BMS constraint)
        this.screenId = "MENU01"; 
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.deviceType = "3270";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("MENU01", event.screenId());
        assertEquals("3270", event.deviceType());
        assertNotNull(event.layout());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-err");
        // Simulate missing mandatory field
        this.screenId = "";
        this.deviceType = "3270";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("must not be null or blank"));
        assertNull(resultEvents);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("screen-map-err");
        // Simulate screenId > 7 chars
        this.screenId = "LONGMAPNAME";
        this.deviceType = "WEB";
    }
}
