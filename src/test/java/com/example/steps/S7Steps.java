package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("ACC-123");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number initialized in previous step
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            // The command must match the aggregate ID (immutable invariant check)
            CloseAccountCmd cmd = new CloseAccountCmd(account.id());
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        Assertions.assertEquals("account.closed", resultEvents.get(0).type());
        Assertions.assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        account = new AccountAggregate("ACC-VIOLATE-BAL");
        account.setBalance(new BigDecimal("100.50")); // Balance > 0
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        account = new AccountAggregate("ACC-VIOLATE-STATUS");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.SUSPENDED); // Not active
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        account = new AccountAggregate("ACC-ORIG");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNull(resultEvents); // No events should be emitted
        Assertions.assertNotNull(capturedException);
        // Validating it's a specific domain error (IllegalStateException or similar)
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }

    // Override execution for the immutability violation scenario specifically
    @When("the CloseAccountCmd command is executed with mismatched ID")
    public void the_close_account_cmd_command_is_executed_with_mismatched_id() {
        try {
            // Intentionally using a different account number than the aggregate ID
            // to simulate the immutability violation
            CloseAccountCmd cmd = new CloseAccountCmd("DIFFERENT-ID");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void setupImmutableViolation() {
         // Using the method above to trigger the specific error case
         a_account_aggregate_that_violates_immutability();
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_executed_immutable() {
        // Delegate to the mismatched ID version for this specific scenario context
        // Note: In Cucumber, we can't overload When easily. We handle logic inside steps.
        // Ideally, the feature file is specific. Assuming the user wants the general trigger.
        // We will check the context in the step definition if needed, or add a specific step.
        // For this implementation, let's refine the "violates immutability" step to look like the general one.
        
        // However, to pass the generic Gherkin, we need to interpret the state.
        // If the account is "ACC-ORIG", and we try to close it, we normally pass.
        // The violation description implies we might be trying to change the number 
        // or the command targets the wrong object.
        // Let's assume the test framework setup injects the mismatch via the 'execute' step.
        
        // Re-using the generic execute:
        try {
             // To trigger the immutability error defined in the domain logic:
             // we must execute with a command that doesn't match the aggregate ID.
             CloseAccountCmd cmd = new CloseAccountCmd("MISMATCHED-ID");
             resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
