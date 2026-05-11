package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        String id = java.util.UUID.randomUUID().toString();
        account = new AccountAggregate(id);
        // Simulate opening an account to make it valid
        account.apply(new AccountOpenedEvent(id, "123456789", "CHECKING", Instant.now()));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled by default setup in 'a valid Account aggregate'
        // Or we can explicitly ensure state if needed
        assertNotNull(account.getAccountNumber());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        String id = java.util.UUID.randomUUID().toString();
        account = new AccountAggregate(id);
        account.apply(new AccountOpenedEvent(id, "987654321", "CHECKING", Instant.now()));
        // Set a non-zero balance to simulate the violation context (can't close if balance > 0)
        account.setBalance(new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        String id = java.util.UUID.randomUUID().toString();
        account = new AccountAggregate(id);
        account.apply(new AccountOpenedEvent(id, "111111111", "CHECKING", Instant.now()));
        // Set status to CLOSED or INACTIVE to violate the "Active" requirement
        account.setStatus(AccountAggregate.AccountStatus.CLOSED);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // In the context of an aggregate command execution, "uniqueness" is usually enforced by the repository/store.
        // However, for the aggregate to reject the command based on this, we can simulate a state where
        // the account number is null or invalid, or the command is mismatched.
        // Let's assume the aggregate is in a state where the accountNumber is missing (data integrity issue).
        String id = java.util.UUID.randomUUID().toString();
        account = new AccountAggregate(id);
        // Manually creating an account without triggering the full open event logic to leave state invalid
        // Note: In a real app, we might load an aggregate from history. Here we mock the state.
        // We'll force a null account number scenario by not fully initializing.
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE); // But number is null/default
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd(account.id(), account.getAccountNumber());
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        assertEquals("account.closed", event.type());
        assertEquals(account.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException or IllegalArgumentException depending on the specific check
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}