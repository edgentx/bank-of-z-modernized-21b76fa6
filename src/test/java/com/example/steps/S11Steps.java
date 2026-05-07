package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class S11Steps {

    private Transaction aggregate;
    private String accountNumber;
    private BigDecimal amount;
    private Currency currency;
    private Exception thrownException;
    private WithdrawalPostedEvent lastEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Setup a valid aggregate state (e.g., created but not posted)
        aggregate = new Transaction();
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_with_invalid_amount() {
        aggregate = new Transaction();
        amount = BigDecimal.ZERO;
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_is_already_posted() {
        aggregate = new Transaction();
        aggregate.markAsPosted(); // Simulate posted state
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_causes_overdraft() {
        aggregate = new Transaction();
        // Setup aggregate such that the withdrawal would exceed balance (simulated)
        aggregate.setAvailableBalance(BigDecimal.TEN); // 10
        amount = new BigDecimal("100.00"); // Withdraw 100
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        this.accountNumber = "ACC-12345";
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("50.00");
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = Currency.getInstance("USD");
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        try {
            PostWithdrawalCmd cmd = new PostWithdrawalCmd(accountNumber, amount, currency);
            lastEvent = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(lastEvent);
        assertEquals(accountNumber, lastEvent.accountNumber());
        assertEquals(amount, lastEvent.amount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof DomainViolationException);
    }
}
