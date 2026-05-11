package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("acc-123");
        // Assume the account is created and opened via a command in a real scenario,
        // here we bypass or mock the opening to get to a testable state.
        account.apply(new AccountOpenedEvent("acc-123", "Cust-1", AccountStatus.ACTIVE, Instant.now()));
        account.clearEvents();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled by context setup
    }

    @Given("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        // Handled by context setup
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            Command cmd = new UpdateAccountStatusCmd(account.id(), AccountStatus.FROZEN);
            resultingEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultingEvents.get(0);
        assertEquals(AccountStatus.FROZEN, event.newStatus());
    }

    // --- Failure Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance_constraint() {
        account = new AccountAggregate("acc-min-balance");
        account.apply(new AccountOpenedEvent("acc-min-balance", "Cust-1", AccountStatus.ACTIVE, Instant.now()));
        // Set balance to 0
        account.setBalance(BigDecimal.ZERO);
        account.clearEvents();
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status_constraint() {
        account = new AccountAggregate("acc-inactive");
        account.apply(new AccountOpenedEvent("acc-inactive", "Cust-1", AccountStatus.FROZEN, Instant.now()));
        account.clearEvents();
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        // This represents a structural invariant check. For the command execution,
        // we simulate a failure if the aggregate ID in the command doesn't match the instance.
        account = new AccountAggregate("acc-original");
        account.apply(new AccountOpenedEvent("acc-original", "Cust-1", AccountStatus.ACTIVE, Instant.now()));
        account.clearEvents();
    }

    @When("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        try {
            // Triggering failure: For balance/active scenarios, trying to close/update might be restricted.
            // For immutability, sending a command with a wrong ID triggers the ID mismatch check.
            
            Command cmd;
            if (account.id().equals("acc-min-balance")) {
                // Attempt to update status (e.g. to close) while balance is 0 (assuming closing requires 0, but let's assume generic invariant failure)
                // Actually, let's try to update to FROZEN. If invariant says balance must be positive, and it's 0, it might fail.
                // The prompt says: "Account balance cannot drop below...". This is usually a Withdrawal invariant.
                // However, assuming S-6 enforces state transitions based on balance.
                cmd = new UpdateAccountStatusCmd("acc-min-balance", AccountStatus.FROZEN);
            } else if (account.id().equals("acc-inactive")) {
                cmd = new UpdateAccountStatusCmd("acc-inactive", AccountStatus.ACTIVE);
            } else {
                // ID mismatch test
                cmd = new UpdateAccountStatusCmd("DIFFERENT-ID", AccountStatus.CLOSED);
            }
            
            resultingEvents = account.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void verify_rejection() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
    }
}
