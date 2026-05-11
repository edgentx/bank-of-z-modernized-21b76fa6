package com.example.steps;

import com.example.domain.account.model.Account;
import com.example.domain.account.model.AccountStatus;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryAccountRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private Account account;
    private final InMemoryAccountRepository repository = new InMemoryAccountRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        account = new Account();
        account.open("ACC-123", AccountStatus.ACTIVE, BigDecimal.ZERO);
        repository.save(account);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Implicitly handled by the account initialized above
    }

    @Given("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        // Implicitly handled by the command in the When step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(account.getAccountNumber(), AccountStatus.FROZEN);
            resultEvents = account.execute(cmd);
            // Commit changes
            repository.save(account);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("account.status.updated", resultEvents.get(0).type());
        assertEquals(AccountStatus.FROZEN, account.getStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance_constraint() {
        account = new Account();
        // Open with negative balance to simulate violating state
        account.open("ACC-BAD-BAL", AccountStatus.ACTIVE, new BigDecimal("-100.00"));
        repository.save(account);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status_constraint() {
        // For this story, we interpret the constraint such that if we are not active,
        // certain operations might be restricted. However, the UpdateAccountStatusCmd 
        // usually changes the status. 
        // To satisfy the scenario "UpdateAccountStatusCmd rejected", we simulate a condition
        // where the transition is invalid or the state prevents the command execution.
        
        // Let's assume the invariant implies we cannot update status if the account is already CLOSED.
        account = new Account();
        account.open("ACC-CLOSED", AccountStatus.CLOSED, BigDecimal.ZERO);
        repository.save(account);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        account = new Account();
        account.open("ACC-IMMUTABLE", AccountStatus.ACTIVE, BigDecimal.ZERO);
        repository.save(account);
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_command_is_executed_with_violations() {
        try {
            UpdateAccountStatusCmd cmd;
            if (account.getAccountNumber().equals("ACC-IMMUTABLE")) {
                // Try to update with a DIFFERENT account number (simulating immutability breach attempt)
                cmd = new UpdateAccountStatusCmd("DIFFERENT-NUM", AccountStatus.FROZEN);
            } else {
                // Standard command for other violations
                cmd = new UpdateAccountStatusCmd(account.getAccountNumber(), AccountStatus.FROZEN);
            }
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on the specific check, it could be IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
