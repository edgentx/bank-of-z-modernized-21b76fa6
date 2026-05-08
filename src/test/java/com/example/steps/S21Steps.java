package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private Exception thrownException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    // In-memory repository for testing
    static class InMemoryScreenMapRepository implements ScreenMapRepository {
        @Override
        public ScreenMapAggregate save(ScreenMapAggregate aggregate) {
            return aggregate;
        }
        @Override
        public java.util.Optional<ScreenMapAggregate> findById(String id) {
            return java.util.Optional.empty();
        }
        @Override
        public ScreenMapAggregate load(String id) {
            return new ScreenMapAggregate(id);
        }
    }

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = repository.load("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // screenId will be set in the When clause construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // deviceType will be set in the When clause construction
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Construct a valid command for the 'Success' scenario or unless overridden by specific Givens
        if (cmd == null) {
            cmd = new RenderScreenCmd("screen-map-1", "LOGIN_SCR", "3270", "USER [_____] PASS [_____");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputFields() {
        aggregate = repository.load("screen-map-invalid");
        // Violate mandatory fields: null screenId
        cmd = new RenderScreenCmd("screen-map-invalid", null, "3270", "data");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSConstraints() {
        aggregate = repository.load("screen-map-bms");
        // Create a layout string > 80 chars to violate legacy BMS constraint
        String longLayout = "X".repeat(100); 
        cmd = new RenderScreenCmd("screen-map-bms", "SCR_01", "3270", longLayout);
    }
}
