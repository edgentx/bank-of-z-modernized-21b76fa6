package com.example.steps;

import com.example.domain.shared.Command;
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
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private BigDecimal currentBalance;
    
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        this.aggregate = new TransactionAggregate("txn-123");
        this.aggregate.setPosted(false); // Ensure it is mutable
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount() {
        this.aggregate = new TransactionAggregate("txn-invalid-amount");
        this.aggregate.setPosted(false);
        this.amount = BigDecimal.ZERO;
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_immutability() {
        this.aggregate = new TransactionAggregate("txn-posted");
        this.aggregate.setPosted(true);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance() {
        this.aggregate = new TransactionAggregate("txn-low-balance");
        this.aggregate.setPosted(false);
        this.currentBalance = new BigDecimal("50.00");
        this.amount = new BigDecimal("100.00"); // Trying to withdraw more than balance
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.accountNumber = "ACC-001";
        if (this.amount == null) this.amount = new BigDecimal("10.00");
        if (this.currency == null) this.currency = "USD";
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("100.00");
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = "USD";
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_post_withdrawal_cmd_command_is_executed() {
        Command cmd = new PostWithdrawalCmd(
            aggregate.id(), 
            accountNumber != null ? accountNumber : "ACC-DEFAULT", 
            amount != null ? amount : BigDecimal.ZERO, 
            currency != null ? currency : "USD",
            currentBalance != null ? currentBalance : BigDecimal.ZERO
        );
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
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
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on implementation, it might be IllegalArgumentException, IllegalStateException, or a custom DomainException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
