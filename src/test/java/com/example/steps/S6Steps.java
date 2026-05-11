package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate aggregate;
    private UpdateAccountStatusCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1: Successfully execute UpdateAccountStatusCmd
    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Valid state: Active, sufficient funds
        aggregate = new AccountAggregate("ACC-1001", "Active", new BigDecimal("500.00"), "SAVINGS");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is part of the aggregate construction, 
        // but we ensure the command matches it implicitly or logic handles it.
        // The command is constructed in the next step.
    }

    @And("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        // Scenario 1 uses a valid status transition, e.g., Frozen or Closed (valid command structure)
        command = new UpdateAccountStatusCmd("ACC-1001", "Frozen", new BigDecimal("500.00"), "SAVINGS", false);
    }

    // Scenario 2: Balance violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        // SAVINGS requires 100.00. Let's give it 50.00.
        aggregate = new AccountAggregate("ACC-1002", "Active", new BigDecimal("50.00"), "SAVINGS");
        // Command setup for the violation check (UpdateStatusCmd runs the invariant check)
        command = new UpdateAccountStatusCmd("ACC-1002", "Frozen", new BigDecimal("50.00"), "SAVINGS", false);
    }

    // Scenario 3: Active status violation
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // Aggregate is in 'Frozen' state, not 'Active'.
        aggregate = new AccountAggregate("ACC-1003", "Frozen", new BigDecimal("500.00"), "SAVINGS");
        command = new UpdateAccountStatusCmd("ACC-1003", "Closed", new BigDecimal("500.00"), "SAVINGS", false);
    }

    // Scenario 4: Immutable account number violation
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("ACC-1004", "Active", new BigDecimal("500.00"), "SAVINGS");
        // Setting the immutable flag to true in the command to simulate the violation scenario.
        // Since the command is a record, we set the flag to indicate the check logic should trigger.
        command = new UpdateAccountStatusCmd("ACC-1004", "Active", new BigDecimal("500.00"), "SAVINGS", true);
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals(command.newStatus(), event.getNewStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Verify it's the correct exception type (IllegalStateException is used for invariant violations)
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
