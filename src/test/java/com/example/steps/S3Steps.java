package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private List events;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
        aggregate.setFullName("Old Name");
        aggregate.setEmail("old@example.com");
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicitly handled by the aggregate setup or command construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Implicitly handled by command construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Implicitly handled by command construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
            "cust-123",
            "New Name",
            "new@example.com",
            "123456",
            "GOV-ID",
            false
        );
        try {
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertTrue(events.get(0) instanceof CustomerDetailsUpdatedEvent);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
        aggregate.setFullName("Valid Name");
        aggregate.setEmail("old@example.com");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidEmail() {
        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
            "cust-123",
            "Valid Name",
            "invalid-email", // Violates valid email
            "123456",
            "GOV-ID",
            false
        );
        try {
            events = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDoB() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
        aggregate.setFullName("Old Name");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithEmptyName() {
        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
            "cust-123",
            "", // Violates name not empty
            "new@example.com",
            "123456",
            "GOV-ID",
            false
        );
        try {
            events = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
        aggregate.setFullName("Name");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with active accounts")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithActiveAccounts() {
        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
            "cust-123",
            "Name",
            "test@example.com",
            "123456",
            "GOV-ID",
            true // Violates: has active accounts
        );
        try {
            events = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }
}
