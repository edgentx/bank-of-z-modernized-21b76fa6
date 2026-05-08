package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Simulating a valid, active account with zero balance
        account = new AccountAggregate("ACC-123", "Active", BigDecimal.ZERO);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is implicitly "ACC-123" from the Given step
        Assertions.assertNotNull(account.id());
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd(account.id());
            resultEvents = account.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals(AccountClosedEvent.class, resultEvents.get(0).getClass());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance_constraint() {
        // Simulate an account with a balance (cannot close)
        account = new AccountAggregate("ACC-HIGH-BAL", "Active", new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // Simulate a closed or inactive account
        account = new AccountAggregate("ACC-INACTIVE", "Closed", BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_unique_number() {
        // Simulate a command with a mismatched account number
        account = new AccountAggregate("ACC-REAL", "Active", BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception");
        // Depending on the specific violation, it could be IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }

    // Additional hook for the "unique number" scenario specifically modifying the command execution context
    @When("the CloseAccountCmd command is executed with a mismatched ID")
    public void the_close_account_cmd_command_is_executed_with_mismatched_id() {
        try {
            // Force a mismatch: Account ID is ACC-REAL, but command targets ACC-FAKE
            CloseAccountCmd cmd = new CloseAccountCmd("ACC-FAKE"); 
            account.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

}
