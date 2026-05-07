package com.example.steps;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private TransactionAggregate aggregate;
    private PostDepositCmd cmd;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Initialize a fresh aggregate with a valid state (not posted)
        aggregate = new TransactionAggregate("txn-123");
        // Assume the aggregate is not in a posted state initially
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account number setup for the command
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Amount setup for the command
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Currency setup for the command
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        // Construct the command with valid details for the "Happy Path" scenario
        if (cmd == null) {
            cmd = new PostDepositCmd("acct-456", new BigDecimal("100.00"), "USD");
        }
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("deposit.posted", resultEvents.get(0).type());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount() {
        aggregate = new TransactionAggregate("txn-invalid-amount");
        cmd = new PostDepositCmd("acct-456", new BigDecimal("-50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_posted_immutable() {
        aggregate = new TransactionAggregate("txn-already-posted");
        // Simulate the aggregate being in a posted state
        aggregate.markPosted(); 
        cmd = new PostDepositCmd("acct-456", new BigDecimal("100.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        aggregate = new TransactionAggregate("txn-invalid-balance");
        // Simulate a state where adding this deposit would overflow or fail validation
        aggregate.setEnforceMaxBalance(true);
        cmd = new PostDepositCmd("acct-456", new BigDecimal("999999999.00"), "USD");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Verify it's an IllegalStateException or IllegalArgumentException as defined in the domain
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
