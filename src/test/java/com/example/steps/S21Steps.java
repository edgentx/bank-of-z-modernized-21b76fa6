package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class S21Steps {

    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        String id = "screen-map-123";
        aggregate = repository.create(id);
    }

    @And("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // Command construction happens in the 'When' step for flexibility
    }

    @And("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // Command construction happens in the 'When' step
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        // Default valid command for the happy path
        if (command == null) {
            command = new RenderScreenCmd(aggregate.id(), "LOGIN_SCR_01", "3270", null);
        }
        try {
            resultEvents = aggregate.execute(command);
            repository.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCR_01", event.getScreenId());
        assertEquals("3270", event.getDeviceType());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_missing_fields() {
        String id = "screen-map-invalid-1";
        aggregate = repository.create(id);
        // Create a command with missing fields
        command = new RenderScreenCmd(aggregate.id(), null, "3270", null);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_invalid_field_length() {
        String id = "screen-map-invalid-2";
        aggregate = repository.create(id);
        
        // Create a field list with a value exceeding BMS constraints
        // e.g., Field 'ACCOUNT_NUM' max length 10, but value is 12 chars
        List<ScreenMapAggregate.FieldDefinition> fields = new ArrayList<>();
        fields.add(new ScreenMapAggregate.FieldDefinition("ACCOUNT_NUM", "123456789012", 10));
        
        command = new RenderScreenCmd(aggregate.id(), "ACCT_SUMMARY", "3270", fields);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException);
    }
}