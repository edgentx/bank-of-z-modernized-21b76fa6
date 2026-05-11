package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate aggregate;
    private UpdateAccountStatusCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("ACC-123");
        aggregate.setStatus(AccountStatus.ACTIVE);
        aggregate.setBalance(new BigDecimal("500.00"));
        aggregate.setAccountType(AccountAggregate.AccountType.STANDARD);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in the command creation step
    }

    @And("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        // Handled in the command creation step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        // Default command setup for success scenario
        if (command == null) {
            command = new UpdateAccountStatusCmd("ACC-123", AccountStatus.FROZEN, new BigDecimal("500.00"));
        }
        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("account.status.updated", event.eventType());
        Assertions.assertEquals(AccountStatus.FROZEN, event.newStatus());
        Assertions.assertEquals("ACC-123", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_min_balance() {
        aggregate = new AccountAggregate("ACC-LOW");
        aggregate.setStatus(AccountStatus.ACTIVE);
        aggregate.setBalance(new BigDecimal("10.00")); // Violates Standard min 100.00
        aggregate.setAccountType(AccountAggregate.AccountType.STANDARD);
        // Command attempting to update status (or just exists)
        command = new UpdateAccountStatusCmd("ACC-LOW", AccountStatus.FROZEN, new BigDecimal("10.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("ACC-INACTIVE");
        aggregate.setStatus(AccountStatus.CLOSED); // Inactive
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setAccountType(AccountAggregate.AccountType.STANDARD);
        command = new UpdateAccountStatusCmd("ACC-INACTIVE", AccountStatus.ACTIVE, BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("ACC-ORIG");
        aggregate.setStatus(AccountStatus.ACTIVE);
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setAccountType(AccountAggregate.AccountType.STANDARD);
        // Command tries to change the number context (simulate mutation attempt)
        command = new UpdateAccountStatusCmd("ACC-NEW", AccountStatus.FROZEN, BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        // Optional: Check specific error messages based on scenario
    }

}
