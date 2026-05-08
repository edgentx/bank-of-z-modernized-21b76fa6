package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.ScreenMapRepository;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    static class InMemoryScreenMapRepository implements ScreenMapRepository {
        @Override
        public ScreenMapAggregate load(String id) {
            return new ScreenMapAggregate(id);
        }
    }

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = repository.load("screen-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled in context of command execution
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Handled in context of command execution
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            Command cmd = new RenderScreenCmd("screen-1", "3270", 1920, 1080);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = repository.load("screen-invalid-fields");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_RenderScreenCmd_command_is_executed_with_missing_fields() {
        try {
            // Execute with null screenId (invalid)
            Command cmd = new RenderScreenCmd(null, "3270", 800, 600);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_bms_constraints() {
        aggregate = repository.load("screen-bad-bms");
    }

    @When("the RenderScreenCmd command is executed with invalid BMS lengths")
    public void the_RenderScreenCmd_command_is_executed_with_invalid_BMS_lengths() {
        try {
            // Assume screenId "BAD" is valid, but resolution is way off for legacy
            Command cmd = new RenderScreenCmd("BAD", "3270", 10000, 10000);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
