package com.example.steps;

import com.example.domain.transaction.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S10Steps {

    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to build a valid aggregate instance
    private TransactionAggregate createValidAggregate() {
        // Assuming TransactionId matches AccountNumber for this context or requires specific ID construction
        return new TransactionAggregate("TX-123");
    }

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.aggregate = createValidAggregate();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Context initialized, account number is part of command construction in 'When'
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Context initialized
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Context initialized
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Executing the valid command context
            Command cmd = new PostDepositCmd("ACC-1001", new BigDecimal("100.00"), "USD");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof DepositPostedEvent, "Event should be DepositPostedEvent");

        DepositPostedEvent event = (DepositPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("deposit.posted", event.type());
        Assertions.assertEquals("ACC-1001", event.accountNumber());
        Assertions.assertEquals(new BigDecimal("100.00"), event.amount());
        Assertions.assertEquals("USD", event.currency());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_transaction_amounts_must_be_greater_than_zero() {
        this.aggregate = createValidAggregate();
    }

    @When("the PostDepositCmd command is executed")
    public void the_cmd_is_executed_invalid_amount() {
        try {
            // Amount = 0
            Command cmd = new PostDepositCmd("ACC-1001", BigDecimal.ZERO, "USD");
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            this.caughtException = e;
        }
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        this.aggregate = createValidAggregate();
        // Execute a valid command first to set state to POSTED
        Command firstCmd = new PostDepositCmd("ACC-1001", new BigDecimal("50.00"), "USD");
        aggregate.execute(firstCmd);
    }

    @When("the PostDepositCmd command is executed")
    public void the_cmd_is_executed_already_posted() {
        try {
            // Attempt to modify/post again
            Command cmd = new PostDepositCmd("ACC-1001", new BigDecimal("100.00"), "USD");
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            this.caughtException = e;
        }
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        this.aggregate = new TransactionAggregate("TX-BAD-BALANCE");
    }

    @When("the PostDepositCmd command is executed")
    public void the_cmd_is_executed_invalid_balance() {
        try {
            Command cmd = new PostDepositCmd("ACC-1001", new BigDecimal("100.00"), "USD");
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            this.caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
        Assertions.assertNull(resultEvents, "No events should be emitted on rejection");
    }
}
