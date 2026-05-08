package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.CustomerDeletedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        aggregate = new CustomerAggregate("cust-123");
        // Ensure it's in a valid state (Enrolled) to pass the "valid email/name" invariants for deletion
        aggregate.markEnrolled("John Doe", "john.doe@example.com", "GOV-ID-123");
        aggregate.setHasActiveAccounts(false); // Ensure no active accounts
    }

    @Given("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // The ID is already set in the aggregate creation
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_Customer_aggregate_that_violates_email_and_gov_id() {
        aggregate = new CustomerAggregate("cust-invalid");
        // We create an aggregate that hasn't been properly enrolled, or has invalid data.
        // Keeping email invalid triggers the exception in execute.
        // We assume 'enrolled' is false or data is null/invalid.
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_Customer_aggregate_that_violates_name_and_dob() {
        aggregate = new CustomerAggregate("cust-no-name");
        // Enroll with empty name to simulate the violation state.
        // However, the Enroll command itself prevents empty names.
        // So we assume a state where name is null.
        // Since we can't call execute(Enroll) with bad data (it throws), 
        // we assume the aggregate exists in a corrupted state or was modified directly.
        // We will use a standard enrollment but assume the internal state is manipulated for the test.
        aggregate.markEnrolled(null, "valid@example.com", "GOV-ID"); 
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_Customer_aggregate_that_violates_active_accounts() {
        aggregate = new CustomerAggregate("cust-active");
        aggregate.markEnrolled("Jane Doe", "jane@example.com", "GOV-ID-456");
        aggregate.setHasActiveAccounts(true);
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_DeleteCustomerCmd_command_is_executed() {
        DeleteCustomerCmd cmd = new DeleteCustomerCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDeletedEvent);
        assertEquals("customer.deleted", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        // Domain errors in this aggregate manifest as IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
