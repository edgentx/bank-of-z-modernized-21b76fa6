package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Setup: Active, Zero Balance, Valid Number
        account = new AccountAggregate("ACC-123");
        // Hydrating the aggregate state to meet preconditions
        account.applyStateChange("ACC-123", BigDecimal.ZERO, "Active");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Implicit in the aggregate initialization above
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        account = new AccountAggregate("ACC-INVALID-BAL");
        // Setup: Active, Non-Zero Balance (Violates close condition)
        account.applyStateChange("ACC-INVALID-BAL", new BigDecimal("100.00"), "Active");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        account = new AccountAggregate("ACC-INACTIVE");
        // Setup: Inactive status
        account.applyStateChange("ACC-INACTIVE", BigDecimal.ZERO, "Inactive");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_unique_number() {
        // To simulate the 'immutable/unique' check failing via command validation,
        // we attempt to pass a null or blank account number in the command.
        account = new AccountAggregate("ACC-MUTABLE");
        account.applyStateChange("ACC-MUTABLE", BigDecimal.ZERO, "Active");
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            Command cmd;
            if ("ACC-MUTABLE".equals(account.id())) {
                // Invalid command scenario
                cmd = new CloseAccountCmd("");
            } else {
                cmd = new CloseAccountCmd(account.id());
            }
            resultEvents = account.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNull(resultEvents); // No events should be committed if rejected
        Assertions.assertNotNull(caughtException);
    }
}
