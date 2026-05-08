package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private CloseAccountCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("ACC-123", "CHECKING");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        cmd = new CloseAccountCmd("ACC-123");
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals("ACC-123", resultEvents.get(0).aggregateId());
        assertEquals(AccountAggregate.Status.CLOSED, aggregate.getStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance_constraint() {
        aggregate = new AccountAggregate("ACC-999", "CHECKING");
        aggregate.credit(BigDecimal.TEN); // Non-zero balance
        cmd = new CloseAccountCmd("ACC-999");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on the exact invariant checked, it might be IllegalStateException or IllegalArgumentException.
        // The scenario description implies a domain invariant violation (IllegalStateException) or business rule (IllegalArgumentException).
        // Here we expect IllegalStateException due to non-zero balance.
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("ACC-888", "CHECKING");
        aggregate.execute(new CloseAccountCmd("ACC-888")); // Close it immediately
        cmd = new CloseAccountCmd("ACC-888"); // Try to close again
        caughtException = null; // Reset exception from first execute
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("ACC-777", "CHECKING");
        // Violate by providing a different account number in the command than the aggregate ID
        cmd = new CloseAccountCmd("ACC-DIFFERENT");
    }
}
