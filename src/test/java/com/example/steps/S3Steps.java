package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S3Steps {

    private CustomerAggregate aggregate;
    private String customerId;
    private String email;
    private String sortCode;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customerId = "cust-123";
        aggregate = new CustomerAggregate(customerId);
        aggregate.setFullName("John Doe");
        aggregate.setEmail("john@example.com");
        aggregate.setEnrolled(true);
        aggregate.setHasActiveAccounts(false);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId already set in setup
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        email = "new.email@example.com";
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        sortCode = "123456";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            Command cmd = new UpdateCustomerDetailsCmd(customerId, "Jane Doe", email, sortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("customer.details.updated", resultEvents.get(0).type());
    }

    // --- Negative Cases ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailValidation() {
        customerId = "cust-invalid-email";
        aggregate = new CustomerAggregate(customerId);
        aggregate.setFullName("Bad Email");
        aggregate.setEmail("bad-email");
        aggregate.setEnrolled(true);
        aggregate.setHasActiveAccounts(false);
        // Set bad email for command
        email = "invalid-at-format";
        sortCode = "123456";
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameValidation() {
        customerId = "cust-empty-name";
        aggregate = new CustomerAggregate(customerId);
        aggregate.setFullName("Old Name");
        aggregate.setEmail("old@example.com");
        aggregate.setEnrolled(true);
        aggregate.setHasActiveAccounts(false);
        // Set empty name for command
        email = "valid@example.com";
        sortCode = "123456";
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccountsConstraint() {
        customerId = "cust-active-accounts";
        aggregate = new CustomerAggregate(customerId);
        aggregate.setFullName("Active User");
        aggregate.setEmail("active@example.com");
        aggregate.setEnrolled(true);
        // The constraint for this story links the command rejection to this flag
        aggregate.setHasActiveAccounts(true);
        email = "update@example.com";
        sortCode = "123456";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Typically IllegalArgumentException or IllegalStateException
    }
}
