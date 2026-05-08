package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate account;
    private String accountNumber;
    private AccountAggregate.AccountStatus newStatus;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.accountNumber = "ACC-123-456";
        // Default to SAVINGS with sufficient balance
        this.account = new AccountAggregate(accountNumber, AccountAggregate.AccountType.SAVINGS, new BigDecimal("500.00"));
        this.account.clearEvents(); // clear creation events if any
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // accountNumber is set in the previous step
        assertNotNull(accountNumber);
    }

    @And("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        this.newStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            Command cmd = new UpdateAccountStatusCmd(accountNumber, newStatus);
            resultingEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("account.status.updated", resultingEvents.get(0).type());
        assertTrue(resultingEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultingEvents.get(0);
        assertEquals(newStatus, event.newStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        this.accountNumber = "ACC-LOW-BAL";
        // Savings min balance is 100.00. Set to 50.
        this.account = new AccountAggregate(accountNumber, AccountAggregate.AccountType.SAVINGS, new BigDecimal("50.00"));
        // We try to close it
        this.newStatus = AccountAggregate.AccountStatus.CLOSED;
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("minimum required balance") || 
                   thrownException.getMessage().contains("must be in an Active status") ||
                   thrownException.getMessage().contains("Immutable"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        this.accountNumber = "ACC-NOT-ACTIVE";
        this.account = new AccountAggregate(accountNumber, AccountAggregate.AccountType.SAVINGS, new BigDecimal("500.00"));
        // Manually set to frozen for the test case context (simulating existing state)
        // Note: Normally we'd load from repo, but we are constructing for test.
        // Since constructor sets Active, we change it to FROZEN via a command or direct mutation if allowed for test setup.
        // Direct mutation for test setup simplicity:
        this.account.execute(new UpdateAccountStatusCmd(accountNumber, AccountAggregate.AccountStatus.FROZEN));
        this.account.clearEvents();

        // Now try to update to CLOSED (which is not ACTIVE)
        this.newStatus = AccountAggregate.AccountStatus.CLOSED;
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        this.accountNumber = "ACC-IMMUTABLE";
        this.account = new AccountAggregate(accountNumber, AccountAggregate.AccountType.SAVINGS, new BigDecimal("100.00"));
        
        // Provide a DIFFERENT account number in the command than the aggregate ID
        this.accountNumber = "DIFFERENT-ACC-NUM"; // Changing the context variable used by the Command constructor
        this.newStatus = AccountAggregate.AccountStatus.ACTIVE;
    }
}
