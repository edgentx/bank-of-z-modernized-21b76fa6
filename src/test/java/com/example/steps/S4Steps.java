package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S4Steps {

    private CustomerAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("customer-123");
        // Hydrate with valid default state
        aggregate.hydrate("John Doe", "john.doe@example.com", "GOV-ID-999", 0);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled by aggregate initialization in previous step
    }

    @And("the customer has no active accounts")
    public void theCustomerHasNoActiveAccounts() {
        // Handled by default hydration
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            DeleteCustomerCmd cmd = new DeleteCustomerCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("customer.deleted", resultEvents.get(0).type());
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        aggregate = new CustomerAggregate("customer-bad-id");
        // Missing email and Gov ID
        aggregate.hydrate("Jane Doe", null, null, 0);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = new CustomerAggregate("customer-bad-name");
        // Missing Name
        aggregate.hydrate(null, "jane@example.com", "GOV-ID-123", 0);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("customer-with-accounts");
        // Has active accounts
        aggregate.hydrate("Rich User", "rich@example.com", "GOV-ID-888", 5);
    }

    @And("the customer has active bank accounts")
    public void theCustomerHasActiveBankAccounts() {
        aggregate.hydrate("Active User", "active@example.com", "GOV-ID-777", 10);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(
                caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
                "Expected IllegalArgumentException or IllegalStateException, but got: " + caughtException.getClass()
        );
    }
}
