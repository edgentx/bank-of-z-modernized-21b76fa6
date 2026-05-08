package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private Exception caughtException;
    private String accountNumber = "ACC-123";

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("ACC-123");
        aggregate.openAccount("ACC-123", "Active"); // Programmatically setting valid state
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // accountNumber defaults to "ACC-123"
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance_constraint() {
        aggregate = new AccountAggregate("ACC-123");
        aggregate.openAccount("ACC-123", "Active");
        aggregate.setBalance(100); // Non-zero balance
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status_constraint() {
        aggregate = new AccountAggregate("ACC-123");
        aggregate.openAccount("ACC-123", "Dormant"); // Not Active
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("ACC-123");
        aggregate.openAccount("ACC-123", "Active");
        // Simulate a scenario where the command ID mismatches the aggregate ID
        accountNumber = "ACC-MISMATCH"; 
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            aggregate.execute(new CloseAccountCmd(accountNumber));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertEquals(1, aggregate.uncommittedEvents().size());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof AccountClosedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException || caughtException instanceof UnknownCommandException);
    }
}
