package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-123");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context established in aggregate constructor or command setup
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context established in command setup
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateViolatingMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("screen-bad-fields");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateViolatingBMSConstraints() {
        this.aggregate = new ScreenMapAggregate("screen-bad-length");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            Command cmd;
            // Based on the 'Given' context, we construct the appropriate command scenario
            // For simplicity in this step definition, we inspect the aggregate ID to determine the test case
            String id = aggregate.id();
            if (id.contains("bad-fields")) {
                cmd = new RenderScreenCmd(id, "MAIN_MENU", null); // Violates deviceType != null
            } else if (id.contains("bad-length")) {
                cmd = new RenderScreenCmd(id, "A_VERY_LONG_SCREEN_NAME_THAT_EXCEEDS_BMS_LIMIT", "3270"); // Violates length
            } else {
                cmd = new RenderScreenCmd(id, "MAIN_MENU", "WEB_BROWSER");
            }
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected exception was not thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException,
                "Exception should be a domain error (IllegalArgumentException)");
    }
}
