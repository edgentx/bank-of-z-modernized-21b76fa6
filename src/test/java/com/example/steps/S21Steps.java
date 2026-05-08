package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SM-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Default valid screenId used in context construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Default valid deviceType used in context construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // If not already set by a 'violates' context, default to valid values
        if (command == null) {
            command = new RenderScreenCmd("SM-001", "LOGINSCRN", "3270");
        }

        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        } catch (Exception e) {
            // Catch all to ensure we report the failure
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("SM-001", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain error exception but command succeeded.");
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || 
            capturedException instanceof IllegalStateException ||
            capturedException instanceof UnknownCommandException,
            "Exception type mismatch: " + capturedException.getClass().getSimpleName()
        );
    }

    // --- Specific Violation Contexts ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("SM-ERR-01");
        // Scenario 1: Null ScreenId
        command = new RenderScreenCmd("SM-ERR-01", null, "3270");
        // Note: In a real loop, we'd parameterize this. For BDD story S-21, we verify the rejection logic exists.
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SM-ERR-02");
        // Scenario: Screen ID > 8 chars (BMS constraint)
        command = new RenderScreenCmd("SM-ERR-02", "TO_LONG_SCREEN_NAME", "3270");
    }
}