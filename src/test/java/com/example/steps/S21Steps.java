package com.example.steps;

import com.example.domain.screen.model.RenderScreenCmd;
import com.example.domain.screen.model.ScreenMapAggregate;
import com.example.domain.screen.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Stored for usage in command construction
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Stored for usage in command construction
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_violations() {
        aggregate = new ScreenMapAggregate("screen-map-invalid");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_length_violations() {
        aggregate = new ScreenMapAggregate("screen-map-len");
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Context determines what parameters to pass
        if (aggregate.id().equals("screen-map-1")) {
            cmd = new RenderScreenCmd("screen-map-1", "LOGIN", "WEB_DESKTOP");
        } else if (aggregate.id().equals("screen-map-invalid")) {
            cmd = new RenderScreenCmd("screen-map-invalid", "", "WEB_DESKTOP");
        } else if (aggregate.id().equals("screen-map-len")) {
            cmd = new RenderScreenCmd("screen-map-len", "VERY_LONG_SCREEN_ID", "WEB_DESKTOP");
        }

        try {
            // Reload aggregate to simulate persistent state
            aggregate = repository.findById(aggregate.id()).orElseThrow();
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("screen.rendered", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
