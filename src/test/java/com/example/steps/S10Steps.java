package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@SpringBootTest
public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private DomainError error;
    private DepositPostedEvent event;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        // Create a valid, unposted transaction
        transaction = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number validation is mocked/in-memory
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Amount validation handled in command construction
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Currency validation handled in command construction
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        // Ensure command is initialized if not done by specific violation contexts
        if (command == null) {
             command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), Currency.getInstance("USD"));
        }
        try {
            event = transaction.execute(command);
        } catch (DomainError e) {
            this.error = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(event);
        Assertions.assertNotNull(event.getEventId());
        Assertions.assertEquals("ACC-123", event.getAccountNumber());
        Assertions.assertEquals(0, new BigDecimal("100.00").compareTo(event.getAmount()));
        Assertions.assertEquals("USD", event.getCurrency().getCurrencyCode());
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount() {
        transaction = new Transaction(UUID.randomUUID());
        command = new PostDepositCmd("ACC-123", new BigDecimal("-10.00"), Currency.getInstance("USD"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(error);
        Assertions.assertNotNull(error.getMessage());
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_violates_already_posted() {
        transaction = new Transaction(UUID.randomUUID());
        // Manually set posted state to simulate the violation scenario
        transaction.markPosted(); 
        command = new PostDepositCmd("ACC-123", new BigDecimal("10.00"), Currency.getInstance("USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance() {
        // Assuming logic where Account balance would overflow, simulated by a flag or mock setup
        transaction = new Transaction(UUID.randomUUID());
        transaction.setValidationOverride(true); // Simulate validation failure
        command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), Currency.getInstance("USD"));
    }
}
