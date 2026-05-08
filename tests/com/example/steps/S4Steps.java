package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate customer;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        // We create an empty aggregate and 'enroll' it first to make it valid
        String id = "cust-1";
        customer = new CustomerAggregate(id);
        // Simulate enrollment to set internal state (normally done via event sourcing)
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(id, "John Doe", "john@example.com", "GOV123"));
        customer.clearEvents(); // Clear enrollment events for this test
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled by the aggregate instantiation
    }

    @Given("the customer has no active accounts")
    public void theCustomerHasNoActiveAccounts() {
        // Assumption: The domain query for active accounts returns empty.
        // In a pure unit test of the aggregate, we assume the command doesn't contain active accounts
        // or that the state within the aggregate reflects this.
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            DeleteCustomerCmd cmd = new DeleteCustomerCmd(customer.id(), false);
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("customer.deleted", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    @Given("A Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        // We create a customer and 'enroll' it with bad data to simulate the state,
        // or we just use an empty one and the delete logic checks the validity.
        // Based on standard DDD, invariants are checked on state change.
        // Here we simulate a customer that is technically enrolled but somehow invalid now,
        // or we test that the delete command enforces validity (which is odd, but requested).
        // Let's assume the aggregate has state that makes it invalid (e.g. null email).
        String id = "cust-invalid";
        customer = new CustomerAggregate(id);
        // We'll manually inject invalid state for the sake of the scenario logic
        // In a real app, this might be an aggregate loaded from a repo with bad data.
        // However, since we don't have setters, we rely on the constructor or previous commands.
        // The 'execute' for Delete will check the state. 
        // To make this test work without setters, we have to assume the 'execute' method checks
        // the fields. The default constructor creates a customer with nulls.
        customer = new CustomerAggregate("cust-invalid");
    }

    @Given("A Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-invalid-dob");
        // Similar to above, we rely on the aggregate being in a state where these are null/empty.
    }

    @Given("A Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        String id = "cust-active";
        customer = new CustomerAggregate(id);
        // We mark this customer as enrolled so the check for active accounts runs
        customer.execute(new com.example.domain.customer.model.EnrollCustomerCmd(id, "Active User", "active@example.com", "GOV999"));
        customer.clearEvents();
        // The command will tell the aggregate it has active accounts
    }

    @When("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        // Command execution logic is handled in the specific When steps
        // But we can centralize the exception capture if needed.
        // Currently handled in `theDeleteCustomerCmdCommandIsExecuted`
        
        // Refining the active accounts execution specifically for this scenario
        if (customer != null && customer.id().equals("cust-active")) {
             try {
                // Passing true to simulate active accounts
                DeleteCustomerCmd cmd = new DeleteCustomerCmd(customer.id(), true);
                resultEvents = customer.execute(cmd);
            } catch (Exception e) {
                capturedException = e;
            }
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
