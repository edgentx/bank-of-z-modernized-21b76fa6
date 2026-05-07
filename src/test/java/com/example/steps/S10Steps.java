package com.example.steps;

import com.example.domain.PostDepositCmd;
import com.example.domain.Transaction;
import com.example.domain.TransactionState;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction transaction;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private Exception capturedException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.transaction = new Transaction(UUID.randomUUID());
        Assertions.assertNotNull(transaction);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_positive() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.amount = BigDecimal.ZERO;
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_Transaction_aggregate_that_violates_immutability() {
        this.transaction = new Transaction(UUID.randomUUID());
        // Manually force state to POSTED to simulate the violation condition
        transaction.setState(TransactionState.POSTED); 
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_balance() {
        // Using a marker string or negative amount to simulate balance validation failure logic
        this.accountNumber = "INVALID_BALANCE_ACCOUNT";
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        this.accountNumber = "ACC-12345";
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("100.00");
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = "USD";
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Default values if not set by specific scenario steps
            if (amount == null) this.amount = new BigDecimal("100.00");
            if (accountNumber == null) this.accountNumber = "ACC-12345";
            if (currency == null) this.currency = "USD";
            if (transaction == null) this.transaction = new Transaction(UUID.randomUUID());

            PostDepositCmd cmd = new PostDepositCmd(transaction.getId(), accountNumber, amount, currency);
            transaction.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no error, but got: " + capturedException);
        Assertions.assertFalse(transaction.getUncommittedEvents().isEmpty(), "Expected events to be emitted");
        // Assuming the event type name or check
        Object event = transaction.getUncommittedEvents().get(0);
        Assertions.assertTrue(event.getClass().getSimpleName().contains("DepositPosted"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected a domain error exception");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || 
                              capturedException instanceof IllegalStateException,
                             "Expected domain violation exception");
    }
}