package com.example.steps;

import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.model.ScreenRenderedEvent;
import com.example.domain.screen.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in When clause construction
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in When clause construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("screen-map-invalid-fields");
        // Command will be constructed in 'When' to violate the rule
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-invalid-len");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Scenario context mapping based on the Gherkin state
            String screenId = "SCRN_01";
            String deviceType = "mobile"; 
            Map<String, String> inputs = new HashMap<>();

            // Adjust inputs based on the aggregate state/violation defined in Given
            if (aggregate.id().equals("screen-map-invalid-fields")) {
                deviceType = "desktop"; // Requires 'accountRef'
                inputs.put("someOtherField", "value"); // Missing 'accountRef'
            } else if (aggregate.id().equals("screen-map-invalid-len")) {
                inputs.put("accountRef", "A".repeat(81)); // Exceeds 80
            } else if (aggregate.id().equals("screen-map-1")) {
                // Happy path
                inputs.put("accountRef", "12345");
            }

            cmd = new RenderScreenCmd(aggregate.id(), screenId, deviceType, inputs);
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
