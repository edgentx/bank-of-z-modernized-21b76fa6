package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S3Steps {

    private CustomerAggregate aggregate;
    private String customerId;
    private String email;
    private String sortCode;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Flags to simulate Application Service checks passed to the Command
    private boolean hasActiveAccounts = false;
    private boolean isEmailUnique = true;
    private boolean isValidEmail = true;
    private boolean isValidGovernmentId = true;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customerId = "cust-123";
        aggregate = new CustomerAggregate(customerId);
        // We assume the aggregate is hydrated/loaded. 
        // For unit testing the Command logic directly on the aggregate, 
        // we might need to ensure the aggregate is in a valid state (e.g. Enrolled).
        // However, the Update Command logic is self-contained in `execute`.
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesUniqueness() {
        // In a real scenario, this might be set up via repo state.
        // Here we control the command flags to trigger the invariant violation.
        isEmailUnique = false;
        aValidCustomerAggregate();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameOrDob() {
        // To test this invariant on Update, we need the aggregate's internal state to be invalid,
        // OR the command payload to be invalid. The AC implies the *Aggregate* violates it.
        // Since we can't easily set internal fields of the Aggregate without a memento or loading,
        // we assume the existing code path checks `this.fullName`.
        // We will create a raw aggregate and assume it wasn't loaded with a name.
        customerId = "cust-empty";
        aggregate = new CustomerAggregate(customerId);
        // Note: In a real test, we'd load from a repo that returns an empty object. 
        // Here we rely on the specific implementation of `updateDetails` checking state.
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        // The violation is triggered by the command flag `hasActiveAccounts`
        hasActiveAccounts = true;
        aValidCustomerAggregate();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        customerId = "cust-123";
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        email = "new.email@example.com";
        isValidEmail = true;
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        sortCode = "10-20-30";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        if (isValidEmail && !email.contains("@")) {
            isValidEmail = false; // double check logic
        }
        
        var cmd = new UpdateCustomerDetailsCmd(
            customerId, 
            email, 
            sortCode, 
            hasActiveAccounts, 
            isEmailUnique
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("customer.details.updated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // We accept IllegalStateException or IllegalArgumentException as domain errors in this context
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || 
            thrownException instanceof IllegalArgumentException
        );
    }
}
