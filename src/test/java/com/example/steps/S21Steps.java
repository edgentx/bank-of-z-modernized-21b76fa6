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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("sm-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // We use a default valid ID. Specific test cases override the command setup.
        if (this.cmd == null) {
            this.cmd = new RenderScreenCmd("LOGIN01", "DESKTOP");
        } else {
            // Merge/overwrite logic if needed, but simple replacement is usually fine for BDD
            // Here we just ensure we have a command object.
        }
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in the previous step for simplicity in this flow, or override here
        this.cmd = new RenderScreenCmd("LOGIN01", "DESKTOP");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
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
        assertEquals("screen.rendered", resultEvents.get(0).type());
        // Ensure no exception was thrown
        assertNull(thrownException);
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("sm-2");
        // Setup command that violates null/blank checks
        this.cmd = new RenderScreenCmd("", "DESKTOP"); // Blank screenId
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("required") 
                   || thrownException.getMessage().contains("exceeds"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        this.aggregate = new ScreenMapAggregate("sm-3");
        // Setup command that violates length constraints (max 10 for screenId)
        this.cmd = new RenderScreenCmd("VERY_LONG_SCREEN_ID", "MOBILE");
    }

}
