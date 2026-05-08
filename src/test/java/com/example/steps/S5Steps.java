package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private OpenAccountCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("acc-123");
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Handled in the When step for brevity, or we can build the command here
        // For this suite, we'll construct the full command in the 'When' step or use a builder pattern if needed.
        // Here we just ensure the context knows we are valid.
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Placeholder
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Placeholder
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Placeholder
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        // Default valid command if no specific violation context is set.
        // This handles the 'Successful' scenario primarily.
        if (cmd == null) {
            cmd = new OpenAccountCmd(
                "acc-123",
                "customer-1",
                "CHECKING",
                new BigDecimal("500.00"),
                "10-20-30"
            );
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        assertEquals("acc-123", event.aggregateId());
        assertEquals("account.opened", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("acc-fail-balance");
        // SAVINGS requires 100.00 (logic in Aggregate), so we provide less.
        cmd = new OpenAccountCmd(
            "acc-fail-balance",
            "customer-1",
            "SAVINGS",
            new BigDecimal("50.00"),
            "10-20-30"
        );
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // This invariant usually applies to Withdrawals/Transfers.
        // But strictly interpreting the feature: if we attempt to Open an account that is somehow already ACTIVE (replay issue?) or if the command implies an action on an inactive account.
        // Since 'OpenAccount' transitions from NONE -> ACTIVE, this specific invariant usually protects existing aggregates.
        // To make this scenario meaningful for 'OpenAccountCmd', we interpret it as trying to open an already open (Active) account.
        aggregate = new AccountAggregate("acc-active");
        
        // Simulate the account already being open (bypassing command to set state)
        // In a real test, we might replay an event, but here we can't easily mutate private state without reflection or a package-private setter.
        // We will rely on the aggregate logic: execute throws IllegalStateException if status != NONE.
        // BUT, we can't set status to ACTIVE without running a command. 
        // Workaround: The scenario is slightly misaligned with OpenAccount (which creates Active). 
        // We will assume the aggregate was initialized in a weird state, or we execute a valid command first.
        
        // We execute a valid command first to make it ACTIVE.
        OpenAccountCmd firstCmd = new OpenAccountCmd("acc-active", "c1", "CHECKING", BigDecimal.TEN, "sc");
        aggregate.execute(firstCmd);

        // Now we set the 'cmd' for the 'When' step to the violating one (trying to open again).
        cmd = new OpenAccountCmd("acc-active", "c1", "CHECKING", BigDecimal.TEN, "sc");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // This is a hard one to test purely at the Aggregate level without a Repository.
        // The aggregate generates it internally. To violate this, we'd need the PRNG to collide, 
        // or an external invariant check.
        // We will simulate this by passing a Command that attempts to FORCE a specific account number 
        // if our design allowed it (it doesn't currently). 
        // OR, we assume the scenario implies trying to Open an account with an ID that exists.
        // Let's use the "Already Exists" flow for this one as well, treating the AccountID as the unique key in the aggregate.
        aggregate = new AccountAggregate("acc-dupe");
        
        OpenAccountCmd firstCmd = new OpenAccountCmd("acc-dupe", "c1", "CHECKING", BigDecimal.TEN, "sc");
        aggregate.execute(firstCmd);
        
        // Attempt to open again on the same Aggregate ID
        cmd = new OpenAccountCmd("acc-dupe", "c2", "SAVINGS", BigDecimal.ONE, "sc");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        // In Java domain, domain errors are often Exceptions (IllegalArgument, IllegalState)
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

}