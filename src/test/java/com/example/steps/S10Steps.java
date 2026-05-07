package com.example.steps;

import com.example.domain.PostDepositCommand;
import com.example.domain.Transaction;
import com.example.domain.TransactionEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S10Steps {

    private Transaction transaction;
    private TransactionEvent resultEvent;
    private Exception capturedException;

    // Helpers
    private String validAccount = "ACC-12345";
    private BigDecimal validAmount = new BigDecimal("100.00");
    private Currency validCurrency = Currency.getInstance("USD");

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        transaction = new Transaction(UUID.randomUUID());
        assertFalse(transaction.isPosted());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // State stored for the when step
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // State stored
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // State stored
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            PostDepositCommand cmd = new PostDepositCommand(validAccount, validAmount, validCurrency);
            resultEvent = transaction.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(resultEvent, "Expected an event to be emitted");
        assertTrue(transaction.getUncommittedEvents().contains(resultEvent), "Event should be in uncommitted list");
        assertTrue(transaction.isPosted(), "Transaction should be marked as posted");
    }

    // Failure Scenarios

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        transaction = new Transaction(UUID.randomUUID());
        validAmount = BigDecimal.ZERO; // Violation
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_no_alterations_once_posted() {
        transaction = new Transaction(UUID.randomUUID());
        // Manually set state to simulate already posted
        transaction.markPosted(); 
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        transaction = new Transaction(UUID.randomUUID());
        validAmount = new BigDecimal("-100.00"); // Negative amount might trigger balance validation logic
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || 
                   capturedException instanceof IllegalStateException, 
                   "Expected valid domain exception type");
        assertNull(resultEvent, "No event should be emitted on rejection");
    }
}
