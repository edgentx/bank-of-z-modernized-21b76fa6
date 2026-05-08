package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {
    private Aggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.aggregate = new AccountAggregate("ACC-123");
        ((AccountAggregate)this.aggregate).setStatus(AccountStatus.ACTIVE);
        ((AccountAggregate)this.aggregate).setBalance(BigDecimal.valueOf(1000));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // No-op, implies using the ID from the aggregate
    }

    @Given("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        // No-op, used in the When step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("ACC-123", AccountStatus.FROZEN);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        Assertions.assertEquals("account.status.updated", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        this.aggregate = new AccountAggregate("ACC-LOW");
        ((AccountAggregate)this.aggregate).setBalance(BigDecimal.valueOf(-100.00)); // Negative balance
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        this.aggregate = new AccountAggregate("ACC-FRZ");
        ((AccountAggregate)this.aggregate).setStatus(AccountStatus.FROZEN); // Not active
        ((AccountAggregate)this.aggregate).setBalance(BigDecimal.valueOf(1000));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        // We simulate a violation by trying to issue a command for a different account number
        // than the aggregate ID, or trying to change the ID logic.
        // Here we create an aggregate with ID ACC-1.
        this.aggregate = new AccountAggregate("ACC-1");
        // The violation logic is in the Command/When step below.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException 
            || thrownException instanceof IllegalArgumentException);
    }

    @When("the UpdateAccountStatusCmd command is executed on the violating aggregate")
    public void the_update_account_status_cmd_command_is_executed_on_violating_aggregate() {
        try {
            UpdateAccountStatusCmd cmd;
            // Context-specific command construction based on the violation
            if (thrownException != null) {
                 // Reset for clean execution attempt
                 thrownException = null;
            }
            
            if (aggregate.id().equals("ACC-1")) {
                // Violation: Mismatched Account Number
                cmd = new UpdateAccountStatusCmd("ACC-CHANGED", AccountStatus.FROZEN);
            } else {
                // Standard command
                cmd = new UpdateAccountStatusCmd(aggregate.id(), AccountStatus.CLOSED);
            }
            
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}
