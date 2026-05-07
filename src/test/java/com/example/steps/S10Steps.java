package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private DepositPostedEvent lastEvent;
    private Throwable thrownException;

    // Scenario: Successfully execute PostDepositCmd

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        // Default to a new, unposted transaction which is valid for deposits
        this.transaction = new Transaction("txn-1");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is part of the command construction
        if (this.command == null) {
            this.command = new PostDepositCmd("acct-123", null, null);
        }
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (this.command != null) {
            this.command = new PostDepositCmd(command.accountNumber(), new BigDecimal("100.00"), command.currency());
        }
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (this.command != null) {
            this.command = new PostDepositCmd(command.accountNumber(), command.amount(), Currency.getInstance("USD"));
        }
    }

    // Scenario: PostDepositCmd rejected — Transaction amounts must be greater than zero

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount() {
        this.transaction = new Transaction("txn-2");
        // We will construct a command with invalid amount in the When step, or setup here
        this.command = new PostDepositCmd("acct-123", new BigDecimal("-50.00"), Currency.getInstance("USD"));
    }

    // Scenario: PostDepositCmd rejected — Transactions cannot be altered or deleted once posted

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_immutability() {
        this.transaction = new Transaction("txn-3");
        // Manually force the transaction to a POSTED state to simulate the violation context
        // In a real repo, this would be loaded from history.
        // Here we rely on a mechanism to simulate an already posted aggregate.
        // Assuming we have a way to hydrate it to POSTED state or we simply mark it posted via a backdoor or constructor variant.
        // For this test, let's assume we can hydrate it directly.
        // Alternatively, execute a valid command first to post it, then try to execute another.
        
        // Executing one valid command to post it
        transaction.execute(new PostDepositCmd("acct-123", BigDecimal.ONE, Currency.getInstance("USD")));
        
        // Now try to execute another command (which would be an alteration or new deposit on immutable log)
        // The AC says "Transactions cannot be altered...". If we treat Transaction as a ledger entry, you can't 'post' to it twice.
        this.command = new PostDepositCmd("acct-123", BigDecimal.TEN, Currency.getInstance("USD"));
    }

    // Scenario: PostDepositCmd rejected — A transaction must result in a valid account balance

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance_validation() {
        this.transaction = new Transaction("txn-4") {
            @Override
            protected void validateBalance(PostDepositCmd cmd) {
                // Override to simulate a domain validation failure (e.g., limit exceeded)
                throw new DomainException("Account balance validation failed: Limit exceeded.");
            }
        };
        this.command = new PostDepositCmd("acct-123", BigDecimal.valueOf(1000000), Currency.getInstance("USD"));
    }

    // Execution and Verification

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        // If command wasn't explicitly set in violation steps, set a default valid one
        if (this.command == null) {
            this.command = new PostDepositCmd("acct-123", BigDecimal.TEN, Currency.getInstance("USD"));
        }

        try {
            Object event = transaction.execute(this.command);
            if (event instanceof DepositPostedEvent) {
                this.lastEvent = (DepositPostedEvent) event;
            } else {
                this.lastEvent = null;
            }
        } catch (DomainException e) {
            this.thrownException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(this.lastEvent, "Expected a DepositPostedEvent to be emitted");
        assertEquals("deposit.posted", this.lastEvent.type());
        assertEquals(this.command.amount(), this.lastEvent.amount());
        assertEquals(this.command.accountNumber(), this.lastEvent.accountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(this.thrownException, "Expected a DomainException to be thrown");
        assertTrue(this.thrownException instanceof DomainException);
    }
}
