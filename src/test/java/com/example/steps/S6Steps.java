package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate account;
    private Exception capturedException;
    private List<DomainException> events;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        account = new AccountAggregate("ACC-001");
        account.setBalance(new BigDecimal("500.00"));
        account.setStatus(AccountAggregate.Status.ACTIVE);
        account.setAccountType(AccountAggregate.AccountType.CHECKING);
        capturedException = null;
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled in setup
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // Handled in execution
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        account = new AccountAggregate("ACC-LOW-BAL");
        account.setBalance(new BigDecimal("50.00")); // Below savings minimum of 100
        account.setStatus(AccountAggregate.Status.ACTIVE);
        account.setAccountType(AccountAggregate.AccountType.SAVINGS);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status_requirement() {
        account = new AccountAggregate("ACC-FROZEN");
        account.setStatus(AccountAggregate.Status.FROZEN); // Not Active
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        // Setup valid aggregate
        account = new AccountAggregate("ACC-IMMUTABLE");
        account.setStatus(AccountAggregate.Status.ACTIVE);
        // The violation will be simulated by sending a command with a different account number
        // than the aggregate ID during execution.
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        Command cmd;
        
        // Determine command parameters based on scenario context (implied by Given steps)
        if (account.getId().equals("ACC-IMMUTABLE")) {
            // Scenario 4: Immutability violation -> sending wrong ID in command
            cmd = new UpdateAccountStatusCmd("DIFFERENT-ID", AccountAggregate.Status.CLOSED);
        } else if (account.getId().equals("ACC-FROZEN")) {
            // Scenario 3: Status violation -> try to update from FROZEN
            cmd = new UpdateAccountStatusCmd("ACC-FROZEN", AccountAggregate.Status.CLOSED);
        } else if (account.getId().equals("ACC-LOW-BAL")) {
            // Scenario 2: Balance violation -> try to close (logic triggers balance check)
            cmd = new UpdateAccountStatusCmd("ACC-LOW-BAL", AccountAggregate.Status.CLOSED);
        } else {
            // Scenario 1: Success
            cmd = new UpdateAccountStatusCmd("ACC-001", AccountAggregate.Status.FROZEN);
        }

        try {
            events = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("account.status.updated", events.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // The generic execution throws RuntimeException (IllegalStateException/IllegalArgumentException)
        // In a real app we might wrap these, but for now checking Exception is enough.
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
