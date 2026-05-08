package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Setup a valid, active, zero-balance account
        account = new AccountAggregate("ACC-123");
        // We simulate the account being active and having zero balance via direct state or a hypothetical open command.
        // For this step, we assume the constructor sets up the basics, or we invoke a behavior that ensures it.
        // In this context, we'll rely on the aggregate's initial state or a hypothetical setup.
        // To make the steps pass, we might need to hydrate it. Let's assume a default active state for the 'valid' case.
        account.markAsActive(); // Helper method for test setup
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // "ACC-123" is already provided in the Given step
        assertNotNull(account.id());
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd(account.id());
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        account = new AccountAggregate("ACC-444");
        account.markAsActive();
        // Set balance to something > 0
        account.setBalanceForTest(BigDecimal.valueOf(100.50));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        account = new AccountAggregate("ACC-555");
        // Status defaults to CLOSED or INACTIVE. Just don't call markAsActive.
        // Or explicitly close it.
        // Default is CLOSED based on implementation logic (no Active -> Closed).
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // Simulate a null or blank ID scenario passed to the command
        // The aggregate itself might have an ID, but the command checks validity/uniqueness context
        account = new AccountAggregate("ACC-666");
        account.markAsActive();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Ideally check it's an IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
