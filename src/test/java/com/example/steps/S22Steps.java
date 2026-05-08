package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.InputValidatedEvent;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.model.ValidateScreenInputCmd;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private final InMemoryScreenMapRepository repository = new InMemoryScreenMapRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("SCRN001");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Implicitly handled by aggregate initialization or command construction
        // We will ensure the command uses the aggregate's ID
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in the When block by constructing a valid map
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        // Prepare valid inputs matching the hardcoded constraints in ScreenMapAggregate
        Map<String, String> inputs = new HashMap<>();
        inputs.put("amount", "100");
        inputs.put("acct", "12345");
        
        executeCommand(inputs);
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
    }

    // --- Scenario 2 ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("SCRN002");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecutedWithMissingFields() {
        // Missing 'amount' (mandatory)
        Map<String, String> inputs = new HashMap<>();
        inputs.put("acct", "12345");
        
        executeCommand(inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertTrue(thrownException.getMessage().contains("mandatory"));
    }

    // --- Scenario 3 ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("SCRN003");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecutedWithLongFields() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("amount", "100");
        inputs.put("acct", "123456789012345678901"); // Length 21, but max is 20 in Aggregate definition
        
        executeCommand(inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainErrorForLength() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertTrue(thrownException.getMessage().contains("constraints"));
    }

    // Helper
    private void executeCommand(Map<String, String> inputs) {
        try {
            Command cmd = new ValidateScreenInputCmd(aggregate.id(), inputs);
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (IllegalArgumentException | UnknownCommandException e) {
            thrownException = e;
        }
    }
}
