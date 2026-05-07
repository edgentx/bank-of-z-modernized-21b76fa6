package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction aggregate;
    private PostDepositCmd command;
    private DomainError error;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        aggregate = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        if (command == null) command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (command == null) command = new PostDepositCmd();
        command.setAmount(new BigDecimal("100.00"));
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (command == null) command = new PostDepositCmd();
        command.setCurrency("USD");
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_with_invalid_amount() {
        aggregate = new Transaction(UUID.randomUUID());
        command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
        command.setAmount(BigDecimal.ZERO); // Violation
        command.setCurrency("USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_that_is_already_posted() {
        aggregate = new Transaction(UUID.randomUUID());
        // Simulate an already posted transaction
        aggregate.markAsPosted(); 
        
        command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
        command.setAmount(new BigDecimal("50.00"));
        command.setCurrency("USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_that_violates_balance_constraints() {
        aggregate = new Transaction(UUID.randomUUID());
        aggregate.setMaximumAllowedBalance(BigDecimal.ONE); // Set max limit to 1.00
        
        command = new PostDepositCmd();
        command.setAccountNumber("ACC-12345");
        command.setAmount(new BigDecimal("100.00")); // Violates max balance
        command.setCurrency("USD");
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        try {
            aggregate.execute(command);
        } catch (DomainError e) {
            this.error = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNull(error, "Expected no error, but got: " + error);
        Assertions.assertTrue(aggregate.hasUncommittedEvents());
        Assertions.assertTrue(aggregate.getUncommittedEvents().get(0) instanceof DepositPostedEvent);
        
        DepositPostedEvent event = (DepositPostedEvent) aggregate.getUncommittedEvents().get(0);
        Assertions.assertEquals(command.getAccountNumber(), event.getAccountNumber());
        Assertions.assertEquals(command.getAmount(), event.getAmount());
        Assertions.assertEquals(command.getCurrency(), event.getCurrency());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(error, "Expected a DomainError to be thrown");
    }
}
