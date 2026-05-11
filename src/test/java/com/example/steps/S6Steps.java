package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // Constructor creates an Active account
        account = new AccountAggregate("ACC-123", "Standard");
        // Simulate state to satisfy invariants if needed
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled in constructor or command setup
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // Will be used in the When step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        // Assuming we want to freeze a valid active account
        command = new UpdateAccountStatusCmd("ACC-123", "Frozen");
        try {
            resultEvents = account.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals("ACC-123", event.aggregateId());
        Assertions.assertEquals("Frozen", event.newStatus());
    }

    // Invariant: Balance check
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        account = new AccountAggregate("ACC-LOW", "Standard");
        // Manually violate state for testing purposes (via reflection or helper if exposed)
        // Since execute is the entry, we rely on the aggregate's internal state logic.
        // Here we simulate a state where the status update implies a financial breach or state conflict
        // However, the prompt implies the command rejection might be based on state.
        // Let's assume the aggregate protects status transitions that would violate balance rules.
        // Since we can't easily set internal state without setters, we assume the Command carries context 
        // or the Aggregate is hydrated in a state that allows us to test this. 
        // For this step definition, we invoke the command on a specific aggregate configuration.
        
        // Actually, the scenario says "violates: Account balance cannot drop...". 
        // This is usually a check during withdrawal. If applied to Status Update, 
        // maybe closing an account with negative balance?
        command = new UpdateAccountStatusCmd("ACC-LOW", "Closed");
    }

    // Invariant: Active status required for operations
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        account = new AccountAggregate("ACC-INACTIVE", "Standard");
        // Freeze it first
        account.execute(new UpdateAccountStatusCmd("ACC-INACTIVE", "Frozen"));
        // Now try to activate it back or do something that requires active state? 
        // The prompt implies the *command* is rejected. 
        // Let's assume we are trying to perform an operation on a Frozen account, 
        // OR we are changing status TO Active illegally (if precondition not met).
        // Based on the text "Account must be in Active status to process...", 
        // this sounds like a precondition check for a Transaction command. 
        // However, the story is S-6 (UpdateAccountStatus). 
        // Let's interpret: Trying to change status FROM Frozen to Active might fail if invariants aren't met?
        // Or, simpler: Trying to update status when current state prevents it.
        // We will execute a command that tries to set status to Active (simulating a re-activation attempt that fails).
        command = new UpdateAccountStatusCmd("ACC-INACTIVE", "Active");
    }

    // Invariant: Immutable Account Number
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_number_immutability() {
        account = new AccountAggregate("ACC-IMMUTABLE", "Standard");
        // Trying to update status with a different ID in the command than the aggregate ID
        command = new UpdateAccountStatusCmd("ACC-DIFFERENT", "Frozen");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Check it's not just a random exception, but a domain-related one (IllegalStateException, IllegalArgumentException)
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }
}
