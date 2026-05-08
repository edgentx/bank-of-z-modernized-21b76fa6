package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_screen_map_aggregate() {
        aggregate = new ScreenMapAggregate("test-screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screen_id_is_provided() {
        // We'll set this in the 'When' or separate state if needed, but assuming standard command construction
    }

    @Given("a valid deviceType is provided")
    public void a_valid_device_type_is_provided() {
        // We'll set this in the 'When'
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_screen_map_aggregate_with_mandatory_violation() {
        aggregate = new ScreenMapAggregate("test-screen-map-invalid");
        // The violation will be in the command we create next
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_screen_map_aggregate_with_length_violation() {
        aggregate = new ScreenMapAggregate("test-screen-map-bms-violation");
        // The violation will be in the command we create next
    }

    @When("the RenderScreenCmd command is executed")
    public void the_render_screen_cmd_command_is_executed() {
        try {
            // Determine which scenario we are in by checking context or just trying valid one
            // For simplicity in BDD steps without shared state, we assume the specific Given sets the state
            // But here we need to distinguish. Let's assume we construct the command based on the context implicitly.
            // However, Cucumber doesn't link Givens to Whens like that explicitly without state.
            // Let's assume we are testing the VALID case here, or we need a parameterized Given.
            // Actually, the Givens above set up the Aggregate. We need to set the command here.
            // To make this work for all scenarios, we should check which aggregate we have or use a flag.
            // A better way: The Given creates the command data.
            
            // Let's rely on the specific violation scenarios to set the command data explicitly in the Given 
            // if we wanted to be strict, but typically we parse the text.
            // For this implementation, I'll assume standard valid command unless overridden by scenario context 
            // (not shown in snippet, so I'll default to valid for the 'valid' scenario and check the specific Givens).
            
            // Wait, the Givens for violation say "Given a ScreenMap aggregate...". 
            // I will assume the violation logic is injected into the 'cmd' variable via reflection or state.
            // Let's just create a valid command here. If the test is for violation, the step definition for that 
            // scenario should have set up the bad command.
            // Since I can't easily detect "which scenario I am in" inside the step without tags, 
            // I will assume the user sets up the data in the Given steps specifically.
            
            // Re-reading standard patterns: The Given usually sets the state.
            // Let's check if cmd is null. If so, default to valid.
            if (cmd == null) {
                 // If we are in the violation scenarios, we should have set cmd in a specific step. 
                 // If we didn't, let's assume valid inputs for the happy path.
                 // BUT, the violation scenarios don't have a specific "And command is X" step.
                 // They have "Given a ScreenMap aggregate that violates...".
                 // This implies the violation comes from the aggregate state OR the command passed to it.
                 // Since the violations are about INPUT fields, the command is the carrier.
                 // I will assume the 'cmd' is populated in the violation Givens if I were to write them specifically.
                 // For the sake of this code, I will assume the Happy Path here, and override in specific methods if I could.
                 // Since I can only define one method per regex, and the regexes are different for the violation Givens, 
                 // I will set the cmd there.
                 
                 // Fallback logic:
                 this.cmd = new RenderScreenCmd("LOGIN_SCR", "3270");
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid data")
    public void the_render_screen_cmd_command_is_executed_with_invalid_data() {
         // This regex matches the generic 'When' in the feature file provided. 
         // I mapped the specific violation Givens to setup the 'cmd' variable below.
         try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Since the feature file uses the exact same "When" text for all scenarios, 
    // the method above `the_render_screen_cmd_command_is_executed` handles ALL of them.
    // I need to ensure the violation Givens set the `cmd` variable to the failing cases.
    
    // Let's assume the valid case defaults it, and the violation cases overwrite it? 
    // No, the Givens run first.
    // So I will create specific implementations for the Givens to set the bad command.

    @Override // Actually just defining more Given methods matching the specific texts
    public void a_screen_map_aggregate_with_mandatory_violation() {
        aggregate = new ScreenMapAggregate("test-screen-map-invalid");
        // Setup invalid command
        this.cmd = new RenderScreenCmd("", "3270"); // Blank screenId
    }

    @Override
    public void a_screen_map_aggregate_with_length_violation() {
        aggregate = new ScreenMapAggregate("test-screen-map-bms-violation");
        // Setup invalid command (length > 80)
        String longId = "A".repeat(81);
        this.cmd = new RenderScreenCmd(longId, "3270");
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalArgumentException);
    }
}
