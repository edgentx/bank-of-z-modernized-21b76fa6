package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uinavigation.repository.ScreenMapRepository;
import com.example.domain.userinterface.model.DeviceType;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMap;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

public class S21Steps {

    private ScreenMap aggregate;
    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    static class InMemoryScreenMapRepository implements ScreenMapRepository {
        private ScreenMap store;
        @Override
        public ScreenMap save(ScreenMap aggregate) {
            this.store = aggregate;
            return aggregate;
        }
        @Override
        public Optional<ScreenMap> findById(String id) {
            return Optional.ofNullable(store);
        }
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMap("map-123");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context setup for command
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context setup for command
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingInputs() {
        aggregate = new ScreenMap("map-invalid-1");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithLongInputs() {
        aggregate = new ScreenMap("map-invalid-2");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Scenario 1: Valid
            if (aggregate.getId().equals("map-123")) {
                RenderScreenCmd cmd = new RenderScreenCmd("ACCTSUM", DeviceType.WEB_DESKTOP);
                resultEvents = aggregate.execute(cmd);
            }
            // Scenario 2: Missing Inputs
            else if (aggregate.getId().equals("map-invalid-1")) {
                RenderScreenCmd cmd = new RenderScreenCmd(null, null);
                resultEvents = aggregate.execute(cmd);
            }
            // Scenario 3: Long Inputs
            else if (aggregate.getId().equals("map-invalid-2")) {
                RenderScreenCmd cmd = new RenderScreenCmd("TOOLONGSCREENID", DeviceType.TERMINAL_3270);
                resultEvents = aggregate.execute(cmd);
            }
        } catch (IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("ACCTSUM", event.screenId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Ideally check it's an IllegalArgumentException or specific Domain Error
    }
}
