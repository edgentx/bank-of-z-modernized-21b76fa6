package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFieldsMustBeValidatedBeforeScreenSubmission() {
        aggregate = new ScreenMapAggregate("screen-map-2");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengthsMustStrictlyAdhereToLegacyBMSConstraintsDuringTheTransitionPeriod() {
        aggregate = new ScreenMapAggregate("screen-map-3");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Placeholder - handled in command construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Placeholder - handled in command construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        Command cmd;
        // Contextual command creation based on the aggregate state (Gherkin context)
        if (aggregate.id().equals("screen-map-2")) {
            // Violation: Missing mandatory input
            cmd = new RenderScreenCmd("screen-map-2", null, null, Map.of());
        } else if (aggregate.id().equals("screen-map-3")) {
            // Violation: Field length > 40 (BMS Constraint)
            String longScreenId = "ID-THAT-IS-VERY-LONG-AND-EXCEEDS-THE-LEGACY-LIMIT"; // > 40 chars
            cmd = new RenderScreenCmd("screen-map-3", longScreenId, "3270", Map.of());
        } else {
            // Success case
            cmd = new RenderScreenCmd("screen-map-1", "CUST-DETAIL-01", "3270", Map.of());
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}