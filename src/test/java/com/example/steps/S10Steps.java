package com.example.steps;

import com.example.domain.transaction.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Aggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class S10Steps {

    private Aggregate aggregate;
    private PostDepositCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        String transactionId = UUID.randomUUID().toString();
        this.aggregate = new TransactionAggregate(transactionId);
        this.command = new PostDepositCmd(transactionId, "ACC-123", new BigDecimal("100.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_with_invalid_amount() {
        String transactionId = UUID.randomUUID().toString();
        this.aggregate = new TransactionAggregate(transactionId);
        this.command = new PostDepositCmd(transactionId, "ACC-123", new BigDecimal("-50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_is_already_posted() {
        String transactionId = UUID.randomUUID().toString();
        this.aggregate = new TransactionAggregate(transactionId);
        // First, post a valid transaction to put it in the POSTED state
        PostDepositCmd initialCmd = new PostDepositCmd(transactionId, "ACC-123", new BigDecimal("100.00"), "USD");
        this.aggregate.execute(initialCmd);
        // Clear events from the setup step
        ((TransactionAggregate) this.aggregate).clearEvents();
        
        // Now, try to post again on the same aggregate ID
        this.command = new PostDepositCmd(transactionId, "ACC-123", new BigDecimal("50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance_constraints() {
        String transactionId = UUID.randomUUID().toString();
        // Constructing an aggregate that will fail balance validation
        this.aggregate = new TransactionAggregate(transactionId, true);
        this.command = new PostDepositCmd(transactionId, "ACC-123", new BigDecimal("100.00"), "USD");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Default command setup uses ACC-123, which is valid.
        // Kept for Gherkin readability compliance.
        Assertions.assertNotNull(command.accountNumber());
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Default command setup uses 100.00, which is valid.
        Assertions.assertTrue(command.amount().compareTo(BigDecimal.ZERO) > 0);
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Default command setup uses USD, which is valid.
        Assertions.assertNotNull(command.currency());
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        try {
            this.resultEvents = this.aggregate.execute(this.command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            this.capturedException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("deposit.posted", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}