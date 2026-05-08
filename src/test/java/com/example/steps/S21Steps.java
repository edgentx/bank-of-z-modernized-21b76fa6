package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-1");
        // Setup valid state if needed
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled by command construction in When
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled by command construction in When
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Assuming the context implies valid data here for the happy path
        theRenderScreenCmdCommandIsExecutedWith("screen-1", "3270");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-invalid");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-bms-violation");
    }

    @When("the RenderScreenCmd command is executed with screenId {string} and deviceType {string}")
    public void theRenderScreenCmdCommandIsExecutedWith(String screenId, String deviceType) {
        try {
            Command cmd = new RenderScreenCmd(screenId, deviceType);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        } catch (UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception but command succeeded");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}