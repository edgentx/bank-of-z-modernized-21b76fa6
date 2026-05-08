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

    private CustomerAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-1");
        aggregate.hydrate("John Doe", "john@example.com", "GOV-123", 0);
        aggregate.setDateOfBirth("1990-01-01");
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Implicit in aggregate construction
    }

    @When("the DeleteCustomerCmd command is executed")
    public void theDeleteCustomerCmdCommandIsExecuted() {
        try {
            DeleteCustomerCmd cmd = new DeleteCustomerCmd("cust-1");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void aCustomerDeletedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("customer.deleted", resultEvents.get(0).type());
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatIsValidEmailGovId() {
        aggregate = new CustomerAggregate("cust-2");
        aggregate.hydrate("Jane Doe", null, null, 0);
        aggregate.setDateOfBirth("1992-02-02");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameOrDob() {
        aggregate = new CustomerAggregate("cust-3");
        aggregate.hydrate(null, "jane@example.com", "GOV-456", 0);
        aggregate.setDateOfBirth(null);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-4");
        aggregate.hydrate("Jim Doe", "jim@example.com", "GOV-789", 1);
        aggregate.setDateOfBirth("1993-03-03");
    }
}
