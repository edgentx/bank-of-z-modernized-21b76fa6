package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("map-001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup handled in the 'When' block via command construction
        // or we could store state here if the flow was more complex.
        // For this pattern, we define 'valid' implicitly by the construction in execute.
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Same as above.
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        RenderScreenCmd cmd = new RenderScreenCmd("map-001", "SCRN01", "3270");
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("map-001", event.aggregateId());
        assertEquals("SCRN01", event.screenId());
    }

    // --- Error Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFieldsMustBeValidatedBeforeScreenSubmission() {
        aggregate = new ScreenMapAggregate("map-001");
        // The violation happens when we send a command with null/blank data
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedInvalid() {
        // Intentionally invalid command (missing screenId)
        RenderScreenCmd cmd = new RenderScreenCmd("map-001", null, "3270");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        // Optionally check message content
        assertTrue(capturedException.getMessage().contains("required"));
    }

    // --- BMS Constraints Scenario ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengthsMustStrictlyAdhereToLegacyBMSConstraintsDuringTheTransitionPeriod() {
        aggregate = new ScreenMapAggregate("map-001");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedLongScreenId() {
        // ScreenId length > 8 triggers BMS constraint error
        RenderScreenCmd cmd = new RenderScreenCmd("map-001", "VERY_LONG_SCREEN_ID", "3270");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }
}