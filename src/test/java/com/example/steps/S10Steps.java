package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.DepositPostedEvent;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private TransactionAggregate aggregate;
    private PostDepositCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate("txn-123", "acc-456", BigDecimal.valueOf(1000));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled in When construction for simplicity
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled in When construction
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled in When construction
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        command = new PostDepositCmd("acc-456", BigDecimal.valueOf(100.00), "USD");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof DepositPostedEvent);
        assertEquals("deposit.posted", resultEvents.get(0).type());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_gt_zero() {
        aggregate = new TransactionAggregate("txn-invalid-amt", "acc-456", BigDecimal.valueOf(100));
    }

    @When("the PostDepositCmd command is executed with invalid amount")
    public void the_PostDepositCmd_command_is_executed_invalid_amt() {
        command = new PostDepositCmd("acc-456", BigDecimal.ZERO, "USD");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_already_posted() {
        aggregate = new TransactionAggregate("txn-already-posted", "acc-456", BigDecimal.valueOf(100));
        // Execute once to make it posted
        aggregate.execute(new PostDepositCmd("acc-456", BigDecimal.TEN, "USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_invalid_balance() {
        // Set current balance to -500 so any deposit pushes it further or stays invalid if logic was strictly positive,
        // but let's assume the logic allows deposits to recover balance unless we are checking strictly positive.
        // Actually, let's say the rule is "Balance must be strictly positive".
        // We will simulate an aggregate with a very low balance, and a valid deposit that should pass.
        // BUT, for the violation, we need to construct a scenario where it fails.
        // Let's assume the aggregate validates against an external constraint or internal constraint.
        // For this test, let's assume the aggregate has a negative balance and we attempt a transaction that would make it MORE negative? 
        // No, PostDeposit adds money.
        // Let's assume the aggregate has a positive balance, but we construct a command that fails the validation if we were checking subtraction.
        // Since this is a deposit, it adds funds. 
        // To trigger the failure, we can mock the internal state to be "Frozen" or similar, OR we rely on the code implementation.
        // The code implementation checks: `currentBalance + amount < 0`.
        // So we need a balance that is very negative.
        aggregate = new TransactionAggregate("txn-neg-bal", "acc-456", BigDecimal.valueOf(-10000));
        // Even with a deposit of 100, -10000 + 100 = -9900. 
        // The check `projectedBalance < 0` will trigger.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}