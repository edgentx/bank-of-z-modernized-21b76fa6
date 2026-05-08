package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-6: UpdateAccountStatusCmd.
 */
public class S6Steps {

    private AccountAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Scenario 1: Success ---

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("acct-123");
        aggregate.setAccountNumber("99-88-777");
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        aggregate.setBalance(BigDecimal.valueOf(5000.00));
        aggregate.setAccountType(AccountAggregate.AccountType.SAVINGS);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account number already set in "a valid Account aggregate"
        // In a real flow, we might construct the command here with the data.
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // Status will be provided in the When clause
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            var cmd = new UpdateAccountStatusCmd(
                    aggregate.id(),
                    "99-88-777", // Matching existing number
                    AccountAggregate.AccountStatus.FROZEN
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof AccountStatusUpdatedEvent, "Event must be AccountStatusUpdatedEvent");
        
        AccountStatusUpdatedEvent statusEvent = (AccountStatusUpdatedEvent) event;
        assertEquals("account.status.updated", statusEvent.type());
        assertEquals(AccountAggregate.AccountStatus.FROZEN, statusEvent.newStatus());
        assertEquals(AccountAggregate.AccountStatus.ACTIVE, statusEvent.oldStatus());
    }

    // --- Scenario 2: Balance Invariant ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("acct-low-balance");
        aggregate.setAccountNumber("11-22-333");
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        aggregate.setAccountType(AccountAggregate.AccountType.SAVINGS);
        // Balance below 100.00 (Savings Minimum)
        aggregate.setBalance(BigDecimal.valueOf(50.00));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Exception should be thrown");
        assertTrue(capturedException.getMessage().contains("minimum required balance"), 
                "Error message should mention minimum balance");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be domain error");
    }

    // --- Scenario 3: Active Status Required ---

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("acct-inactive");
        aggregate.setAccountNumber("44-55-666");
        aggregate.setStatus(AccountAggregate.AccountStatus.FROZEN); // Not Active
        aggregate.setBalance(BigDecimal.valueOf(1000.00));
        aggregate.setAccountType(AccountAggregate.AccountType.CHECKING);
    }

    // --- Scenario 4: Immutable Account Number ---

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("acct-immutable");
        aggregate.setAccountNumber("ORIGINAL-NUM");
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setAccountType(AccountAggregate.AccountType.CHECKING);
        // The command executed in When will try to change the number
    }

    // We need a distinct When/Then for the last two scenarios to trigger the specific error
    // The generic When/Then above works, but relies on specific Setup.
    // However, to support the generic Gherkin structure, the standard When/Then methods are sufficient
    // provided the setup method puts the aggregate in the right state.

    // Override When for Scenario 4 to test changing account number
    @When("the UpdateAccountStatusCmd command is executed with a different number")
    public void the_UpdateAccountStatusCmd_command_is_executed_with_different_number() {
        try {
            var cmd = new UpdateAccountStatusCmd(
                    aggregate.id(),
                    "DIFFERENT-NUM", // Trying to change the number
                    AccountAggregate.AccountStatus.ACTIVE
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
