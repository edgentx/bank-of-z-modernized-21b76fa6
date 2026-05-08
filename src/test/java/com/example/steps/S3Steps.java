package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainException; // Assuming a standard exception or using IllegalArgumentException/IllegalStateException
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S3Steps {

    private CustomerAggregate aggregate;
    private UpdateCustomerDetailsCmd command;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;
    private Exception capturedException;

    // --- Given Steps ---

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        this.aggregate = new CustomerAggregate("cust-123")
                .setEnrolled(true)
                .setFullName("John Doe")
                .setEmail("john@example.com")
                .setGovernmentId("GOV-123")
                .setHasActiveAccounts(false);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        this.aggregate = new CustomerAggregate("cust-invalid")
                .setEnrolled(true)
                .setFullName("Jane Doe")
                .setEmail("invalid-email")
                .setGovernmentId("") // Empty
                .setHasActiveAccounts(false);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDoB() {
        this.aggregate = new CustomerAggregate("cust-empty-name")
                .setEnrolled(true)
                .setFullName("") // Empty
                .setEmail("jane@example.com")
                .setGovernmentId("GOV-456")
                .setHasActiveAccounts(false);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccountsConstraint() {
        // To simulate the constraint violation, we set the aggregate state to have active accounts.
        // The command will be constructed or the aggregate state will be such that the logic fails.
        // Since the AC implies the delete/update action fails, we set the flag indicating active accounts exist.
        this.aggregate = new CustomerAggregate("cust-active")
                .setEnrolled(true)
                .setFullName("Rich User")
                .setEmail("rich@example.com")
                .setGovernmentId("GOV-789")
                .setHasActiveAccounts(true); // This should cause the failure
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled implicitly in aggregate construction, or we could store it for the command
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Handled in the command construction step
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in the command construction step
    }

    // --- When Steps ---

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // Construct command based on the scenario context.
            // We use the aggregate's current state to populate fields that aren't explicitly violated in the "Given".
            // The AC implies specific violations. We need to ensure the command triggers the specific check.
            // However, the command *contains* the data. The aggregate *validates* it.

            // For the "Active Accounts" scenario, the domain logic checks the aggregate state (hasActiveAccounts)
            // against the command or just the aggregate state. In the implementation provided:
            // if (c.hasActiveAccounts()) throw ...
            // So we must ensure the command carries the state that triggers the failure if that's the check,
            // or that the aggregate state triggers the failure.
            // The implementation: "if (c.hasActiveAccounts()) ..." -> The command controls the failure.
            // So for the "Violates active accounts" scenario, we pass true in the command.

            boolean activeAccountsFlag = aggregate.getHasActiveAccounts();

            this.command = new UpdateCustomerDetailsCmd(
                    aggregate.id(),
                    aggregate.getFullName() != null ? aggregate.getFullName() : "New Name", // Preserve or use new
                    aggregate.getEmail() != null ? aggregate.getEmail() : "new@example.com",
                    aggregate.getGovernmentId() != null ? aggregate.getGovernmentId() : "GOV-NEW",
                    "10-20-30", // Valid SortCode
                    activeAccountsFlag
            );

            // Special override for the specific "violates" scenarios to trigger the specific domain error paths.
            // The domain logic checks:
            // 1. c.hasActiveAccounts() (Active Accounts)
            // 2. c.email() format (Email/GovID)
            // 3. c.fullName() format (Name/DoB)

            if (aggregate.getFullName() == null || aggregate.getFullName().isBlank()) {
                // Trigger Name validation error
                 this.command = new UpdateCustomerDetailsCmd(aggregate.id(), "", aggregate.getEmail(), aggregate.getGovernmentId(), "10-20-30", false);
            }
            if (aggregate.getEmail() == null || !aggregate.getEmail().contains("@")) {
                // Trigger Email validation error
                 this.command = new UpdateCustomerDetailsCmd(aggregate.id(), aggregate.getFullName(), aggregate.getEmail(), aggregate.getGovernmentId(), "10-20-30", false);
            }
            // The "Active Accounts" logic in the execute method uses the command's boolean.
            // The Given step set the aggregate's boolean to true. We pass it along.

            this.resultingEvents = aggregate.execute(command);
            this.capturedException = null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
            this.resultingEvents = null;
        }
    }

    // --- Then Steps ---

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNotNull(resultingEvents, "Events should not be null");
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertEquals("customer.details.updated", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain exception to be thrown");
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException"
        );
    }
}
