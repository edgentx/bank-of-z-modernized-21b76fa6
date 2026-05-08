package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class S4Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-123");
        // Enroll using the command pattern to ensure valid state
        aggregate.execute(new EnrollCustomerCmd(
                "cust-123",
                "John Doe",
                "john.doe@example.com",
                "GOV-ID-123"
        ));
        aggregate.clearEvents(); // Clear enrollment events for clean test
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        // Create a blank aggregate (unenrolled) so email/ID are null
        aggregate = new CustomerAggregate("cust-invalid");
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aggregate = new CustomerAggregate("cust-invalid");
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccountsConstraint() {
        aggregate = new CustomerAggregate("cust-active");
        // Enroll normally first
        aggregate.execute(new EnrollCustomerCmd(
                "cust-active",
                "Jane Doe",
                "jane.doe@example.com",
                "GOV-ID-456"
        ));
        aggregate.clearEvents();
        // Force the internal state flag for active accounts
        aggregate.markHasActiveAccounts(true);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Already handled in aggregate construction
        assertThat(aggregate.id()).isNotNull();
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(new DeleteCustomerCmd(aggregate.id()));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertThat(capturedException).isNull();
        assertThat(resultEvents).hasSize(1);
        assertThat(resultEvents.get(0).type()).isEqualTo("customer.deleted");
        assertThat(aggregate.isDeleted()).isTrue();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertThat(capturedException).isNotNull();
        // We accept IllegalArgumentException or IllegalStateException as domain errors
        assertThat(capturedException).isInstanceOf(IllegalArgumentException.class)
                .isInstanceOf(IllegalStateException.class);
    }
}
