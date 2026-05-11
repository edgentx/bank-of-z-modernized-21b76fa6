package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-7: CloseAccountCmd
 */
public class S7Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<?> events;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("ACC-TEST-1");
        account.setBalance(BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // The account number is implicitly handled by the aggregate initialization in the previous step
        // or we can verify it exists.
        assertNotNull(account.id());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_non_zero_balance() {
        account = new AccountAggregate("ACC-TEST-BAL");
        account.setBalance(new BigDecimal("50.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_is_not_active() {
        account = new AccountAggregate("ACC-TEST-STAT");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.CLOSED);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_with_immutability_violation() {
        // This scenario represents an invariant check.
        // We create a valid aggregate, but the command will reference a different ID,
        // simulating an attempt to manipulate the wrong aggregate or violating immutability logic.
        account = new AccountAggregate("ACC-ORIG");
        account.setBalance(BigDecimal.ZERO);
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            // In the immutability violation scenario, we intentionally pass a different ID
            String id = (account.id().equals("ACC-ORIG")) ? "ACC-DIFFERENT" : account.id();
            CloseAccountCmd cmd = new CloseAccountCmd(id);
            events = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(events, "Events list should not be null");
        assertFalse(events.isEmpty(), "Events list should not be empty");
        assertTrue(events.get(0) instanceof AccountClosedEvent, "Event should be AccountClosedEvent");
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error (Exception) to be thrown");
        // Check for specific exception types or messages if necessary, but general exception catch works for BDD verification
    }
}
