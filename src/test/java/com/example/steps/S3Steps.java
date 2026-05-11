package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.function.Supplier;

public class S3Steps {

    private CustomerAggregate customer;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        // Enroll the customer first to establish a valid state
        customer.execute(new EnrollCustomerCmd("cust-123", "John Doe", "john.doe@example.com", "GOV-ID-123"));
        customer.clearEvents(); // Clear enrollment events for cleaner test output
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicitly handled by the aggregate initialization
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Implicitly handled by the command construction in the When step
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Implicitly handled by the command construction in the When step
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        executeCommand(() -> customer.execute(new UpdateCustomerDetailsCmd("cust-123", "Jane Doe", "jane.doe@example.com", "123456")));
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "One event should be emitted");
        Assertions.assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent, "Event should be CustomerDetailsUpdatedEvent");
    }

    // --- Negative Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        customer = new CustomerAggregate("cust-invalid");
        customer.execute(new EnrollCustomerCmd("cust-invalid", "Invalid User", "invalid@example.com", "GOV-999"));
        customer.clearEvents();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidEmail() {
        executeCommand(() -> customer.execute(new UpdateCustomerDetailsCmd("cust-invalid", "Invalid User", "bad-email", "123456")));
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-empty");
        customer.execute(new EnrollCustomerCmd("cust-empty", "Existing Name", "existing@example.com", "GOV-888"));
        customer.clearEvents();
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithEmptyName() {
        executeCommand(() -> customer.execute(new UpdateCustomerDetailsCmd("cust-empty", "", "valid@example.com", "123456")));
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccountsForDelete() {
        customer = new CustomerAggregate("cust-active");
        customer.execute(new EnrollCustomerCmd("cust-active", "Active User", "active@example.com", "GOV-777"));
        customer.clearEvents();
    }

    @When("the DeleteCustomerCmd command is executed indicating active accounts")
    public void theDeleteCustomerCmdCommandIsExecutedIndicatingActiveAccounts() {
        executeCommand(() -> customer.execute(new DeleteCustomerCmd("cust-active", true)));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected a domain error (IllegalArgument or IllegalState), got: " + capturedException.getClass().getSimpleName()
        );
    }

    private void executeCommand(Supplier<List<DomainEvent>> commandExecutor) {
        try {
            resultEvents = commandExecutor.get();
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
