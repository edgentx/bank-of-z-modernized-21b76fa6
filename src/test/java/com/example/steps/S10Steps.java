package com.example.steps;

import com.example.domain.PostDepositCmd;
import com.example.domain.Transaction;
import com.example.domain.DepositPostedEvent;
import com.example.domain.DomainError;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CucumberContextConfiguration
public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private DomainError capturedError;
    private DepositPostedEvent capturedEvent;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        this.transaction = new Transaction();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Default initialization, can be overridden in specific scenarios
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Default initialization
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Default initialization
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.transaction = new Transaction();
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("-100.00"), Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        this.transaction = new Transaction();
        // Simulate an already posted transaction by setting a flag or state
        this.transaction.markAsPosted(); 
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        this.transaction = new Transaction();
        // Simulate a condition where the balance would become invalid
        this.transaction.setAccountBalance(new BigDecimal("-100.00")); // Overdraft limit check
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), Currency.getInstance("USD"));
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        if (command == null) {
            // If not set in specific Given, initialize a valid one
            command = new PostDepositCmd("ACC-123", new BigDecimal("200.00"), Currency.getInstance("USD"));
        }

        try {
            Object result = transaction.execute(command);
            if (result instanceof DomainError) {
                capturedError = (DomainError) result;
            } else if (result instanceof DepositPostedEvent) {
                capturedEvent = (DepositPostedEvent) result;
            }
        } catch (Exception e) {
            fail("Unexpected exception during execution: " + e.getMessage());
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(capturedEvent, "Expected a DepositPostedEvent to be emitted");
        assertEquals("ACC-123", capturedEvent.getAccountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedError, "Expected a DomainError");
    }
}
