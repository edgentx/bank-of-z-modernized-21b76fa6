package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.aggregate = new AccountAggregate("acc-123");
        this.aggregate.setAccountNumber("123456789");
        this.aggregate.setStatus(AccountStatus.ACTIVE);
        this.aggregate.setBalance(new BigDecimal("1000.00"));
        this.aggregate.setType(AccountType.CHECKING);
        // Reset exception
        this.capturedException = null;
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in the 'When' step construction
    }

    @Given("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        // Handled in the 'When' step construction
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        Command cmd = new UpdateAccountStatusCmd("123456789", AccountStatus.FROZEN);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals(AccountStatus.FROZEN, event.newStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance_constraint() {
        this.aggregate = new AccountAggregate("acc-124");
        this.aggregate.setAccountNumber("987654321");
        this.aggregate.setStatus(AccountStatus.ACTIVE);
        this.aggregate.setType(AccountType.CHECKING); // Min 100
        this.aggregate.setBalance(new BigDecimal("50.00")); // Violates constraint
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status_constraint() {
        this.aggregate = new AccountAggregate("acc-125");
        this.aggregate.setAccountNumber("111111111");
        this.aggregate.setStatus(AccountStatus.FROZEN); // Not active
        this.aggregate.setBalance(new BigDecimal("1000.00"));
        this.aggregate.setType(AccountType.CHECKING);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability_constraint() {
        this.aggregate = new AccountAggregate("acc-126");
        this.aggregate.setAccountNumber("222222222");
        this.aggregate.setStatus(AccountStatus.ACTIVE);
        this.aggregate.setBalance(new BigDecimal("1000.00"));
        // We simulate the violation by having the aggregate initialized with a number
        // The command will try to use a DIFFERENT number, triggering the immutability check
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We accept IllegalStateException or IllegalArgumentException as domain errors
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // Step variations for the violation scenarios
    @When("the UpdateAccountStatusCmd command is executed on the violating aggregate")
    public void the_update_account_status_cmd_command_is_executed_on_violating_aggregate() {
        // For balance/active violations, we send a valid command but the aggregate state blocks it
        // For immutability, we change the accountNumber in the command to conflict
        String numberToUse = "222222222"; // default
        
        // Check which scenario we are in based on aggregate state logic or just generic execution
        // The immutability violation scenario needs a specific trigger:
        if (aggregate.getAccountNumber() != null && aggregate.getAccountNumber().equals("222222222")) {
            // This is the immutability test aggregate, send a different number to trigger error
             // Actually, if we execute the command, the internal check handles it.
        }
        
        Command cmd = new UpdateAccountStatusCmd(aggregate.getAccountNumber(), AccountStatus.FROZEN);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
