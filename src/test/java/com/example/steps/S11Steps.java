package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private TransactionAggregate aggregate;
    private PostWithdrawalCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.aggregate = new TransactionAggregate("tx-123");
        this.aggregate.setCurrentAccountBalance(new BigDecimal("1000.00"));
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_gt_zero() {
        this.aggregate = new TransactionAggregate("tx-invalid-amount");
        this.aggregate.setCurrentAccountBalance(BigDecimal.ONE);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        this.aggregate = new TransactionAggregate("tx-already-posted");
        this.aggregate.setCurrentAccountBalance(BigDecimal.ONE);
        // Simulate aggregate state where posted is already true
        this.aggregate.markPosted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        this.aggregate = new TransactionAggregate("tx-insufficient-funds");
        // Set balance to 0, withdrawal attempt will be > 0
        this.aggregate.setCurrentAccountBalance(BigDecimal.ZERO);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // We defer command creation to the When block to control parameters
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        // Default valid parameters unless overridden by specific scenario logic context
        // Since Cucumber executes steps sequentially, we determine parameters here.
        String acc = "ACC-001";
        BigDecimal amt = new BigDecimal("50.00");
        String curr = "USD";

        // Adjust for specific invalid cases based on the context provided by the "Given" steps above
        // Checking "already posted" scenario: Amount could be valid, but state is invalid
        // Checking "amount <= 0" scenario: We need to pass invalid amount
        if (aggregate.id().equals("tx-invalid-amount")) {
            amt = BigDecimal.ZERO;
        }
        
        // Checking "balance" scenario: Amount valid, but balance insufficient
        if (aggregate.id().equals("tx-insufficient-funds")) {
            amt = new BigDecimal("100.00"); // Balance is 0
        }

        this.cmd = new PostWithdrawalCmd(acc, amt, curr);

        try {
            this.resultEvents = aggregate.execute(cmd);
            this.thrownException = null;
        } catch (Exception e) {
            this.thrownException = e;
            this.resultEvents = null;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof WithdrawalPostedEvent);
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) resultEvents.get(0);
        assertEquals("withdrawal.posted", event.type());
        assertEquals("ACC-001", event.accountNumber());
        assertEquals(new BigDecimal("50.00"), event.amount());
        assertEquals("USD", event.currency());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // The domain rules throw either IllegalStateException or IllegalArgumentException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}