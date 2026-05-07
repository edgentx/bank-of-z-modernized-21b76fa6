package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import com.example.domain.uimodel.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private Iterable<DomainEvent> events;

    // Scenario: Successfully execute RenderScreenCmd
    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Context stored implicitly in the command creation in the When step
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Context stored implicitly in the command creation in the When step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Using valid defaults for success case
        RenderScreenCmd cmd = new RenderScreenCmd("LOGINSCR", "3270");
        executeCommand(cmd);
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(events);
        assertTrue(events.iterator().hasNext());
        DomainEvent event = events.iterator().next();
        assertTrue(event instanceof ScreenRenderedEvent);
        ScreenRenderedEvent rendered = (ScreenRenderedEvent) event;
        assertEquals("screen.rendered", rendered.type());
        assertEquals("LOGINSCR", rendered.screenId());
        assertEquals("3270", rendered.deviceType());
    }

    // Scenario: RenderScreenCmd rejected — Mandatory input fields
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_mandatory_violations() {
        aggregate = new ScreenMapAggregate("screen-map-violation-mandatory");
        repository.save(aggregate);
    }

    // Reusing When from above logic via specific dispatching or context aware step
    // In Cucumber, we can differentiate by context, but here we define specific flow
    @When("the RenderScreenCmd command is executed with null screenId")
    public void the_render_screen_cmd_command_is_executed_with_null_screenid() {
        RenderScreenCmd cmd = new RenderScreenCmd(null, "MOBILE");
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    // Scenario: RenderScreenCmd rejected — Field lengths
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_length_violations() {
        aggregate = new ScreenMapAggregate("screen-map-violation-length");
        repository.save(aggregate);
    }

    @When("the RenderScreenCmd command is executed with invalid length")
    public void the_render_screen_cmd_command_is_executed_with_invalid_length() {
        // "THIS-IS-A-VERY-LONG-SCREEN-ID" is > 8 chars
        RenderScreenCmd cmd = new RenderScreenCmd("THIS-IS-A-VERY-LONG-SCREEN-ID", "WEB");
        executeCommand(cmd);
    }

    private void executeCommand(RenderScreenCmd cmd) {
        try {
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}