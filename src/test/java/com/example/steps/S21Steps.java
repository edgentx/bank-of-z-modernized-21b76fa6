package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCREEN-001");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup handled in 'when' via command construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup handled in 'when' via command construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCREEN-ERR-01");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("SCREEN-BMS-01");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Determine command based on context (simulated by state or simple defaults for test)
        // For simplicity in this Step definition, we infer the intent based on the aggregate ID used in Given
        Command cmd;
        if (aggregate.id().equals("SCREEN-001")) {
            cmd = new RenderScreenCmd("SCREEN-001", "MOBILE", "MAIN_MENU");
        } else if (aggregate.id().equals("SCREEN-ERR-01")) {
            cmd = new RenderScreenCmd("SCREEN-ERR-01", null, "MAIN_MENU"); // Violates mandatory
        } else if (aggregate.id().equals("SCREEN-BMS-01")) {
            cmd = new RenderScreenCmd("SCREEN-BMS-01", "3270", "THIS_FIELD_IS_DEFINITELY_TOO_LONG_FOR_LEGACY_BMS"); // Violates length
        } else {
            cmd = new RenderScreenCmd("UNKNOWN", "UNKNOWN", "UNKNOWN");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        } catch (UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertTrue(resultEvents.iterator().hasNext());
        Assertions.assertTrue(resultEvents.iterator().next() instanceof ScreenRenderedEvent);
        Assertions.assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
        Assertions.assertNull(resultEvents); // Or empty list depending on strictness
    }
}
