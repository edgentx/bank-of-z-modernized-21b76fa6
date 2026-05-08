package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ScreenRenderedEvent;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
        repository.save(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in When step context for simplicity, or we could store state in a context object
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in When step context
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Default valid values for "Happy Path" scenario if not specified by Given violations
        if (aggregate == null) {
            // If aggregate wasn't initialized in a specific violation Given, initialize it now
            aggregate = new ScreenMapAggregate("screen-map-test");
        }
        
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("screen-map-1", "MENUI01", "3270");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid input")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidInput() {
        // We use this generic hook to trigger execution for violation scenarios 
        // assuming the aggregate state was set up to fail or we pass bad data here.
        // However, to support specific violation Givens, we'll handle command creation in the When
        // based on context, or we assume the violation is in the Command payload we construct here.
        
        // Actually, Gherkin scenarios usually drive specific inputs. 
        // Let's refine the approach: The specific violation scenarios below will override this generic execution.
    }

    // --- Scenario: Mandatory Input Validation ---
    
    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("screen-map-violation-mandatory");
    }

    @When("the RenderScreenCmd command is executed with missing screenId")
    public void theRenderScreenCmdCommandIsExecutedWithMissingScreenId() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd("screen-map-violation-mandatory", "", "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // --- Scenario: Legacy BMS Constraints ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("screen-map-violation-length");
    }

    @When("the RenderScreenCmd command is executed with invalid length")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidLength() {
        try {
            // BMS max is 7 chars for screenId. Using 8 chars to violate.
            RenderScreenCmd cmd = new RenderScreenCmd("screen-map-violation-length", "LONGMENU01", "3270");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    // --- Assertions ---

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("MENUI01", event.screenId());
        assertEquals("3270", event.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
