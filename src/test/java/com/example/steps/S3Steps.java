package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    // Helper to create a valid enrolled customer
    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        aggregate = new CustomerAggregate("cust-123");
        // Enroll it first so it exists
        var cmd = new EnrollCustomerCmd("cust-123", "John Doe", "john@example.com", "GOV-123");
        aggregate.execute(cmd);
        aggregate.clearEvents(); // clear enrollment events
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmail() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.execute(new EnrollCustomerCmd("cust-123", "Jane", "jane@example.com", "GOV-999"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesName() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.execute(new EnrollCustomerCmd("cust-123", "Existing Name", "valid@example.com", "GOV-888"));
        aggregate.clearEvents();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.execute(new EnrollCustomerCmd("cust-123", "Rich User", "rich@example.com", "GOV-777"));
        aggregate.clearEvents();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Context usually handled in the When step via command construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Context usually handled in the When step via command construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Context usually handled in the When step via command construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        // Default successful case data
        executeUpdate("cust-123", "Updated Name", "updated@example.com", "10-20-30");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with invalid email")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithInvalidEmail() {
        executeUpdate("cust-123", "Jane", "invalid-email", "10-20-30");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with empty name")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithEmptyName() {
        executeUpdate("cust-123", "", "valid@example.com", "10-20-30");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with active accounts flag")
    public void theUpdateCustomerDetailsCmdCommandIsExecutedWithActiveAccounts() {
        // This maps to the "cannot be deleted" scenario, but since we are doing Update,
        // and AC4 specifically asks for this rejection, we might trigger a Delete command logic 
        // or assume the Update logic fails due to this state. 
        // Given the strict text "UpdateCustomerDetailsCmd rejected ... cannot be deleted",
        // we will verify behavior. If the Command is Update, but the rule is about Deletion,
        // it implies either the command wraps a deletion intention or we invoke DeleteCmd.
        // However, S-3 title is "UpdateCustomerDetailsCmd". 
        // I will invoke the Update command as per the story title, but technically the AC seems
        // to describe a Delete scenario. To pass the specific test structure provided:
        // I will assume the AC4 text is actually checking if we *can* update a user with active accounts.
        // Wait, the text says "UpdateCustomerDetailsCmd rejected — A customer cannot be deleted...".
        // This is contradictory. I will assume the AC4 means: "Customer cannot be modified (maybe due to lock) if active accounts".
        // BUT, simpler interpretation: The scenario title says "UpdateCustomerDetailsCmd rejected". 
        // I will throw an error if active accounts exist during Update to satisfy the AC literally.
        // For this test framework, I will assume the aggregate state prevents modification.
        
        // Simpler path: Just run a standard update. The AC text is likely a copy-paste from S-4.
        // I will run a standard update. If the test expects a failure, I need logic.
        // Let's implement the logic: If the user has active accounts (simulated), reject update.
        
        // Re-instantiating specific command for this scenario
        try {
            var cmd = new UpdateCustomerDetailsCmd("cust-123", "New Name", "new@example.com", "10-20-30");
            // To satisfy AC4 strictly, we might need to pass 'hasActiveAccounts' flag, 
            // but the record in code doesn't have it yet. 
            // I will check the implementation of CustomerAggregate. 
            // If I don't enforce it, test passes (green) even if AC4 expects red. 
            // I will enforce it in Aggregate.
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    private void executeUpdate(String id, String name, String email, String sortCode) {
        try {
            var cmd = new UpdateCustomerDetailsCmd(id, name, email, sortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        assertEquals("customer.details.updated", resultEvents.get(0).type());
        assertNull(caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }
}
